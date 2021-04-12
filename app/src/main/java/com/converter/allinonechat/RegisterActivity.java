package com.converter.allinonechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    TextView username, useremail,passsignup,repassword;
    Button btnsignin, btnregister;
    CircleImageView profile_img;
    Uri imageUri;
    String imageURI;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog=new ProgressDialog(this );
        progressDialog.setMessage("Loading...Please wait");
        progressDialog.setCancelable(false);

        database= FirebaseDatabase.getInstance();
        auth= FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        storage= FirebaseStorage.getInstance();
        profile_img=findViewById(R.id.profile_img);
        username=findViewById(R.id.username);
        useremail=findViewById(R.id.useremail);
        passsignup=findViewById(R.id.passsignup);
        repassword=findViewById(R.id.repassword);
        btnsignin=findViewById(R.id.btnsignin);
        btnregister=findViewById(R.id.btnregister);

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String name = username.getText().toString();
                String email = useremail.getText().toString();
                String pass = passsignup.getText().toString();
                String confpass = repassword.getText().toString();
                String status = "Hay There I am Using All in One ChatApp";

                if (name.isEmpty()) {
                    username.setError("Username is empty");
                    username.requestFocus();
                    progressDialog.dismiss();
                    return;

                }

                if (email.isEmpty()) {
                    useremail.setError("Email is empty");
                    useremail.requestFocus();
                    progressDialog.dismiss();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    useremail.setError("Enter the valid email address");
                    useremail.requestFocus();
                    progressDialog.dismiss();
                    return;
                }
                if (pass.isEmpty()) {
                    passsignup.setError("Enter the password");
                    passsignup.requestFocus();
                    progressDialog.dismiss();
                    return;
                }
                if (pass.length() < 6) {
                    passsignup.setError("Length of the password should be more than 6");
                    passsignup.requestFocus();
                    progressDialog.dismiss();
                    return;
                }
                if (confpass.isEmpty()) {
                    repassword.setError("Re-Enter same Password");
                    repassword.requestFocus();
                    progressDialog.dismiss();
                    return;
                }
                if (!confpass.equals(pass)) {
                    repassword.setError("Password not matched ");
                    repassword.requestFocus();
                    progressDialog.dismiss();
                    return;
                }
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference reference=database.getReference().child("User").child(auth.getUid());
                            StorageReference reference1=storage.getReference().child("Upload").child(auth.getUid());

                            if(imageUri != null){
                                reference1.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if(task.isComplete()){
                                            reference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    imageURI= uri.toString();
                                                    Users users=new Users(auth.getUid(), name, email, imageURI, status);
                                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                progressDialog.dismiss();
                                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                            } else{
                                                                Toast.makeText(RegisterActivity.this, "You are not Registered! Try again", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            }else{
                                String status = "Hay There I am Using All in One ChatApp";
                                imageURI= "https://firebasestorage.googleapis.com/v0/b/allinonechat-6ec6d.appspot.com/o/profile_img.jpg?alt=media&token=97ba74a6-8e4e-4642-bd63-219152597643";
                                Users users=new Users(auth.getUid(), name, email, imageURI, status);
                                reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Registered Successfully! ", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                                        } else{
                                            Toast.makeText(RegisterActivity.this, "You are not Registered! Try again", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "You are not Registered! Try again", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            }
        });

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);

            }
        });

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                switch (view.getId()){
                    case R.id.btnsignin:
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        break;
                               }
            }
        });
        

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==10){
            if (data!=null){
                 imageUri=data.getData();
                profile_img.setImageURI(imageUri);
            }
        }
    }
}