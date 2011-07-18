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
package cc.mincai.android.desecret.storage;

import android.content.Context;
import cc.mincai.android.desecret.storage.dropbox.DropboxStorage;
import cc.mincai.android.desecret.util.Predicate;
import org.apache.commons.lang.time.StopWatch;

public class Pandolar {
    public static void test(Context context) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        CloudStorage cloudStorage = new DropboxStorage(context);
        cloudStorage.login();

        String fileName = "hello_world.txt";
//        String fileName = "gem5-77d12d8f7971.tar.bz2";

        for (int i = 0; i < 10; i++) {
            System.out.printf("#%d\n", i);

            cloudStorage.uploadFile(fileName);
            cloudStorage.downloadFile(fileName, fileName + ".fromTheCloud");

            cloudStorage.listFiles("", new Predicate<String>() {
                @Override
                public boolean apply(String param) {
                    return true;
                }
            });

            cloudStorage.deleteFile(fileName);
        }

        cloudStorage.deleteFile("");

        cloudStorage.logout();

        stopWatch.stop();

        System.out.println(stopWatch.getTime() / 1000);
    }
}
