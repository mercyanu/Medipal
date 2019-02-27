package com.aimitechsolutions.medipal.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.model.FetchNow;
import com.aimitechsolutions.medipal.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DASHBOARD";
    private DrawerLayout mDrawerLayout;

    ImageView drawerImageView;
    TextView drawerUserView;
    TextView drawerCountryView;
    ImageView drawerEditImage;

    final FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_drawer);

        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(userID == null) Log.d(TAG, "No currentUser found");
        else Log.d(TAG, "User found:"+userID);

        DocumentReference documentReference = db.collection("users").document(userID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        User gottenUser = doc.toObject(User.class);
                        if(FetchNow.setCurrentUserDetails(gottenUser)){
                            Log.d(TAG, "Global variable now set");
                            setDrawerValues();
                        }
                    }
                    else Log.d(TAG, "Documents do not exist");
                }
                else Log.d(TAG, "User data not fetched due to:" +task.getException());
            }
        });


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

        View navView = navigationView.getHeaderView(0);
        drawerImageView = navView.findViewById(R.id.drawer_profile_image);
        drawerUserView = navView.findViewById(R.id.drawer_full_name);
        drawerCountryView = navView.findViewById(R.id.drawer_location);
        drawerEditImage = navView.findViewById(R.id.drawer_edit_details);
    }

    public void setDrawerValues(){
        //--this line for profile image if eventually used
        String docName = "Dr. " + FetchNow.getUserFirstName() + " " + FetchNow.getUserLastName();
        String patName = FetchNow.getUserFirstName() + " " + FetchNow.getUserLastName();
        Log.d(TAG, "Docname = "+ docName +" Patname = " + patName);
        if(FetchNow.getUserType() != null && FetchNow.getUserType().equals("A doctor"))
            drawerUserView.setText(docName);
        else if(FetchNow.getUserType() != null && FetchNow.getUserType().equals("Not a doctor"))
            drawerUserView.setText(patName);
        drawerCountryView.setText(FetchNow.getUserCountry());
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START)) mDrawerLayout.closeDrawer(Gravity.START);
        else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Log out of Medipal?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DashboardActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            Toast.makeText(DashboardActivity.this, "Settings Clickec", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(id == R.id.manage_requests) {
            //goto the manage requests activity
            Intent i = new Intent(this, ManageInvitesActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
