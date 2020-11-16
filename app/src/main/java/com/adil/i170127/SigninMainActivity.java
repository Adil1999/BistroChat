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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SigninMainActivity extends AppCompatActivity {

    TextView register;
    EditText email, password;
    Button signin;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signin_main);

        firebaseAuth = FirebaseAuth.getInstance();
        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninMainActivity.this, RegisterActivity.class));
                finish();
            }
        });

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signin = findViewById(R.id.signin);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString().trim();
                String txt_password = password.getText().toString().trim();

                if(TextUtils.isEmpty(txt_email)){
                    Toast.makeText(SigninMainActivity.this, "Please Enter Email", Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(txt_password)){
                    Toast.makeText(SigninMainActivity.this, "Please Enter Password", Toast.LENGTH_LONG).show();
                    return;
                }
                if(txt_password.length() < 6) {
                    Toast.makeText(SigninMainActivity.this, "Password too short", Toast.LENGTH_LONG).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(txt_email, txt_password)
                        .addOnCompleteListener(SigninMainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(SigninMainActivity.this, HomeActivity.class));
                                    Toast.makeText(SigninMainActivity.this, "Signing in", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(SigninMainActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }
}