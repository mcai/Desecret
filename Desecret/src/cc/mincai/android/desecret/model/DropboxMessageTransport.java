package cc.mincai.android.desecret.model;

import android.content.Context;
import cc.mincai.android.desecret.storage.CloudStorage;
import cc.mincai.android.desecret.storage.dropbox.DropboxStorage;
import cc.mincai.android.desecret.util.Predicate;
import cc.mincai.android.desecret.util.SerializationHelper;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class DropboxMessageTransport extends MessageTransport {
    private CloudStorage cloudStorage;

    private Timer timer = new Timer();

    public DropboxMessageTransport(Context context) {
        super(context);
        this.cloudStorage = new DropboxStorage(context);
    }

    @Override
    public void open() {
        this.cloudStorage.login();

        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pollNewMessages();
            }
        }, 0, UPDATE_INTERVAL);
    }

    @Override
    public void close() {
        this.cloudStorage.logout();
    }

    private void pollNewMessages() {
        List<String> newMessageFileNames = this.cloudStorage.listFiles("", new Predicate<String>() {
            @Override
            public boolean apply(String fileName) {
//                return fileName.startsWith(FILE_NAME_PREFIX_MESSAGE + getUserId() + "_"); //TODO
                return fileName.startsWith(FILE_NAME_PREFIX_MESSAGE);
            }
        });

        for (String newMessageFileName : newMessageFileNames) {
            this.cloudStorage.downloadFile(newMessageFileName, newMessageFileName);

            try {
                this.dispatch(new MessageReceivedEvent(SerializationHelper.deserialize(this.getContext().openFileInput(newMessageFileName), Message.class)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            this.cloudStorage.deleteFile(newMessageFileName);
        }
    }

    @Override
    public void sendMessage(Message message) {
        try {
            String fileName = FILE_NAME_PREFIX_MESSAGE + message.getEvent().getUserId() + "_" + UUID.randomUUID().toString();

            PrintWriter pw = new PrintWriter(this.getContext().openFileOutput(fileName, Context.MODE_PRIVATE));
            pw.write(SerializationHelper.serialize(message));
            pw.flush();
            pw.close();

            this.cloudStorage.uploadFile(fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String FILE_NAME_PREFIX_MESSAGE = "message_";

    private static final long UPDATE_INTERVAL = 5000;
}
