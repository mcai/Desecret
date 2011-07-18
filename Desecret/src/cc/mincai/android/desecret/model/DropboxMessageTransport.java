package cc.mincai.android.desecret.model;

import android.content.Context;
import cc.mincai.android.desecret.storage.CloudStorage;
import cc.mincai.android.desecret.storage.dropbox.DropboxStorage;
import cc.mincai.android.desecret.util.Predicate;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

public class DropboxMessageTransport extends MessageTransport {
    private CloudStorage cloudStorage;

    public DropboxMessageTransport(Context context) {
        super(context);
        this.cloudStorage = new DropboxStorage(context);
    }

    @Override
    public void open() {
        this.cloudStorage.login();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(;;) {
                    pollNewMessages();

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void close() {
        this.cloudStorage.logout();
    }

    private void pollNewMessages() {
        List<String> newMessageFileNames = this.cloudStorage.listFiles("", new Predicate<String>() {
            @Override
            public boolean apply(String fileName) {
                return fileName.startsWith(FILE_NAME_PREFIX_MESSAGE + getUserId() + "_");
            }
        });

        for(String newMessageFileName : newMessageFileNames) {
            this.cloudStorage.downloadFile(newMessageFileName, newMessageFileName);

            try {
                this.dispatch(new MessageReceivedEvent(Message.fromXml(this.getContext().openFileInput(newMessageFileName))));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            this.cloudStorage.deleteFile(newMessageFileName);
        }
    }

    @Override
    public void sendMessage(Message message) {
        try {
            String fileName = FILE_NAME_PREFIX_MESSAGE + message.getTo() + "_" + UUID.randomUUID().toString();

            PrintWriter pw = new PrintWriter(this.getContext().openFileOutput(fileName, Context.MODE_PRIVATE));
            pw.write(Message.toXml(message));
            pw.flush();
            pw.close();

            this.cloudStorage.uploadFile(fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String FILE_NAME_PREFIX_MESSAGE = "message_";
}
