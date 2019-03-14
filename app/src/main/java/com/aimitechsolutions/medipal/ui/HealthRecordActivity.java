package com.aimitechsolutions.medipal.ui;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.aimitechsolutions.medipal.R;

public class HealthRecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_record);

        Toolbar toolbar = findViewById(R.id.toolbar_record);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        HealthRecordView recordView = new HealthRecordView();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.record_container, recordView);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.record_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch(id){
            case android.R.id.home:
                finish();
                break;
            case R.id.health_upload:
                HealthRecordViewUploaded  uploaded = new HealthRecordViewUploaded();
                ft.replace(R.id.record_container, uploaded);
                ft.addToBackStack("uploaded");
                ft.commit();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}