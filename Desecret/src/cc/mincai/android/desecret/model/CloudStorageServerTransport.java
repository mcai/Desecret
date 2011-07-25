package cc.mincai.android.desecret.model;

import cc.mincai.android.desecret.storage.CloudStorage;
import cc.mincai.android.desecret.storage.FileSystemAccessProvider;
import cc.mincai.android.desecret.storage.dropbox.DropboxStorage;
import cc.mincai.android.desecret.util.Action2;
import cc.mincai.android.desecret.util.SerializationHelper;

import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CloudStorageServerTransport extends ServerTransport {
    private FileSystemAccessProvider fileSystemAccessProvider;

    private CloudStorage cloudStorage;

    private List<MessageContainer> pendingOutgoingMessageContainers;

    private ActivityEventContainer activityEventContainer;

    private List<String> targetUserIdsToMonitor;

    private List<String> polledActivityEventFileNames;

    private Timer timerContactCloudStorage;

    private List<String> userIds;
    private SimpleDateFormat timeFormat;
    private Comparator<String> fileNameTimePrefixComparator;

    public CloudStorageServerTransport(String userId, Action2<String, Message> onMessageReceivedCallback, Action2<String, ActivityEvent> onTargetActivityEventOcurredCallback, FileSystemAccessProvider fileSystemAccessProvider) {
        this(userId, onMessageReceivedCallback, onTargetActivityEventOcurredCallback, fileSystemAccessProvider, new DropboxStorage(fileSystemAccessProvider));
    }

    public CloudStorageServerTransport(String userId, Action2<String, Message> onMessageReceivedCallback, Action2<String, ActivityEvent> onTargetActivityEventOcurredCallback, FileSystemAccessProvider fileSystemAccessProvider, CloudStorage cloudStorage) {
        super(userId, onMessageReceivedCallback, onTargetActivityEventOcurredCallback);

        this.fileSystemAccessProvider = fileSystemAccessProvider;

        this.cloudStorage = cloudStorage;

        this.pendingOutgoingMessageContainers = new ArrayList<MessageContainer>();

        this.activityEventContainer = new ActivityEventContainer();

        this.targetUserIdsToMonitor = new ArrayList<String>();

        this.polledActivityEventFileNames = new ArrayList<String>();

        this.timerContactCloudStorage = new Timer();

        this.addTargetUserIdToMonitor(this.getUserId()); //TODO: test only, to be removed

        this.timeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        this.fileNameTimePrefixComparator = new Comparator<String>() {
            @Override
            public int compare(String s, String s1) {
                String str = s.substring(0, s.indexOf("_") - 1);
                String str1 = s1.substring(0, s.indexOf("_") - 1);

                try {
                    return timeFormat.parse(str).compareTo(timeFormat.parse(str1));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Override
    public void connect() {
        this.cloudStorage.connect();

        this.timerContactCloudStorage.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendPendingOutgoingMessages();

                pollNewIncomingMessages();
            }
        }, 0, UPDATE_INTERVAL);

        this.userIds = this.cloudStorage.listFiles("");

//        this.targetUserIdsToMonitor =
    }

    public static class UserCredentials {
        private String id;
        private String password;
        private List<String> targetUserIdsToMonitor;
    }

    @Override
    public void disconnect() {
        this.timerContactCloudStorage.cancel();

        this.cloudStorage.disconnect();
    }

    @Override
    public void setParam(String userId, String key, Object value) {
        this.push(value, userId + "/" + FOLDER_NAME_PARAMS + "/" + key);
    }

    @Override
    public <T> T getParam(Class<? extends T> clz, String userId, String key) {
        return this.pull(clz, userId + "/" + FOLDER_NAME_PARAMS + "/" + key, false);
    }

    @Override
    public void removeParam(String userId, String key) {
        this.remove(userId + "/" + FOLDER_NAME_PARAMS + "/" + key);
    }

    @Override
    public void fireActivityEvent(ActivityEvent event) {
        this.activityEventContainer.getEvents().add(event);
    }

    @Override
    public void addTargetUserIdToMonitor(String userId) { //TODO: persist
        if(!this.targetUserIdsToMonitor.contains(userId)) {
            this.targetUserIdsToMonitor.add(userId);
        }
    }

    @Override
    public void removeTargetUserIdToMonitor(String userId) { //TODO: persist
        if(this.targetUserIdsToMonitor.contains(userId)) {
            this.targetUserIdsToMonitor.remove(userId);
        }
    }

    @Override
    public void sendMessage(String to, Message message) {
        this.pendingOutgoingMessageContainers.add(new MessageContainer(this.getUserId(), to, message));
    }

    private void pollNewIncomingMessages() {
        List<String> newMessageFileNames = this.cloudStorage.listFiles(this.getUserId() + "/" + FOLDER_NAME_MESSAGES);
        Collections.sort(newMessageFileNames, this.fileNameTimePrefixComparator);

        for (String newMessageFileName : newMessageFileNames) {
            MessageContainer messageContainer = this.pull(MessageContainer.class, this.getUserId() + "/" + FOLDER_NAME_MESSAGES + "/" + newMessageFileName, true);
            this.onMessageReceived(messageContainer.getFrom(), messageContainer.getMessage());
        }

        for(String targetUserIdToMonitor : this.targetUserIdsToMonitor) {
            List<String> activityEventFileNames = this.cloudStorage.listFiles(targetUserIdToMonitor + "/" + FOLDER_NAME_ACTIVITY_EVENTS);
            Collections.sort(activityEventFileNames, this.fileNameTimePrefixComparator);

            for(String activityEventFileName : activityEventFileNames) {
                if(!this.polledActivityEventFileNames.contains(targetUserIdToMonitor + "/" + FOLDER_NAME_ACTIVITY_EVENTS + "/" + activityEventFileName)) {
                    ActivityEventContainer activityEventContainer = this.pull(ActivityEventContainer.class, targetUserIdToMonitor + "/" + FOLDER_NAME_ACTIVITY_EVENTS + "/" + activityEventFileName, false);
                    List<ActivityEvent> events = activityEventContainer.getEvents();

                    for(ActivityEvent event : events) {
                        this.onTargetActivityEventOccurred(targetUserIdToMonitor, event);
                    }

                    this.polledActivityEventFileNames.add(targetUserIdToMonitor + "/" + FOLDER_NAME_ACTIVITY_EVENTS + "/" + activityEventFileName);
                }
            }
        }
    }

    private void sendPendingOutgoingMessages() {
        if(!this.pendingOutgoingMessageContainers.isEmpty()) {
            for (MessageContainer messageContainer : this.pendingOutgoingMessageContainers) {
                this.push(messageContainer, messageContainer.getTo() + "/" + FOLDER_NAME_MESSAGES);
            }

            this.pendingOutgoingMessageContainers.clear();
        }

        if(!this.activityEventContainer.getEvents().isEmpty()) {
            this.push(this.activityEventContainer, this.getUserId() + "/" + FOLDER_NAME_ACTIVITY_EVENTS);

            this.activityEventContainer.getEvents().clear();
        }
    }

    public void push(Object obj, String remoteFolderName) {
        String localFileName = this.timeFormat.format(Calendar.getInstance().getTime()) + "_" + UUID.randomUUID().toString();

        PrintWriter pw = new PrintWriter(this.fileSystemAccessProvider.openFileAsOutputStream(localFileName));
        pw.write(SerializationHelper.serialize(obj));
        pw.flush();
        pw.close();

        this.cloudStorage.uploadFile(localFileName, remoteFolderName);

        this.fileSystemAccessProvider.deleteFile(localFileName);
    }

    public <T> T pull(Class<? extends T> clz, String remoteFileName, boolean deleteRemoteFile) {
        String localFileName = UUID.randomUUID().toString();

        this.cloudStorage.downloadFile(remoteFileName, localFileName);

        T obj = SerializationHelper.deserialize(this.fileSystemAccessProvider.openFileAsInputStream(localFileName), clz);

        if (deleteRemoteFile) {
            this.remove(remoteFileName);
        }

        this.fileSystemAccessProvider.deleteFile(localFileName);

        return obj;
    }

    public void remove(String remoteFileName) {
        this.cloudStorage.deleteFile(remoteFileName);
    }

    @Override
    public List<String> getUserIds() {
        return this.userIds;
    }

    private static final String FOLDER_NAME_MESSAGES = "messages";
    private static final String FOLDER_NAME_PARAMS = "params";
    private static final String FOLDER_NAME_ACTIVITY_EVENTS = "activityEvents";

    private static final long UPDATE_INTERVAL = 60000;
}
