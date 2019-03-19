package com.mhp.foodfinder.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mhp.foodfinder.R;

public class Loginwithemail extends AppCompatActivity {
    Toolbar toolbar;
    Button login,resetpassword;
    EditText email,password;
    ProgressBar progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_loginwithemail);
        toolbar = (Toolbar)findViewById(R.id.loginwithemail_toolbar);
        login=(Button)findViewById(R.id.loginpage_button);
        email=(EditText)findViewById(R.id.emaillogin_txt);
        password=(EditText)findViewById(R.id.passwordlogin_txt);
        progress=(ProgressBar)findViewById(R.id.progressBar2);
        resetpassword = (Button)findViewById(R.id.resetpassword_btn);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Back");

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Loginwithemail.this,ResetPassword.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loginUser(){
        String email_temp=email.getText().toString().trim();
        String password_temp=password.getText().toString().trim();

        if (email_temp.isEmpty()) {
            email.setError("Email is required!!");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email_temp).matches()) {
            email.setError("Please enter a valid email!!");
            email.requestFocus();
            return;
        }
        if (password_temp.isEmpty()) {
            password.setError("Password is required!!");
            password.requestFocus();
            return;
        }
        if (password_temp.length() < 6) {
            password.setError("Minimum length of the password is 6!!");
            password.requestFocus();
            return;
        }

        progress.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email_temp,password_temp).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    progress.setVisibility(View.GONE);
                    startActivity(new Intent(Loginwithemail.this,Snitch_v2.class));
                    finish();
                }else{
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
