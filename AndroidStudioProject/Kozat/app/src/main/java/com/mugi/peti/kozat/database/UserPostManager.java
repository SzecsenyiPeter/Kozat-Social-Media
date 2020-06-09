package com.mugi.peti.kozat.database;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.mugi.peti.kozat.model.UserPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserPostManager {
    FirebaseDatabase database;
    DatabaseReference databaseReference;


    public  final static String POST_FEED_REFERENCE = "PostFeed";
    public final static String FEED_TYPE_USER = "MyFeed";
    public final static String FEED_TYPE_FRIENDS = "FriendFeed";
    public UserPostManager()
    {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
    }

    public void submitPost(final String submiterUid, UserPost post, Uri postImageUri, final OnCompleteListener<Void> callback)
    {
        DatabaseReference postReference = databaseReference.child("Posts").push();
        final String postId = postReference.getKey();
        postReference.setValue(post).addOnCompleteListener(callback);
        FriendManager friendManager = new FriendManager();
        databaseReference.child(POST_FEED_REFERENCE).child(submiterUid).child(FEED_TYPE_USER).child(postId).setValue(true);
        friendManager.retrieveFriendUids(submiterUid, new FriendManager.OnFriendListRetrievedListener() {
            @Override
            public void onAllUidsGathered(ArrayList<String> friendUids)
            {
                Map<String, Object> friendFeedUpdates = new HashMap<>();
                for (String friendUid : friendUids)
                {
                    friendFeedUpdates.put(POST_FEED_REFERENCE + "/" + friendUid + "/" + FEED_TYPE_FRIENDS + "/" + postId, false );
                }
                friendFeedUpdates.put(POST_FEED_REFERENCE + "/" + submiterUid + "/" + FEED_TYPE_FRIENDS + "/" + postId, false );
                databaseReference.updateChildren(friendFeedUpdates);
            }
        });
        if (post.withImage)
        {
            PictureManager pictureManager = new PictureManager(PictureManager.POST_PICTURE_ROOT);
            pictureManager.uploadPicture(postImageUri, postId, new OnFailureListener() { // ide
                @Override
                public void onFailure(@NonNull Exception e) {
                    //TODO baj handling, öröké forgó kör hozzáadása

                }
            }, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            });
        }


    }

    public void readPost(String postId, ValueEventListener callback)
    {
        databaseReference
                .child("Posts").child(postId)
                .addListenerForSingleValueEvent(callback);
    }

}
