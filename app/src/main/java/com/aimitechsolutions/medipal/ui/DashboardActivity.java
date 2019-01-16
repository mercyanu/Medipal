package com.aimitechsolutions.medipal.ui;

import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;

public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_drawer);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.hamburger_icon, getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerVisible(Gravity.START)) {
                    mDrawerLayout.closeDrawer(Gravity.START);
                } else {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            }
        });

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //handle click on navigation menu item
                        switch (menuItem.getItemId()){

                            case R.id.home:
                                Toast.makeText(DashboardActivity.this, "YOu have selected Home", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.appt:
                                Toast.makeText(DashboardActivity.this, "YOu have selected Appointment", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.consult:
                                Toast.makeText(DashboardActivity.this, "YOu have selected Consultation", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.record:
                                Toast.makeText(DashboardActivity.this, "YOu have selected Health Record", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.settings:
                            case R.id.logout:
                        }

                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START)) mDrawerLayout.closeDrawer(Gravity.START);
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            Toast.makeText(DashboardActivity.this, "YOu have selected Home", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(id == R.id.mediapal) Toast.makeText(DashboardActivity.this, "YOu have selected Home", Toast.LENGTH_SHORT).show();

        return super.onOptionsItemSelected(item);
    }
}
