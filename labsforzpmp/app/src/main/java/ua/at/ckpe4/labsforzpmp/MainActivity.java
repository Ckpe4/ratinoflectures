package ua.at.ckpe4.labsforzpmp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String EXTRA_MESSAGE ="ua.at.ckpe4.labsforpzmp.MESSAGE";
    static DropboxAPI<AndroidAuthSession> mDBApi;
    static String accessToken;

    ListView lv;
    Button searchButton;
    ArrayList<String> list;
    ArrayAdapter<String> arrayAdapter;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new UpdateChecker(this).execute();

        lv = (ListView) findViewById(R.id.listView);

        list = new ArrayList<String>();

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                list );
        lv.setAdapter(arrayAdapter);

        dbHelper = new DBHelper(this);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor c = db.query("mytable", null, "name = ?;",
                        new String[]{(String) parent.getItemAtPosition(position)},
                        null, null, null);
                if (c.moveToFirst()){
                    int item = c.getInt(c.getColumnIndex("id"));
                    db.close();
                    Intent intent = new Intent(MainActivity.this, InformationActicity.class);
                    intent.putExtra(EXTRA_MESSAGE, item);
                    startActivity(intent);
                }
            }
        });
        sendMessage(findViewById(R.id.button));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AppKeyPair appKeys = new AppKeyPair(Constants.APP_KEY, Constants.APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        SharedPreferences settings = getSharedPreferences("settings", 0);
        accessToken = settings.getString("dropboxtoken", "DEFAULT");
        if(!accessToken.equals("DEFAULT")){
            mDBApi.getSession().setOAuth2AccessToken(accessToken);
        }
    }

    public void sendMessage(View view) {
        list.clear();
        EditText etName = (EditText) findViewById(R.id.editText);
        String nameString = etName.getText().toString();

        SQLiteDatabase db;
        db = dbHelper.getWritableDatabase();
        Cursor c = db.query("mytable", null, "name LIKE ?", new String[]{"%" + nameString + "%"},
                null, null, null);
        if (c.moveToFirst()) {
            int nameColIndex = c.getColumnIndex("name");
            do {
                list.add(c.getString(nameColIndex));
            }while (c.moveToNext());
        }
        lv.setAdapter(arrayAdapter);
        db.close();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (!accessToken.equals("DEFAULT")) {
            MenuItem loginItem = menu.findItem(R.id.action_login);
            loginItem.setTitle(R.string.action_logout);
        }
        return true;
    }
    public void commitSettings() {
        SharedPreferences settings = getSharedPreferences("settings", 0);
        ((TextView)findViewById(R.id.useremail)).setText(settings.getString("useremail", "Empty"));
        ((TextView)findViewById(R.id.useremail)).setText(settings.getString("username", "Not logged in"));
    }

    protected void onResume() {
        super.onResume();
        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                mDBApi.getSession().finishAuthentication();
                accessToken = mDBApi.getSession().getOAuth2AccessToken();
                SharedPreferences settings = getSharedPreferences("settings", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("dropboxtoken", accessToken);
                //editor.putString("name", mDBApi.accountInfo().displayName);
                //editor.putString("email", mDBApi.accountInfo().email);
                //editor.putString("uid", Long.toString(mDBApi.accountInfo().uid));
                editor.commit();
            } catch (IllegalStateException e) {
                Log.d(Constants.LOG_TAG, "Error authenticating", e);
            //} catch (com.dropbox.client2.exception.DropboxException ex) {
                //Log.d(Constants.LOG_TAG, "Error receiving data", ex);
            }
            commitSettings();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (!accessToken.equals("DEFAULT") && id == R.id.action_login) {
            item.setTitle(R.string.action_logout);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            new UpdateChecker(this).enableToasts(true).execute();
            return true;
        } else if (id == R.id.action_login) { //Log in and log out
            if (accessToken.equals("DEFAULT")) {
                mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
                commitSettings();
                item.setTitle(R.string.action_logout);
            } else {
                accessToken = "DEFAULT";
                mDBApi.getSession().setOAuth2AccessToken(MainActivity.accessToken);
                item.setTitle(R.string.action_login);
                commitSettings();
                Toast.makeText(this, "Log out successfully", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(MainActivity.this, CalcActivity.class);
            //intent.putExtra(EXTRA_MESSAGE, 7);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}