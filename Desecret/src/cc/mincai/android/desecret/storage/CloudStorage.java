package cc.mincai.android.desecret.storage;

import java.util.List;

public interface CloudStorage {
    void connect();

    void disconnect();

    void uploadFile(String fromPath, String remoteFolderName);

    void downloadFile(String fromPath, String toPath);

    void deleteFile(String path);

    List<String> listFiles(String path);
}
