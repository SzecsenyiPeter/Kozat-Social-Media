package com.mugi.peti.kozat.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;

import com.mugi.peti.kozat.BuildConfig;
import com.mugi.peti.kozat.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;


public class PickImageDialogueFragment extends DialogFragment {

    public interface ImageChosenListener
    {
        void onImageChosen(Uri chosenImageUri);
    }

    ImageView galleryImageView;
    ImageView cameraImageView;
    TextView galleryTextView;
    TextView cameraTextView;
    protected static final int REQUEST_GALLERY = 1;
    protected static final int REQUEST_CAMERA = 2;
    ImageChosenListener imageChosenCallback;
    Uri cameraOutPutURI;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.dialogue_choose_image,null);

        galleryImageView = dialogView.findViewById(R.id.ChooseImageGalleryImageView);
        cameraImageView = dialogView.findViewById(R.id.ChooseImageCameraImageView);
        galleryTextView = dialogView.findViewById(R.id.ChooseImageGalleryTextView);
        cameraTextView = dialogView.findViewById(R.id.ChooseImageCameraTextView);

        galleryImageView.setOnClickListener(new GalleryClickListener());
        galleryTextView.setOnClickListener(new GalleryClickListener());

        cameraImageView.setOnClickListener(new CameraClickListener());
        cameraTextView.setOnClickListener(new CameraClickListener());

        builder.setView(dialogView);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PickImageDialogueFragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    public void setImageChosenCallBack(ImageChosenListener imageChosenListener)
    {
        this.imageChosenCallback = imageChosenListener;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode)
        {
            case REQUEST_GALLERY:
                this.dismiss();
                if (data == null){
                    return;
                }
                Uri chosenImageUri = data.getData();
                imageChosenCallback.onImageChosen(chosenImageUri);
                break;
            case REQUEST_CAMERA:
                this.dismiss();
                imageChosenCallback.onImageChosen(cameraOutPutURI);
                break;
        }

    }
    class GalleryClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_GALLERY);
        }
    }
    class CameraClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            File choosenFileCropped = new File(Environment.getExternalStorageDirectory(), "tempFileCamera.png");
            cameraOutPutURI = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".com.vansuita.pickimage.provider", choosenFileCropped);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraOutPutURI);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }
}
