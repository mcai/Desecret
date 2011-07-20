package cc.mincai.android.desecret.newModel;

import java.util.List;

public class Model {
    //WelcomeActivity
    //
    //UserLoginActivity
    //	user: <user id>
    //	password: <password>
    //
    //UserRegisterActivity
    //	user: <user id>
    //	password: <password>
    //			  <reenter password>
    //	nickname: <nick name>
    //	gender:   <male or female>
    //	Birthday: <year-of-the-birth>
    //
    //HomePageActivity
    //	Messages -> popup window: reply; browse messages
    //	Contacts -> popup window: contact profile; browse activities; send message
    //
    //MessageBrowserActivity
    //ActivityBrowserActivity (can browse user's own or one contact's recent activities)

    public static class UserCredentials {
        private String id;
        private String password;
    }

    public static class Contact {
        private String id;
        private String displayName;
        private String thumbnailUrl;
    }

    public static class Group {
        private String id;
        private String title;
    }

    public static class Album {
        private String id;
        private String ownerId;
        private String title;
        private String description;
        private String thumbnailUrl;
    }

    public static enum MediaItemType {
        AUDIO,
        IMAGE,
        VIDEO
    }

    public static class MediaItem {
        private String id;
        private String albumId;
        private String url;
        private String thumbUrl;
        private MediaItemType type;
        private String title;
        private String description;
    }

    public static class Activity {
        private String id;
        private String body;
        private String title;
    }

    public static class Message {
        private String content;
        private List<String> recipients;
        private List<MediaItem> mediaItems;
    }
}
