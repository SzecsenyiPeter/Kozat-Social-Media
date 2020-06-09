package com.mugi.peti.kozat.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class LoginActivity extends AppCompatActivity
{
    Button loginButton;
    Button registerButton;

    EditText emailEditText;
    EditText passwordText;

    TextView errorTextView;
    View progressView;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.promptToRegisterButton);

        emailEditText = findViewById(R.id.loginEmailInputText);
        passwordText = findViewById(R.id.loginPasswordInputText);

        errorTextView = findViewById(R.id.loginErrorTextView);
        progressView = findViewById(R.id.loginProgressBar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordText.getText().toString();
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    emailEditText.setError("Email address format is incorrect!");
                    return;
                }
                progressView.setVisibility(View.VISIBLE);
                errorTextView.setVisibility(View.GONE);
                loginButton.setEnabled(false);
                registerButton.setEnabled(false);


                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    progressView.setVisibility(View.GONE);
                                    loginButton.setEnabled(true);
                                    registerButton.setEnabled(true);
                                    Intent intent = new Intent(LoginActivity.this, MainFeedActivity.class);
                                    startActivity(intent);
                                } else{
                                    progressView.setVisibility(View.GONE);
                                    errorTextView.setVisibility(View.VISIBLE);
                                    loginButton.setEnabled(true);
                                    registerButton.setEnabled(true);
                                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}
