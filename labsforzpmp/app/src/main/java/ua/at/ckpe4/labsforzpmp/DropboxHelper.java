package ua.at.ckpe4.labsforzpmp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by Yaroslav on 19.05.2016.
 */
public class DropboxHelper extends AsyncTask<Void, Void, Integer> {

    private Context context;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private static final int DOWNLOAD = 1;
    private static final int UPLOAD = 2;
    private int mode;
    private Runnable runnable;

    public DropboxHelper(Context context, DropboxAPI<AndroidAuthSession> mDBApi) {
        this.context = context;
        this.mDBApi = mDBApi;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            String s = "/data/data/org.intergalacticapps.mobdevdb/databases";
            Log.d(Constants.LOG_TAG, "MY: " + s);
            switch (mode) {
                case DOWNLOAD:
                    File file = new File(s + "/" + Constants.FAVORITES_DB);
                    Log.d(Constants.LOG_TAG, "FILE EXISTS " + file.exists());
                    FileOutputStream outputStream = new FileOutputStream(file);
                    DropboxAPI.DropboxFileInfo info = mDBApi.getFile(Constants.FAVORITES_DB, null, outputStream, null);
                    Log.d(Constants.LOG_TAG, "The file's rev is: " + info.getMetadata().rev);
                    break;
                case UPLOAD:
                    File file1 = new File(s + "/" + Constants.FAVORITES_DB);
                    FileInputStream inputStream = new FileInputStream(file1);
                    try {
                        mDBApi.delete(Constants.FAVORITES_DB);
                    } catch (Exception e) {}
                    DropboxAPI.Entry response = mDBApi.putFile(Constants.FAVORITES_DB, inputStream,
                            file1.length(), null, null);
                    Log.d(Constants.LOG_TAG, "The uploaded file's rev is: " + response.rev);
            }
        } catch (Exception e) {
            Log.d(Constants.LOG_TAG, e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer i) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public void runOnPostExecute(Runnable runnable) {
        this.runnable = runnable;
    }

    public void setDownloadMode() {
        mode = DOWNLOAD;
    }

    public void setUploadMode() {
        mode = UPLOAD;
    }
}
