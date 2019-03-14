package com.aimitechsolutions.medipal.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.model.FetchNow;
import com.aimitechsolutions.medipal.model.ImageUploadDetails;
import com.aimitechsolutions.medipal.utils.CustomToast;
import com.aimitechsolutions.medipal.utils.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetImageDialog extends DialogFragment {

    private static final String TAG = "GETIMAGEDIALOG";
    private static final int CHOOSE_GALLERY_CODE = 01;
    private static final int CAPTURE_CAMERA_CODE = 02;

    //views
    View fragView;
    ProgressBar pg;
    Button upload;
    Button cancel;
    EditText descriptionView;
    TextView filenameView;

    String mDescription;
    String mCapturedImage;
    Uri mImageUri;
    String mDate;
    String uploadUri;
    //ArrayList<String> uploadDetails = new ArrayList<>();
    //Map<String, Object> uploadDetails = new HashMap<>();


    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragView = inflater.inflate(R.layout.get_image_dialog, container, false);
        getDialog().setCancelable(false);  getDialog().setCanceledOnTouchOutside(false);
        pg = fragView.findViewById(R.id.progressBar11);
        descriptionView = fragView.findViewById(R.id.description);
        filenameView = fragView.findViewById(R.id.file_name);

        Button selectGallery = fragView.findViewById(R.id.dialog_gallery);
        selectGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Getting image from gallery");
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, CHOOSE_GALLERY_CODE);
            }
        });

        Button openCamera= fragView.findViewById(R.id.dialog_camera);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Starting the camera");
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(cameraIntent.resolveActivity(getActivity().getPackageManager()) != null){
                    File imageFile = createImageFile();
                    if(imageFile != null) {
                        Uri imageUri = FileProvider.getUriForFile(getActivity(),
                                "com.aimitechsolutions.medipal.fileprovider",
                                imageFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(cameraIntent, CAPTURE_CAMERA_CODE);
                        Log.d(TAG, "The uri of that path is ->"+imageUri);
                    }else
                        Toast.makeText(getActivity(), "Image file is empty and not created", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel = fragView.findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCapturedImage = "";
                getDialog().dismiss();
            }
        });


        upload = fragView.findViewById(R.id.upload_button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                mDescription = descriptionView.getText().toString();
                if(!Validator.isEmpty(mDescription)){
                    if(mImageUri != null){
                        startUpload();
                    }
                    else CustomToast.displayToast(getContext(), fragView, "Please select or capture an image");
                }
                else CustomToast.displayToast(getContext(), fragView, "You have not entered a description");
            }
        });

        return fragView;
    }

    private File createImageFile(){
        String date = new SimpleDateFormat("dd_MM_yyyy").format(new Date());
        String imageName = "Upload_"+ date+"_";
        File fileDirectory = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try{
            File imageFile = File.createTempFile(imageName, ".jpg", fileDirectory);
            mCapturedImage = imageFile.getAbsolutePath(); //we have gotten the absolute image path of the image in this app's directory_pictures
            Log.d(TAG, "Captured image storeD in path ->"+ mCapturedImage);
            return imageFile;
        }
        catch(IOException e){
            Log.d(TAG, "Could not create file due to: "+ e.getCause());
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case CHOOSE_GALLERY_CODE:
                if(resultCode == Activity.RESULT_OK) {
                    mImageUri = data.getData();
                    filenameView.setText(getFileName(mImageUri));
                    Log.d(TAG, "Chosen ImageUri from Gallery:"+mImageUri);
                }
                break;
            case CAPTURE_CAMERA_CODE:
                if(resultCode == Activity.RESULT_OK){
                    mImageUri = Uri.fromFile(new File(mCapturedImage));
                    filenameView.setText(getFileName(mImageUri));
                    Log.d(TAG, "Captured image from Camera:"+ mImageUri);
                }
                break;
            default:
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
    result = result.substring(cut + 1);
}
        }
                return result;
                }

    private void startUpload(){
        pg.setVisibility(View.VISIBLE);
        final StorageReference storageReference = storageRef.child("uploads/"+ FetchNow.getUserId()+"/"+ getFileName(mImageUri));
        final UploadTask uploadTask = storageReference.putFile(mImageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        updateFirestore(uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pg.setVisibility(View.INVISIBLE);
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                        dialogBuilder.setIcon(R.drawable.red_tick)
                                .setTitle("Something went wrong!!!")
                                .setMessage("Could not upload content, please try again")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = dialogBuilder.create();
                        dialog.show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pg.setVisibility(View.INVISIBLE);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setIcon(R.drawable.red_tick)
                        .setTitle("Something went wrong!!!")
                        .setMessage("Could not upload content, please try again")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
            }
        });
    }

    private void updateFirestore(Uri uri){
        uploadUri = uri.toString();
        mDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        ImageUploadDetails uploadDetails = new ImageUploadDetails(mDate, uploadUri, mDescription);
        CollectionReference collectionReference = firebaseFirestore.collection("upload_details").document("1")
                .collection(FetchNow.getUserId());
        collectionReference.add(uploadDetails).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    pg.setVisibility(View.INVISIBLE);
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    dialogBuilder.setIcon(R.drawable.green_tick)
                            .setTitle("Upload successful!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    getDialog().dismiss();
                                }
                            });
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                }
                else {
                    pg.setVisibility(View.INVISIBLE);
                    CustomToast.displayToast(getContext(), fragView, "Upload error sorry");
                }
            }
        });
    }

    private void hideKeyboard(){
        if(fragView != null){
            ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(fragView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
