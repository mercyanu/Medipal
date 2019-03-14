package com.aimitechsolutions.medipal.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.model.ImageUploadDetails;

import java.util.ArrayList;

public class ImageUploadAdapter extends ArrayAdapter<ImageUploadDetails> {

    public ImageUploadAdapter(Context context, ArrayList<ImageUploadDetails> uploadDetailsList){
        super(context, 0, uploadDetailsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if(listView == null){
            listView = LayoutInflater.from(getContext()).inflate(R.layout.healthrecord_upload_detail, parent, false);
        }

        ImageUploadDetails currentDetail = getItem(position);

        TextView fileName = listView.findViewById(R.id.file_name);
        fileName.setText(currentDetail.getDescription());
        TextView date = listView.findViewById(R.id.date);
        date.setText(currentDetail.getDate());

        return listView;
    }
}
