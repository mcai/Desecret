package cc.mincai.android.desecret.storage.dropbox;

import org.apache.http.StatusLine;
import org.json.simple.JSONArray;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * High-level API for Dropbox functions that wraps the calls in DropboxClient.
 * @author tom
 *
 */
public class DropboxAPI {
    protected DropboxClient client;
    protected Config config;

    final public static int AUTH_STATUS_NONE = -1;
    final public static int AUTH_STATUS_FAILURE = 0;
    final public static int AUTH_STATUS_SUCCESS = 1;
    final public static int AUTH_STATUS_NETWORK_ERROR = 2;

    @SuppressWarnings("unchecked")
    protected DropboxClient getClient(Map config, Authenticator auth) {
        if (client != null) {
            return client;
        } else {
            client = new DropboxClient(config, auth);
            return client;
        }
    }

    @SuppressWarnings("unchecked")
    static private long getFromMapAsLong(Map map, String name) {
        Object val = map.get(name);
        long ret = 0;
        if (val != null && val instanceof Number) {
            ret = ((Number)val).longValue();
        }
        return ret;
    }
    @SuppressWarnings("unchecked")
    static private int getFromMapAsInt(Map map, String name) {
        Object val = map.get(name);
        int ret = 0;
        if (val != null && val instanceof Number) {
            ret = ((Number)val).intValue();
        }
        return ret;
    }
    @SuppressWarnings("unchecked")
    static private boolean getFromMapAsBoolean(Map map, String name) {
        Object val = map.get(name);
        if (val != null && val instanceof Boolean) {
            return ((Boolean)val).booleanValue();
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    static private void addToMapIfSet(Map map, String name, String val) {
        if (val != null) {
            map.put(name, val);
        }
    }
    @SuppressWarnings("unchecked")
    static private void addToMapIfSet(Map map, String name, long val) {
        if (val > -1) {
            map.put(name, val);
        }
    }

    public abstract class DropboxReturn {
        public int httpCode;
        public String httpReason;
        protected boolean hasError = false;

        @SuppressWarnings("unchecked")
        public DropboxReturn(Map map) {
            if (map != null) {
                Object error = map.get("ERROR");
                if (error != null && error instanceof StatusLine) {
                    hasError = true;
                    StatusLine status = (StatusLine)error;
                    httpCode = status.getStatusCode();
                    httpReason = status.getReasonPhrase();
                } else {
                    httpCode = 200;
                }
            }
        }

        @SuppressWarnings("unchecked")
        protected Map getBody(Map map) {
            if (map != null) {
                Object body = map.get("body");
                if (body != null && body instanceof Map) {
                    return (Map)body;
                } else if (hasError) {
                    return null;
                }
            }
            return map;
        }
    }

    public class Config extends DropboxReturn {
        public String consumerKey;
        public String consumerSecret;
        public String server;
        public String contentServer;
        public int port = -1;
        public String accessTokenKey;
        public String accessTokenSecret;
        public int authStatus = AUTH_STATUS_NONE;
        public String authDetail; 

        @SuppressWarnings("unchecked")
        public Config(Map map) {
            super(map);

            map = getBody(map);
            if (map != null) {
                consumerKey = (String)map.get("consumer_key");
                consumerSecret = (String)map.get("consumer_secret");
                server = (String)map.get("server");
                contentServer = (String)map.get("content_server");
                port = getFromMapAsInt(map, "port");
                accessTokenKey = (String)map.get("access_token_key");
                accessTokenSecret = (String)map.get("access_token_secret");
            }
        }

        @SuppressWarnings("unchecked")
        public Map toMap() {
            Map ret = new HashMap();

            addToMapIfSet(ret, "consumer_key", consumerKey);
            addToMapIfSet(ret, "consumer_secret", consumerSecret);
            addToMapIfSet(ret, "server", server);
            addToMapIfSet(ret, "content_server", contentServer);
            addToMapIfSet(ret, "port", port);
            addToMapIfSet(ret, "access_token_key", accessTokenKey);
            addToMapIfSet(ret, "access_token_secret", accessTokenSecret);
            return ret;
        }
    }

    public class Entry extends DropboxReturn {
        public long bytes;
        public String hash;
        public String icon;
        public boolean is_dir;
        public String modified;
        public String path;
        public String root;
        public String size;
        public String mime_type;
        public long revision;
        public boolean thumb_exists;

        public ArrayList<Entry> contents;

        @SuppressWarnings("unchecked")
        public Entry(Map map) {
            super(map);

            if (!hasError) {
                Map bmap = getBody(map);

                if (bmap != null) {
                    bytes = getFromMapAsLong(bmap, "bytes");

                    hash = (String)bmap.get("hash");
                    icon = (String)bmap.get("icon");

                    is_dir = getFromMapAsBoolean(bmap, "is_dir");

                    modified = (String)bmap.get("modified");
                    path = (String)bmap.get("path");
                    root = (String)bmap.get("root");
                    size = (String)bmap.get("size");
                    mime_type = (String)bmap.get("mime_type");

                    revision = getFromMapAsLong(bmap, "revision");

                    thumb_exists = getFromMapAsBoolean(bmap, "thumb_exists");

                    Object json_contents = bmap.get("contents");
                    if (json_contents != null && json_contents instanceof JSONArray) {
                        contents = new ArrayList<Entry>();
                        Object entry;
                        Iterator it = ((JSONArray)json_contents).iterator();
                        while (it.hasNext()) {
                            entry = it.next();
                            if (entry instanceof Map) {
                                contents.add(new Entry((Map)entry));                
                            }
                        }
                    }        
                }
            }
        }

        public String fileName() {
            String[] dirs = path.split("/");
            return dirs[dirs.length-1];
        }
    }

    // Cached
    @SuppressWarnings("unchecked")
    public Config getConfig(InputStream is, boolean refresh) {
        if (refresh || config == null) {
            try {
                Map map = Authenticator.loadConfig(is);
                config = new Config(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    // Used when we have to authenticate from scratch
    public Config authenticate(Config config, String username, String password) {
        TrustedAuthenticator auth;

        try {
            auth = new TrustedAuthenticator(config.toMap());

            boolean resp = auth.retrieveTrustedAccessToken(username, password);
            if (resp) {
                client = getClient(config.toMap(), auth);

                config.accessTokenKey = auth.getTokenKey();
                config.accessTokenSecret = auth.getTokenSecret();
                config.authStatus = AUTH_STATUS_SUCCESS;
            } else {
                config.authStatus = AUTH_STATUS_FAILURE;                                        
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            config.authStatus = AUTH_STATUS_NETWORK_ERROR;                                        
            config.authDetail = e.toString();
        }

        return config;
    }

    public void deauthenticate() {
        client = null;
    }
}
