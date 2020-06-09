package com.mugi.peti.kozat.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;
import com.mugi.peti.kozat.R;
import com.mugi.peti.kozat.database.ProfilePictureManager;
import com.mugi.peti.kozat.database.UserProfileManager;
import com.mugi.peti.kozat.fragment.PickImageDialogueFragment;
import com.mugi.peti.kozat.model.UserProfile;
import com.mugi.peti.kozat.utilities.StringFormatter;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateProfileActivity extends AppCompatActivity{


    EditText fullNameEditText;
    EditText dateOfBirth;
    Button submit;
    Calendar currentDate;
    Calendar selectedDateOfBirth;
    Button chooseProfilePicture;
    ImageView profilePictureImage;

    View progressBar;
    boolean isDateSelected = false;

    String dateOfBirthString;

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    protected static final int REQUEST_PICK_IMAGE = 1;
    Uri imageToUseUri;
    CreateProfileActivity context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        setContentView(R.layout.activity_create_profile);
        fullNameEditText = findViewById(R.id.creationFullNameEditText);
        dateOfBirth = findViewById(R.id.creationDateOfBirthEditText);

        submit = findViewById(R.id.createSubmitButton);
        currentDate = Calendar.getInstance();
        selectedDateOfBirth = Calendar.getInstance();
        chooseProfilePicture = findViewById(R.id.creationChooseProfilePicture);
        profilePictureImage = findViewById(R.id.creationProfilePicture);
        progressBar = findViewById(R.id.creationProgressBar);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        context = this;

        final DatePickerDialog.OnDateSetListener dateCallback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                selectedDateOfBirth.set(Calendar.YEAR, year);
                selectedDateOfBirth.set(Calendar.MONTH, month);
                selectedDateOfBirth.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String dateFormat = "MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
                dateOfBirthString = sdf.format(selectedDateOfBirth.getTime());
                dateOfBirth.setText(dateOfBirthString);
                isDateSelected = true;
            }
        };

        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CreateProfileActivity.this,
                        AlertDialog.THEME_HOLO_LIGHT,
                        dateCallback,
                        currentDate.get(Calendar.YEAR),currentDate.get(Calendar.MONTH),currentDate.get(Calendar.DAY_OF_WEEK_IN_MONTH)
                );
                datePickerDialog.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullNameEditText.getText().toString();
                submit.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                if (StringFormatter.checkIfStringIsNullOrEmpty(fullName) || StringFormatter.checkIfStringContainsSpecialChars(fullName)){
                    submit.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Please enter a valid name!", Toast.LENGTH_LONG)
                            .show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                fullName = StringFormatter.removeUnnecessaryWhiteSpaces(fullName);
                fullName = StringFormatter.removeUnnecessaryLinesFromString(fullName);
                if (imageToUseUri == null){
                    //imageToUseUri = Uri.parse("android.resource://com.mugi.peti.kozat/drawable/ic_launcher_background");
                    Drawable d = ResourcesCompat.getDrawable(getResources(), R.mipmap.kozat_icon_primary, null);
                    Bitmap bm = drawableToBitmap(d);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bm, "Title", null);
                    imageToUseUri = Uri.parse(path);

                }

                if (isDateSelected)
                {
                    String dateFormat = "MM/dd/yyyy";
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
                    UserProfile userProfileToCreate = new UserProfile(fullName, firebaseUser.getEmail(), sdf.format(currentDate.getTime()), dateOfBirthString);
                    UserProfileManager userProfileManager = new UserProfileManager();
                    userProfileManager.writeUserProfile(userProfileToCreate, firebaseUser.getUid(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                ProfilePictureManager profilePictureManager = new ProfilePictureManager();
                                profilePictureManager.uploadProfilePicture(imageToUseUri, firebaseUser.getUid(), new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Failed to upload profile picture! Please try again!", Toast.LENGTH_LONG)
                                                .show();
                                        progressBar.setVisibility(View.GONE);
                                        submit.setEnabled(true);
                                    }
                                }, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        progressBar.setVisibility(View.GONE);
                                        submit.setEnabled(true);
                                        Intent intent = new Intent(CreateProfileActivity.this, MainFeedActivity.class);
                                        startActivity(intent);
                                    }
                                });
                        }
                            else {
                                Toast.makeText(getApplicationContext(), "Profile creation failed! Please try again!", Toast.LENGTH_LONG)
                                        .show();
                                progressBar.setVisibility(View.GONE);
                                submit.setEnabled(true);
                            }
                        }
                    });

                }
            }
        });
        chooseProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });
    }

    void showImagePickerDialog()
    {
        PickImageDialogueFragment pickImageDialogueFragment = new PickImageDialogueFragment();
        pickImageDialogueFragment.setImageChosenCallBack(new PickImageDialogueFragment.ImageChosenListener() {
            @Override
            public void onImageChosen(Uri chosenImageUri) {
                imageToUseUri = chosenImageUri;
                File choosenFileCropped = new File(getCacheDir(), "tempFileCropped.png");
                Uri destination = Uri.fromFile(choosenFileCropped);
                UCrop.Options options = new UCrop.Options();
                options.setCircleDimmedLayer(true);
                UCrop.of(imageToUseUri, destination).withAspectRatio(1,1).withOptions(options).start(context, UCrop.REQUEST_CROP);
            }
        });
        pickImageDialogueFragment.show(getFragmentManager(), "like this");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode)
        {
            case REQUEST_PICK_IMAGE:
                imageToUseUri = data.getData();
                File choosenFileCropped = new File(getCacheDir(), "tempFileCropped.png");
                Uri destination = Uri.fromFile(choosenFileCropped);
                UCrop.Options options = new UCrop.Options();
                options.setCircleDimmedLayer(true);
                UCrop.of(imageToUseUri, destination).withAspectRatio(1,1).withOptions(options).start(this);
                break;

            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    imageToUseUri = UCrop.getOutput(data);
                    profilePictureImage.setImageURI(null);
                    profilePictureImage.setImageURI(imageToUseUri);
                }
                break;

        }

    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
