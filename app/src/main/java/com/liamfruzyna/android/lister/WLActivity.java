package com.liamfruzyna.android.lister;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.liamfruzyna.android.lister.Data.Data;
import com.liamfruzyna.android.lister.Data.IO;
import com.liamfruzyna.android.lister.Data.Util;
import com.liamfruzyna.android.lister.Data.WishList;
import com.liamfruzyna.android.lister.Fragments.DatesFragment;
import com.liamfruzyna.android.lister.Fragments.PeopleFragment;
import com.liamfruzyna.android.lister.Fragments.SettingsFragment;
import com.liamfruzyna.android.lister.Fragments.TagsFragment;
import com.liamfruzyna.android.lister.Fragments.WLFragment;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class WLActivity extends ActionBarActivity
{
    private String[] drawerTitles = {"Home", "Tag Viewer", "People Viewer", "Date Viewer", "Settings"};
    private DrawerLayout drawer;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;

    //main method that is run when app is started
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wl);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Data.setLists(IO.load());
                } catch (JSONException e)
                {
                    e.printStackTrace();
                } catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }

                //makes sure that lists isn't null
                if (Data.getLists() == null)
                {
                    Data.setLists(new ArrayList<WishList>());
                }

                Data.setUnArchived(Util.populateUnArchived());
            }
        }).start();

        //setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        drawerList.setAdapter(new ArrayAdapter<>(this, R.layout.navdrawer_list_item, drawerTitles));
        // Set the list's click listener
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                Fragment frag = new Fragment();
                        String tag = "";
                        switch (position)
                        {
                            case 0:
                                //Home
                                frag = new WLFragment();
                                tag = "WL";
                                break;
                            case 1:
                                //Tag Viewer
                                frag = new TagsFragment();
                                tag = "Tags";
                                break;
                            case 2:
                                //People Viewer
                                frag = new PeopleFragment();
                                tag = "People";
                                break;
                            case 3:
                                //Date Viewer
                                frag = new DatesFragment();
                                tag = "Dates";
                                break;
                            case 4:
                                //Settings
                                frag = new SettingsFragment();
                                tag = "Settings";
                                break;
                        }
                        changeFragment(frag, tag);


                drawerList.setItemChecked(position, true);
                drawer.closeDrawer(findViewById(R.id.drawer));
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        else
        {
            changeFragment(new WLFragment(), "WL");
        }


        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_menu_white_24dp, R.string.open, R.string.closed) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawer.setDrawerListener(drawerToggle);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 0:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    changeFragment(new WLFragment(), "WL");
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Fuck you, Lister needs that!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    public void setTitle(String title)
    {
        getSupportActionBar().setTitle(title);
    }

    public void changeFragment(Fragment fragment, String tag)
    {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(R.anim.abc_slide_in_top, R.anim.abc_fade_out, R.anim.abc_slide_in_bottom, R.anim.abc_fade_out);
        transaction.replace(R.id.container, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}