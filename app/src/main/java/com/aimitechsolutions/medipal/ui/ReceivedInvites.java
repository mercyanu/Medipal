package com.aimitechsolutions.medipal.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aimitechsolutions.medipal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceivedInvites extends Fragment {

    View fragV;

    public ReceivedInvites() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.received_invites_frag, container, false);

        return fragV;
    }

}
