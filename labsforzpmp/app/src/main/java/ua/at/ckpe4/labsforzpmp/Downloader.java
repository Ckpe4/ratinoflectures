package ua.at.ckpe4.labsforzpmp;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.Inflater;

/**
 * Created by Yaroslav on 17.05.2016.
 */
public class Downloader extends AsyncTask<String, Integer, String> {
    private Context context;
    private String filePath;
    private Activity activity;

    public Downloader(Context context) {
        this.context = context;
    }

    public Downloader(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            Log.d(Constants.LOG_TAG, "Downloading file...");
            URL url = new URL(params[0]);
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[50];
            int current;
            while((current = bis.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, current);
            }
            FileOutputStream fos = context.openFileOutput(filePath, Context.MODE_PRIVATE);
            fos.write(buffer.toByteArray());
            fos.close();
            Log.d(Constants.LOG_TAG, "File downloaded.");
        } catch (Exception e){
            Log.e(Constants.LOG_TAG, "Download error: " , e);
        }
        return null;
    }

   // @Override
    protected void onPostExecute(String s) {
        //Log.e(Constants.LOG_TAG, "LOADED FILE");
        activity.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        ((InformationActicity) activity).refreshImage();
    }

    public void setDownloadPath(String path) {
        filePath = path;
    }

    public void downloadFile(String url, String downloadPath) {
        setDownloadPath(downloadPath);
        execute(url);
    }
}
