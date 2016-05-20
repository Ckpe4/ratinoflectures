package ua.at.ckpe4.labsforzpmp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Scanner;
import java.util.zip.Inflater;

/**
 * Created by Yaroslav on 17.05.2016.
 */
public class UpdateChecker extends AsyncTask<Void, Void, Integer> {

    public static final int UP_TO_DATE = 0;
    public static final int UPDATED = 1;
    Context context;
    private String dbInfoPath;
    private boolean toastsEnabled;

    UpdateChecker(Context context) {
        this.context = context;
        dbInfoPath = context.getFilesDir().getAbsolutePath() +"/" + Constants.DB_VERSION_FILE;
    }

    @Override
    protected Integer doInBackground(Void... arg0) {
        Log.d(Constants.LOG_TAG, "Checking for updates...");
        float serverDbVersion = 0;
        try {
            URL url = new URL(Constants.SERVER_URL + Constants.SERVER_DB_FOLDER
                    + Constants.DB_VERSION_FILE);
            Scanner s = new Scanner(url.openStream());
            serverDbVersion = Float.parseFloat(s.nextLine());
            Log.d(Constants.LOG_TAG, "Database version on server: " + serverDbVersion);
        } catch (IOException ex) {
            Log.d(Constants.LOG_TAG,ex.toString());
        }
        float localDbVersion = 0;
        try {
            localDbVersion = loadVersion();
            Log.d(Constants.LOG_TAG, "Local database version: " + localDbVersion);
            if (serverDbVersion > localDbVersion) {
                Log.d(Constants.LOG_TAG, "Updating database...");
                return updateDatabase(serverDbVersion);
            } else {
                Log.d(Constants.LOG_TAG, "Database is up to date.");
                return UP_TO_DATE;
            }
        } catch (Exception e) {
            try {
                Log.d(Constants.LOG_TAG, "Local database not found. Downloading...");
                return updateDatabase(serverDbVersion);
            } catch (Exception e1) {
                Log.d(Constants.LOG_TAG, "Failed to download database.");
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer i) {
        super.onPostExecute(i);
        if (toastsEnabled) {
            switch (i) {
                case UP_TO_DATE:
                    Toast.makeText(context, "No updates found", Toast.LENGTH_SHORT).show();
                    break;
                case UPDATED:
                    Toast.makeText(context, "Database updated", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private float loadVersion() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(dbInfoPath));
        float result = Float.parseFloat(reader.readLine());
        reader.close();
        return result;
    }

    private Integer updateDatabase(float serverDbVersion) {
        try {
            new Updater(context, Constants.SERVER_URL, Constants.DB_NAME).updateDatabase();
            PrintWriter writer = new PrintWriter(dbInfoPath, "UTF-8");
            writer.println(Float.toString(serverDbVersion));
            writer.close();
            Log.d(Constants.LOG_TAG, "Local database updated to version " + serverDbVersion + ".");
            return UPDATED;
        } catch (Exception e) {
            Log.d(Constants.LOG_TAG, e.toString());
        }
        return null;
    }

    public UpdateChecker enableToasts(boolean value) {
        toastsEnabled = value;
        return this;
    }
}
