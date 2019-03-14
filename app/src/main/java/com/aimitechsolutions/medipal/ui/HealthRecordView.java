package com.aimitechsolutions.medipal.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aimitechsolutions.medipal.R;

public class HealthRecordView extends Fragment {

    View fragV;
    ImageView editView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.healthrecord_view, container, false);

        editView = fragV.findViewById(R.id.edit_icon);
        editView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HealthRecordUpdate update = new HealthRecordUpdate();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.record_container, update);
                ft.addToBackStack("healthrecord");
                ft.commit();
            }
        });

        return fragV;
    }
}
