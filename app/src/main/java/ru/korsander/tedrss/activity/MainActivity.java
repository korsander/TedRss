package ru.korsander.tedrss.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ru.korsander.tedrss.R;
import ru.korsander.tedrss.db.TedRssDBManager;
import ru.korsander.tedrss.fragment.ListFragment;
import ru.korsander.tedrss.fragment.OnFragmentInteractionListener;
import ru.korsander.tedrss.service.DownloadService;


public class MainActivity extends ActionBarActivity implements OnFragmentInteractionListener{

    private Toolbar toolbar;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getFragmentManager().findFragmentByTag(ListFragment.FRAGMENT_NAME) == null) {
            getFragmentManager().popBackStackImmediate(ListFragment.FRAGMENT_NAME, 0);
            getFragmentManager().beginTransaction().replace(R.id.container, ListFragment.newInstance("", ""), ListFragment.FRAGMENT_NAME).addToBackStack(ListFragment.FRAGMENT_NAME).commit();
            DownloadService.startLoad(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_backup:
                TedRssDBManager.exportDatabse();
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
