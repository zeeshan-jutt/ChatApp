package com.converter.allinonechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    RecyclerView UserRecyclerView;
    ImageView Logout, imgSetting;
    UserAdapter adapter;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar =(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Chats");

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersArrayList = new ArrayList<>();


        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }
        Toast.makeText(this, "Data is Fetching...", Toast.LENGTH_SHORT).show();

        DatabaseReference reference = database.getReference().child("User");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        UserRecyclerView = findViewById(R.id.UserRecycle);
        UserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(HomeActivity.this, usersArrayList);
        UserRecyclerView.setAdapter(adapter);

//        imgSetting = findViewById(R.id.img_Setting);
//        Logout = findViewById(R.id.Logout);
//        Logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Dialog dialog = new Dialog(HomeActivity.this, R.style.UserDialog);
//                dialog.setContentView(R.layout.dialog_layout);
//
//                TextView cancelbtn, logoutbtn;
//                cancelbtn = dialog.findViewById(R.id.cancelbtn);
//                logoutbtn = dialog.findViewById(R.id.logoutbtn);
//
//                logoutbtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        FirebaseAuth.getInstance().signOut();
//                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
//                    }
//                });
//                cancelbtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();
//            }
//        });
//        imgSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.main_settings_btn:
                Intent i = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(i);
                return true;

            case R.id.main_logout_btn:
                Dialog dialog = new Dialog(HomeActivity.this, R.style.UserDialog);
                dialog.setContentView(R.layout.dialog_layout);

                TextView cancelbtn, logoutbtn;
                cancelbtn = dialog.findViewById(R.id.cancelbtn);
                logoutbtn = dialog.findViewById(R.id.logoutbtn);

                logoutbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    }
                });
                cancelbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return  true;
            case R.id.main_more_btn:
                Toast.makeText(HomeActivity.this,"Wait for more...", Toast.LENGTH_SHORT).show();
                return  true;
            default:   return super.onOptionsItemSelected(item);
        }

    }
//
//    @Override
//    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
//            return;
//        }
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//        doubleBackToExitPressedOnce = true;
//    }
}