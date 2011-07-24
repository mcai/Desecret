package cc.mincai.android.desecret.storage.dropbox;

import cc.mincai.android.desecret.storage.CloudStorage;
import cc.mincai.android.desecret.storage.FileSystemAccessProvider;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DropboxStorage extends DropboxAPI implements CloudStorage {
    private static final String DROPBOX_CLOUD_STORAGE_ROOT_DIR = "/cloudStorage";
    private static final String DROPBOX_ROOT_DIR = "dropbox";

    private String userName;
    private String password;

    private FileSystemAccessProvider fileSystemAccessProvider;

    public DropboxStorage(FileSystemAccessProvider fileSystemAccessProvider) {
        this(fileSystemAccessProvider, "min.cai.china@gmail.com", "bywwnss");
    }

    public DropboxStorage(FileSystemAccessProvider fileSystemAccessProvider, String userName, String password) {
        this.fileSystemAccessProvider = fileSystemAccessProvider;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public void connect() {
        this.authenticate(this.getConfig(this.fileSystemAccessProvider.openEmbeddedFileAsInputStream("testing.json"), true), this.userName, this.password);
    }

    @Override
    public void disconnect() {
        this.deauthenticate();
    }

    @Override
    public void uploadFile(String fromPath, String remoteFolderName) {
        try {
            this.client.putFile(DROPBOX_ROOT_DIR, DROPBOX_CLOUD_STORAGE_ROOT_DIR + "/" + remoteFolderName, this.fileSystemAccessProvider.getFile(fromPath));
        } catch (DropboxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void downloadFile(String fromPath, String toPath) {
        try {
            HttpResponse response = this.client.getFile(DROPBOX_ROOT_DIR, DROPBOX_CLOUD_STORAGE_ROOT_DIR + "/" + fromPath);
            IOUtils.copy(response.getEntity().getContent(), this.fileSystemAccessProvider.openFileAsOutputStream(toPath));
            response.getEntity().consumeContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (DropboxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFile(String path) {
        try {
            this.client.fileDelete(DROPBOX_ROOT_DIR, DROPBOX_CLOUD_STORAGE_ROOT_DIR + "/" + path, null);
        } catch (DropboxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> listFiles(String path) {
        try {
            List<String> strs = new ArrayList<String>();

            DropboxAPI.Entry entry = new DropboxAPI.Entry(this.client.metadata(DROPBOX_ROOT_DIR, DROPBOX_CLOUD_STORAGE_ROOT_DIR + "/" + path, 10000, null, true, false, null));
            List<DropboxAPI.Entry> contents = entry.contents;
            if (contents != null) {
                for (DropboxAPI.Entry ent : contents) {
                    strs.add(ent.fileName());
                }
            }

            return strs;
        } catch (DropboxException e) {
            throw new RuntimeException(e);
        }
    }
}
