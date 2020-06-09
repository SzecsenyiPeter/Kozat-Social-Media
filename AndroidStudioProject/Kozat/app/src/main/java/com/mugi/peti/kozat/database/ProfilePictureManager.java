package com.mugi.peti.kozat.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfilePictureManager
{
    final static String PROFILE_PICTURE_ROOT = "ProfilePictures";
    final static String PROFILE_PICTURE_NUMBER = "ProfilePictureNumber";

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    public StorageReference profilePicturesStorageReference;
    public ProfilePictureManager()
    {
        profilePicturesStorageReference = FirebaseStorage.getInstance().getReference().child(PROFILE_PICTURE_ROOT);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child(PROFILE_PICTURE_NUMBER);
    }

    public void uploadProfilePicture(Uri pictureToUpload, String userUid, OnFailureListener failureListener, OnSuccessListener<UploadTask.TaskSnapshot> successListener){
        UploadTask uploadTask = profilePicturesStorageReference
                .child(userUid)
                .putFile(pictureToUpload);
        uploadTask.addOnFailureListener(failureListener).addOnSuccessListener(successListener);
        databaseReference.child(userUid).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                int profilePictureNumber = 0;
                if (mutableData.getValue() != null)
                {
                    profilePictureNumber = mutableData.getValue(Integer.class);
                }
                profilePictureNumber++;

                mutableData.setValue(profilePictureNumber);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) { }
        });

    }

    public void loadProfilePictureIntoImageView(final String userUid, final ImageView imageView, final Context context){


        databaseReference.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int profilePictureNumber = -2;
                if (dataSnapshot.getValue() != null){
                    profilePictureNumber = dataSnapshot.getValue(Integer.class);
                }
                SharedPreferences sharedPreferences = context.getSharedPreferences(PROFILE_PICTURE_NUMBER, Context.MODE_PRIVATE);
                int cachedProfilePictureForUserNumber = sharedPreferences.getInt(userUid, -1);
                if(profilePictureNumber != cachedProfilePictureForUserNumber){
                    Glide.with(context)
                            .using(new FirebaseImageLoader())
                            .load(profilePicturesStorageReference.child(userUid))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imageView);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(userUid, profilePictureNumber);
                    editor.apply();
                }
                else {
                    Glide.with(context)
                            .using(new FirebaseImageLoader())
                            .load(profilePicturesStorageReference.child(userUid))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false)
                            .into(imageView);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
