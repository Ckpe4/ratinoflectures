package ua.at.ckpe4.labsforzpmp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ckpe4 on 15.05.2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, Constants.DB_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d(Constants.LOG_TAG, "--- onCreate database ---");
        /*db.execSQL("create table mytable ("
                + "id integer primary key autoincrement,"
                + "name text, "
                + "citizenship text, "
                + "academicstatus text, "
                + "acStDateText text, "
                + "degreeText text, "
                + "degreeDateText text, "
                + "pictureURL text"
                + ");");
        ContentValues cv = new ContentValues();
        for (int i = 0; i < 20; i++){
            cv.put("name", "name" + i + " lastname" + i);
            cv.put("citizenship", "україна");
            cv.put("academicstatus", "доцент");
            cv.put("acStDateText", "2007");
            cv.put("degreeText", "кандидат технічних nнаук, доктор технічних наук.");
            cv.put("degreeDateText", "2008");
            cv.put("pictureURL", "Basyuk_T_0.jpg");
            db.insert("mytable", null, cv);
            cv.clear();
        }*/

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(Constants.LOG_TAG, "--- onUpgrade database ---");
        //db.execSQL("drop table mytable");
        //onCreate(db);
    }
}
