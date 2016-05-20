package ua.at.ckpe4.labsforzpmp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Yaroslav on 17.05.2016.
 */
public class Updater extends Downloader {
    private String serverUrl;
    private String dbName;
    private Context context;

    public Updater(Context context, String serverUrl, String dbName) {
        super(context);
        this.context = context;
        this.serverUrl = serverUrl;
        this.dbName = dbName;
    }

    public void updateDatabase() {
        Log.d(Constants.LOG_TAG, "Updating database...");
        setDownloadPath(dbName + Constants.DB_EXTENSION);
        execute(serverUrl + Constants.SERVER_DB_FOLDER + dbName + Constants.DB_EXTENSION);
    }

    @Override
    protected String doInBackground(String... params) {
        super.doInBackground(params);
        copyDatabase();
        return null;
    }

    private void copyDatabase() {
        SQLiteOpenHelper helper = new SQLiteOpenHelper(context, dbName, null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
            }
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        };
        SQLiteDatabase db = helper.getReadableDatabase();
        db.close();
        OutputStream os = null;
        InputStream is = null;
        try {
            Log.d(Constants.LOG_TAG, "Copying DB from server version into app");
            is = context.openFileInput(Constants.DB_NAME + Constants.DB_EXTENSION);
            os = new FileOutputStream("/data/data/ua.at.ckpe4.labsforzpmp/databases/"
                    + Constants.DB_NAME);
            copyFile(os, is);
            Log.e(Constants.LOG_TAG, "Copied successfully.");
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, e.toString());
        } finally {
            try {
                if(os != null){
                    os.close();
                }
                if(is != null){
                    is.close();
                }
            } catch (IOException e) {
                Log.e(Constants.LOG_TAG, "Failed to close database.");
            }
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Log.e(Constants.LOG_TAG, "LOADED DB");
    }

    private void copyFile(OutputStream os, InputStream is) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while((length = is.read(buffer))>0){
            os.write(buffer, 0, length);
        }
        os.flush();
    }
}
