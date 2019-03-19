package com.mhp.foodfinder.Activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mhp.foodfinder.R;

public class ResetPassword extends AppCompatActivity {

    private EditText resetemail_txt;
    private Button reset_btn;
    private FirebaseAuth mAuth;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();
        progress=(ProgressBar)findViewById(R.id.progressBar);

        resetemail_txt = (EditText)findViewById(R.id.resetemail_txt);
        reset_btn = (Button)findViewById(R.id.reset_btn);

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
    }

    private void reset(){

        final String email_temp = resetemail_txt.getText().toString().trim();

        if (email_temp.isEmpty()) {
            resetemail_txt.setError("Email is required!!");
            resetemail_txt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email_temp).matches()) {
            resetemail_txt.setError("Please enter a valid email!!");
            resetemail_txt.requestFocus();
            return;
        }

        progress.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email_temp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Email had been sent. Please check your email account to reset your password!!",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    progress.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
