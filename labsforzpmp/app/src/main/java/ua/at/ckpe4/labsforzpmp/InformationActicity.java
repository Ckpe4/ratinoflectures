package ua.at.ckpe4.labsforzpmp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class InformationActicity extends AppCompatActivity {

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_activity);

        Intent intent = getIntent();
        int item = intent.getIntExtra(MainActivity.EXTRA_MESSAGE, 0);
        TextView nameText = (TextView) findViewById(R.id.nameText);
        TextView acStText = (TextView) findViewById(R.id.acStText);
        TextView citizenshipText = (TextView) findViewById(R.id.citizenshipText);
        TextView acStDateText = (TextView) findViewById(R.id.acStDateText);
        TextView degreeText = (TextView) findViewById(R.id.degreeText);
        TextView degreeDateText = (TextView) findViewById(R.id.degreeDateText);

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query("mytable", null, "id = '" + Integer.toString(item)
                + "'", null, null, null, null);

        String pictureName = "";

        if (c.moveToFirst())
        {
            nameText.setText(c.getString(c.getColumnIndex("name")));
            acStText.setText(c.getString(c.getColumnIndex("academicstatus")));
            citizenshipText.setText(c.getString(c.getColumnIndex("citizenship")));
            acStDateText.setText(c.getString(c.getColumnIndex("acStDateText")));
            degreeText.setText(c.getString(c.getColumnIndex("degreeText")));
            degreeDateText.setText(c.getString(c.getColumnIndex("degreeDateText")));
            pictureName = c.getString((c.getColumnIndex("pictureURL")));
        }
        c.close();
        db.close();
        dbHelper.close();


        String fullPath = this.getFilesDir().getAbsolutePath();

        if (!pictureName.isEmpty()) {
            File img = new File(fullPath + "/" + pictureName);
            imageUri = Uri.fromFile(img);
            if (!img.exists()) {
                Downloader downloader = new Downloader(this, this);
                downloader.downloadFile(Constants.SERVER_URL + Constants.SERVER_PIC_FOLDER + pictureName, pictureName);
            } else {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }
            ((ImageView) findViewById(R.id.deviceImageView)).setImageURI(imageUri);
        }
    }
    public void refreshImage(){
        ((ImageView) findViewById(R.id.deviceImageView)).setImageURI(imageUri);
    }
}
