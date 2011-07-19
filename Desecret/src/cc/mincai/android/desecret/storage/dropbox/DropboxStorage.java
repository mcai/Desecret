/*******************************************************************************
 * Copyright (c) 2010-2011 by Min Cai (min.cai.china@gmail.com).
 *
 * This file is part of the FleximJ multicore architectural simulator.
 *
 * FleximJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FleximJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FleximJ. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.mincai.android.desecret.storage.dropbox;

import android.content.Context;
import cc.mincai.android.desecret.storage.CloudStorage;
import cc.mincai.android.desecret.util.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DropboxStorage extends DropboxAPI implements CloudStorage {
    private static final String DROPBOX_CLOUD_STORAGE_ROOT_DIR = "/cloudStorage";
    private static final String DROPBOX_ROOT_DIR = "dropbox";

    private String userName;
    private String password;

    private Context context;

    public DropboxStorage(Context context) {
        this(context, "min.cai.china@gmail.com", "bywwnss");
    }

    public DropboxStorage(Context context, String userName, String password) {
        this.context = context;
        this.userName = userName;
        this.password = password;
    }

    @Override
    public void login() {
        try {
            Config config = this.getConfig(this.context.getAssets().open("testing.json"), true);
            this.authenticate(config, this.userName, this.password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logout() {
        this.deauthenticate();
    }

    @Override
    public void uploadFile(String path) {
        try {
            this.client.putFile(DROPBOX_ROOT_DIR, DROPBOX_CLOUD_STORAGE_ROOT_DIR, this.context.getFileStreamPath(path));
        } catch (DropboxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void downloadFile(String fromPath, String toPath) {
        try {
            HttpResponse response = this.client.getFile(DROPBOX_ROOT_DIR, DROPBOX_CLOUD_STORAGE_ROOT_DIR + "/" + fromPath);
            IOUtils.copy(response.getEntity().getContent(), this.context.openFileOutput(toPath, Context.MODE_PRIVATE));
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
    public List<String> listFiles(String path, Predicate<String> fileNameFilter) {
        try {
            List<String> strs = new ArrayList<String>();

            DropboxAPI.Entry entry = new DropboxAPI.Entry(this.client.metadata(DROPBOX_ROOT_DIR, DROPBOX_CLOUD_STORAGE_ROOT_DIR + "/" + path, 10000, null, true, false, null));
            List<DropboxAPI.Entry> contents = entry.contents;
            if (contents != null) {
                for (DropboxAPI.Entry ent : contents) {
                    String fileName = ent.fileName();
                    if(fileNameFilter.apply(fileName)) {
                        strs.add(fileName);
                    }
                }
            }

            return strs;
        } catch (DropboxException e) {
            throw new RuntimeException(e);
        }
    }
}
