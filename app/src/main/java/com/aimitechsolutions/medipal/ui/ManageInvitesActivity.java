package com.aimitechsolutions.medipal.ui;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.aimitechsolutions.medipal.R;

public class ManageInvitesActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_invites);

        Toolbar toolbar = findViewById(R.id.toolbar_invites);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SendInvite sendInvite = new SendInvite();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.invites_container, sendInvite);
        ft.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.invites_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
       switch(id){
           case android.R.id.home:
               finish();
               break;
           case R.id.pending_invites:
               //open frag to show invites sent and received
               PendingInvites pendingInvites = new PendingInvites();
               ft.replace(R.id.invites_container, pendingInvites);
               ft.addToBackStack("pending");
               ft.commit();
               break;

           default:
       }

        return super.onOptionsItemSelected(item);
    }
}
