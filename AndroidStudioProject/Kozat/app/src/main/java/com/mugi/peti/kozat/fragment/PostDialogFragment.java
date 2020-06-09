package com.mugi.peti.kozat.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;
import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.database.PictureManager;
import com.mugi.peti.kozat.database.UserPostManager;
import com.mugi.peti.kozat.model.UserPost;
import com.mugi.peti.kozat.utilities.StringFormatter;

public class PostDialogFragment extends DialogFragment
{

    EditText postEditText;
    ImageView addImageButton;
    ImageView chosenImageDisplay;

    Uri pictureUriToUpload;
    Boolean imageAdded = false;

    FeedMainFragment callingFragment;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.dialog_create_post,null);
        builder.setView(dialogView);

        postEditText = dialogView.findViewById(R.id.CreatePostEditText);
        addImageButton = dialogView.findViewById(R.id.CreatePostAddPhotoImageView);
        chosenImageDisplay = dialogView.findViewById(R.id.CreatePostImageDisplay);
        chosenImageDisplay.setImageURI(null);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialogueFragment pickImageDialogueFragment = new PickImageDialogueFragment();
                pickImageDialogueFragment.setImageChosenCallBack(new PickImageDialogueFragment.ImageChosenListener() {
                    @Override
                    public void onImageChosen(Uri chosenImageUri) {
                        imageAdded = true;
                        pictureUriToUpload = chosenImageUri;
                        chosenImageDisplay.setVisibility(View.VISIBLE);
                        chosenImageDisplay.setImageURI(chosenImageUri);
                        addImageButton.setVisibility(View.GONE);
                    }
                });
                pickImageDialogueFragment.show(getActivity().getFragmentManager(), "pls");
            }
        });

        builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String postText = postEditText.getText().toString();
                if (StringFormatter.checkIfStringIsNullOrEmpty(postText)) return;
                postText = StringFormatter.removeUnnecessaryLinesFromString(postText);
                UserPost userPost = new UserPost(user.getUid(), postText, imageAdded);


                UserPostManager userPostManager = new UserPostManager();
                userPostManager.submitPost(
                        user.getUid(),
                        userPost,
                        pictureUriToUpload,
                        new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (imageAdded){

                        }
                    }
                });

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PostDialogFragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

}
