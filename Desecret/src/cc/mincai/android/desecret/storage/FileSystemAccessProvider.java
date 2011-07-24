package cc.mincai.android.desecret.storage;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileSystemAccessProvider {
    OutputStream openFileAsOutputStream(String fileName);
    InputStream openFileAsInputStream(String fileName);
    File getFile(String fileName);
    InputStream openEmbeddedFileAsInputStream(String fileName);
    void deleteFile(String fileName);
}
