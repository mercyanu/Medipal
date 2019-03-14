package com.aimitechsolutions.medipal.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.model.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceivedInvites extends Fragment {

    private final String TAG = "RECEIVEINVITES";
    View fragV;

    public ReceivedInvites() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.received_invites_frag, container, false);
        ArrayList<User> userList = (ArrayList<User>)this.getArguments().getSerializable("receive_user");
        if(userList != null){

            StringBuilder ids = new StringBuilder();
            for(User user : userList){
                String temmp = user.getUid()+" ";
                ids.append(temmp);
            }
            Toast.makeText(getActivity(), "You have thi now "+ids.toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "user list is not empty"+ids.toString());
        }else Log.d(TAG, "THE USERLIST has something");

        return fragV;
    }

}
