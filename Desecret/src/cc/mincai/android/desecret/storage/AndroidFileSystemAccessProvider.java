package cc.mincai.android.desecret.storage;

import android.content.Context;

import java.io.*;

public class AndroidFileSystemAccessProvider implements FileSystemAccessProvider {
    private Context context;

    public AndroidFileSystemAccessProvider(Context context) {
        this.context = context;
    }

    @Override
    public OutputStream openFileAsOutputStream(String fileName) {
        try {
            return this.context.openFileOutput(fileName, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream openFileAsInputStream(String fileName) {
        try {
            return this.context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getFile(String fileName) {
        return this.context.getFileStreamPath(fileName);
    }

    @Override
    public InputStream openEmbeddedFileAsInputStream(String fileName) {
        try {
            return this.context.getAssets().open(fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        this.context.deleteFile(fileName);
    }
}
