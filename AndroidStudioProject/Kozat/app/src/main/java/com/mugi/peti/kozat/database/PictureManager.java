package com.mugi.peti.kozat.database;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PictureManager {
    public final static String PROFILE_PICTURE_ROOT = "ProfilePictures";
    public final static String POST_PICTURE_ROOT = "PostPictures";

    private StorageReference pictureStorageReference;
    public PictureManager(String pictureLocation)
    {
        pictureStorageReference = FirebaseStorage.getInstance().getReference().child(pictureLocation);
    }

    public void uploadPicture(Uri pictureToUpload, String userUid, OnFailureListener failureListener, OnSuccessListener<UploadTask.TaskSnapshot> successListener){
        UploadTask uploadTask = pictureStorageReference
                .child(userUid)
                .putFile(pictureToUpload);
        uploadTask.addOnFailureListener(failureListener).addOnSuccessListener(successListener);
    }

    public void loadPictureIntoImageView(String userUid, ImageView imageView, Context context){
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(pictureStorageReference.child(userUid))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}
