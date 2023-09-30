package com.satifeed;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText mEmail, mPassword;
    Button mLoginBtn;
    TextView mForgotBtn;
    ProgressBar mProgressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = findViewById(R.id.loginEmail);
        mPassword = findViewById(R.id.loginPassword);
        mLoginBtn = findViewById(R.id.loginBtn);
        mForgotBtn = findViewById(R.id.forgotPassTxt);
        mProgressBar = findViewById(R.id.progressBar);
        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() != null){
            Intent i = new Intent(LoginActivity.this, NoticeActivity.class);
            i.putExtra("faculty", "Yes");
            startActivity(i);
            finish();
        }

        mLoginBtn.setOnClickListener(v -> {
            // Get data from the input fields
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString();

            // Check if input data is valid or not
            if(checkData()){
                // Begin registration process
                mProgressBar.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(LoginActivity.this, NoticeActivity.class);
                        i.putExtra("faculty", "Yes");
                        startActivity(i);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        finish();
                    }
                    else{
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity.java", task.getException().getMessage());
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        mForgotBtn.setOnClickListener(v -> {

            // Create the "New Contact" view from the layout_add_contact.xml layout file
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View view = inflater.inflate(R.layout.forgot_layout, null);

            MaterialAlertDialogBuilder alertBuilder = new MaterialAlertDialogBuilder(LoginActivity.this, R.style.MyDialogTheme);
            alertBuilder.setView(view);

            // Get the email from the EditText
            final EditText emailField = view.findViewById(R.id.forgotEmail);

            alertBuilder.setCancelable(false)
                    .setPositiveButton("Send", (di,i) -> {})
                    .setNegativeButton("Cancel", (di,i) -> {
                        di.cancel();
                    });

            final AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view2 -> {
                if (TextUtils.isEmpty(emailField.getText())){
                    emailField.setError("Email is required");
                    return;
                }

                String email = emailField.getText().toString();
                fAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Reset Link sent. Please check your email", Toast.LENGTH_LONG).show();
                        Log.d("LoginActivity", "Email sent.");
                    }
                    else{
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("LoginActivity", task.getException().getMessage());
                    }
                });

                alertDialog.dismiss();
            });

        });
    }

    private boolean checkData() {
        if (TextUtils.isEmpty(mEmail.getText())){
            mEmail.setError("Email is required");
            return false;
        }

        if (TextUtils.isEmpty(mPassword.getText())){
            mPassword.setError("Password is required");
            return false;
        }

        if (mPassword.getText().toString().length() < 6){
            mPassword.setError("Password must be at least 6 characters long");
            return false;
        }

        return true;
    }
}