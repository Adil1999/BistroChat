package com.adil.i170127;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    TextView signin;
    EditText email,password,cnPassword;
    Button register;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        signin = findViewById(R.id.signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, SigninMainActivity.class));
                finish();
            }
        });

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        cnPassword = findViewById(R.id.cn_password);
        register = findViewById(R.id.register);

        firebaseAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString().trim();
                String txt_password = password.getText().toString().trim();
                String txt_cpassword = cnPassword.getText().toString().trim();

                if(TextUtils.isEmpty(txt_email)){
                    Toast.makeText(RegisterActivity.this, "Please Enter Email", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(txt_password)){
                    Toast.makeText(RegisterActivity.this, "Please Enter Password", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(txt_cpassword)){
                    Toast.makeText(RegisterActivity.this, "Please Enter Confirm Password", Toast.LENGTH_LONG).show();
                    return;
                }
                if(txt_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password too short", Toast.LENGTH_LONG).show();
                    return;
                }

                if (txt_password.equals(txt_cpassword)){
                    firebaseAuth.createUserWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(RegisterActivity.this, CreateProfileActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        });

    }
}