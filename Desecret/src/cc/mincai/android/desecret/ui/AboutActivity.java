package cc.mincai.android.desecret.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Browser;
import android.view.KeyEvent;
import cc.mincai.android.desecret.R;

public class AboutActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash); //TODO: to be replaced with R.layout.about

        Cursor bookmarks = Browser.getAllBookmarks(getContentResolver());
        int urlColumn = bookmarks.getColumnIndex(Browser.BookmarkColumns.URL);

        String[] projection = new String[]
                {
                        BaseColumns._ID,
                        Browser.BookmarkColumns.URL,
                        Browser.BookmarkColumns.TITLE
                };

        Cursor results = managedQuery(Browser.BOOKMARKS_URI, projection, null, null, Browser.BookmarkColumns.URL + " ASC");


    }

    private boolean notContainsBookmark(String url) {
        Cursor results = Browser.getAllBookmarks(getContentResolver());
        int urlColumn = results.getColumnIndex(Browser.BookmarkColumns.URL);

        results.moveToFirst();

        do {
            if(results.getString(urlColumn).equals(url)) {
                return false;
            }
        }
        while (results.moveToNext());

        return true;
    }

    private void addBookmark(String url, String title) {
        ContentValues inputValues = new ContentValues();
        inputValues.put(Browser.BookmarkColumns.BOOKMARK, "1");
        inputValues.put(Browser.BookmarkColumns.URL, url);
        inputValues.put(Browser.BookmarkColumns.TITLE, title);

        Uri uri = getContentResolver().insert(Browser.BOOKMARKS_URI, inputValues);

        //Browser.saveBookmark(this, title, url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);

        //TODO

        return true;
    }
}

