package com.mhp.foodfinder.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.mhp.foodfinder.Adapter.ItemPagerAdapter;
import com.mhp.foodfinder.Model.User;
import com.mhp.foodfinder.R;

import java.util.Calendar;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class UserSettings extends AppCompatActivity {

    private EditText name_text,birthday_text,contact_text;
    private CountryCodePicker ccp;
    private Button img_select;
    private FloatingTextButton update_btn;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    private ViewPager user_viewPager;
    private ItemPagerAdapter adapter;

    private final int PICK_IMAGE_REQUEST = 71;
    String [] user_filePath,img = null;

    private int sel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        name_text = (EditText)findViewById(R.id.name_text);
        birthday_text = (EditText)findViewById(R.id.birthday_text);
        birthday_text.addTextChangedListener(mDateEntryWatcher);
        contact_text = (EditText)findViewById(R.id.editText_carrierNumber);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(contact_text);
        img_select = (Button) findViewById(R.id.img_select);
        update_btn = (FloatingTextButton)findViewById(R.id.update_btn);

        user_viewPager = (ViewPager) findViewById(R.id.user_viewPager);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();

        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user_d = dataSnapshot.getValue(User.class);

                if(user_d==null){
                    img= new String[1];
                    img[0] =null;
                    return;
                }

                if(user_d.getName()!=null) {
                    name_text.setText(user_d.getName());
                }

                if(user_d.getBirthday()!=null){
                    birthday_text.setText(user_d.getBirthday());
                }

                if(user_d.getPhone()!=null){
                    ccp.setFullNumber(user_d.getPhone());
                }

                if(user_d.getProfilePicture()!=null){
                    img= new String[1];
                    img[0] = user_d.getProfilePicture();
                    adapter = new ItemPagerAdapter(getApplicationContext(),img);
                    user_viewPager.setAdapter(adapter);
                    user_viewPager.setVisibility(View.VISIBLE);
                }else{
                    img= new String[1];
                    img[0] =null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        img_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAcc();
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
                birthday_text.setText(current);
                birthday_text.setSelection(sel < current.length() ? sel : current.length());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    };

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null &&data.getData()!=null){
            user_filePath = new String[1];
            user_filePath[0] = data.getData().toString();
            adapter = new ItemPagerAdapter(getApplicationContext(),user_filePath);
            user_viewPager.setAdapter(adapter);
            user_viewPager.setVisibility(View.VISIBLE);
        }
    }

    private void updateUser(String birthday, String name,String phone, String profilePicture ){
        User user_d = new User (birthday,name,phone,profilePicture);
        mDatabase.child("users").child(user.getUid()).setValue(user_d);
    }

    private void updateAcc(){

        try {
            final String name_temp = name_text.getText().toString().trim();
            final String birthday_temp = birthday_text.getText().toString().trim();
            final String contact_temp = contact_text.getText().toString().trim();

            if (!contact_temp.isEmpty()) {
                if (!ccp.isValidFullNumber()) {
                    contact_text.setError("Contact number is not valid!!");
                    contact_text.requestFocus();
                    return;
                }
            }

            if (user_filePath != null) {

                if(img[0]!=null) {
                    StorageReference imageRef = mStorage.getReferenceFromUrl(img[0]);
                    imageRef.delete();
                }

                final AlertDialog waitingDialog = new SpotsDialog(UserSettings.this);
                waitingDialog.show();
                waitingDialog.setMessage("Updating....");

                StorageReference imageRef = storageReference.child("users/"+ UUID.randomUUID().toString());
                Uri file = Uri.parse(user_filePath[0]);
                imageRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        updateUser(birthday_temp,name_temp,ccp.getFullNumberWithPlus(),taskSnapshot.getDownloadUrl().toString());
                        Toast.makeText(getApplicationContext(), "Update successfully", Toast.LENGTH_SHORT).show();
                        waitingDialog.dismiss();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Update fail 1" + e.toString(), Toast.LENGTH_LONG).show();
                    }
                });

            } else {
                updateUser(birthday_temp, name_temp, ccp.getFullNumberWithPlus(), img[0]);
                Toast.makeText(getApplicationContext(), "Update successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "Update fail 2" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
