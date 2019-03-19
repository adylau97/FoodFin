package com.mhp.foodfinder.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import com.mhp.foodfinder.Model.User;
import com.mhp.foodfinder.R;

import java.util.Calendar;

public class CreateAcc extends AppCompatActivity {
    CountryCodePicker ccp;
    EditText editTextCarrierNumber,name,password,retype_password,email,dob;
    Button create;
    Toolbar toolbar;
    ProgressBar progress;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    int sel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        name=(EditText)findViewById(R.id.name_txt);
        password=(EditText)findViewById(R.id.password_txt);
        retype_password=(EditText)findViewById(R.id.retype_txt);
        email=(EditText)findViewById(R.id.email_txt);
        dob=(EditText)findViewById(R.id.dateofbirth_txt);
        dob.addTextChangedListener(mDateEntryWatcher);
        toolbar = (Toolbar)findViewById(R.id.createacc_toolbar);
        progress=(ProgressBar)findViewById(R.id.progressBar);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        editTextCarrierNumber = (EditText) findViewById(R.id.editText_carrierNumber);
        create=(Button)findViewById(R.id.create_btn);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Back");


        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               CreateUser();
            }
        });
    }

    private TextWatcher mDateEntryWatcher = new TextWatcher() {

        private String current="";
        private String ddmmyyyy = "DDMMYYYY";
        private Calendar cal = Calendar.getInstance();

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                int cl = clean.length();
                sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8){
                    clean = clean + ddmmyyyy.substring(clean.length());
                }else{
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day  = Integer.parseInt(clean.substring(0,2));
                    int mon  = Integer.parseInt(clean.substring(2,4));
                    int year = Integer.parseInt(clean.substring(4,8));

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon-1);
                    year = year<1900 ?1900: year>Calendar.getInstance().get(Calendar.YEAR)?Calendar.getInstance().get(Calendar.YEAR):year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                    clean = String.format("%02d%02d%02d",day, mon, year);
                }

                clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                dob.setText(current);
                dob.setSelection(sel < current.length() ? sel : current.length());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void writeNewUser(String id,String birthday, String name, String phone) {
        User user = new User(birthday,name, phone,null);
        mDatabase.child("users").child(id).setValue(user);
    }

    private void CreateUser() {
        final String name_temp = name.getText().toString().trim();
        String email_temp = email.getText().toString().trim();
        String password_temp = password.getText().toString().trim();
        String retype_temp = retype_password.getText().toString().trim();
        final String dob_temp = dob.getText().toString().trim();



        if (name_temp.isEmpty()) {
            name.setError("Name is required!!");
            name.requestFocus();
            return;
        }
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
        if (retype_temp.isEmpty()) {
            retype_password.setError("Please retype the password!!");
            retype_password.requestFocus();
            return;
        }
        if (!retype_temp.equals(password_temp)) {
            retype_password.setError("The password is not correct!!");
            retype_password.requestFocus();
            return;
        }
        if (dob_temp.isEmpty()||dob_temp.equals("DD/MM/YYYY")) {
            dob.setError("Date of birth is required!!");
            dob.requestFocus();
            return;
        }

        if(sel!=10 && sel!=11){
            dob.setError("Invalid date!!");
            dob.requestFocus();
            return;
        }

        ccp.registerCarrierNumberEditText(editTextCarrierNumber);

        if (!ccp.isValidFullNumber()) {
            editTextCarrierNumber.setError("Phone number is not valid!!");
            return;
        }


            progress.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email_temp, password_temp).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progress.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user= mAuth.getCurrentUser();
                        String uid=user.getUid();
                        writeNewUser(uid,dob_temp,name_temp,ccp.getFullNumberWithPlus());
                        startActivity(new Intent(CreateAcc.this,Snitch_v2.class));
                        finish();
                    } else {
                        if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(getApplicationContext(), "You had already registered!!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
}
