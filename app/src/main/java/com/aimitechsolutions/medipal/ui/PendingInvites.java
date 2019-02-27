package com.aimitechsolutions.medipal.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.aimitechsolutions.medipal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingInvites extends Fragment {

    View fragV;

    public PendingInvites() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.pending_invites_frag, container, false);

        setHasOptionsMenu(true);  //use setHasOptionsMenu and override method opPrepareOptionsMenu to remove action menu options

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ReceivedInvites receivedInvites = new ReceivedInvites();
        ft.add(R.id.receive_container, receivedInvites);
        SentInvites sentInvites = new SentInvites();
        ft.add(R.id.sent_container, sentInvites);
        ft.commit();

        return fragV;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }
}
