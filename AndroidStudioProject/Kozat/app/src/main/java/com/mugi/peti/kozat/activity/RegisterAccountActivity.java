package com.mugi.peti.kozat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mugi.peti.kozat.R;

public class RegisterAccountActivity extends AppCompatActivity
{
    Button registerButton;
    EditText emailEditText;
    EditText passwordEditText;

    TextView errorTextView;
    View progressView;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        firebaseAuth = FirebaseAuth.getInstance();
        registerButton = (Button) findViewById(R.id.registerButton);
        emailEditText = findViewById(R.id.emailInputText);
        passwordEditText = findViewById(R.id.passwordInputText);

        errorTextView = findViewById(R.id.registerErrorTextView);
        progressView = findViewById(R.id.registerProgressBar);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    emailEditText.setError("Email address format is incorrect!");
                    return;
                }
                if (password.length() < 7)
                {
                    passwordEditText.setError("Password must be at least 7 characters long!");
                    return;
                }
                registerButton.setEnabled(false);
                progressView.setVisibility(View.VISIBLE);
                errorTextView.setVisibility(View.GONE);
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    progressView.setVisibility(View.GONE);
                                    registerButton.setEnabled(true);
                                    Intent intent = new Intent(RegisterAccountActivity.this, CreateProfileActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    progressView.setVisibility(View.GONE);
                                    errorTextView.setVisibility(View.VISIBLE);
                                    registerButton.setEnabled(true);
                                    //Toast.makeText(getApplicationContext(), "Registration failed! Please try again", Toast.LENGTH_LONG)
                                    //        .show();
                                }
                            }
                        });
            }
        });
    }
}
