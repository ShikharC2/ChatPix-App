package com.android.chatpix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ChatPix").setMessage("Are You Sure You Want to Exit?")
                .setIcon(R.drawable.logo);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Date date = new Date();
                String time = String.valueOf(date.getHours())+":"+String.valueOf(date.getMinutes());
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child("LastActive").child(currentUser.getPhoneNumber()).child("LastActive").setValue("Last Active at "+time);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

//    public void onStop() {
//        super.onStop();
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//        Date date = new Date();
//        String time = String.valueOf(date.getHours()) + ":" + String.valueOf(date.getMinutes());
//        reference.child("LastActive").child(currentUser.getPhoneNumber()).child("LastActive").setValue(time);
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_cont,new ChatFragment()).commit();

        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.purple_200));


        ChipNavigationBar navigationBar = findViewById(R.id.chipNavigationBar);
        navigationBar.setItemSelected(R.id.item1,true);

        navigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
//                switch (i){
//                    case R.id.item1:
//                        getSupportFragmentManager().beginTransaction().replace(R.id.main_cont, new ChatFragment()).commit();
//                        break;
//
//                    case R.id.item2:
//                        getSupportFragmentManager().beginTransaction().replace(R.id.main_cont, new ProfileFragment()).commit();
//                        break;
//
//                }
                if (i == R.id.item1) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_cont, new ChatFragment())
                            .commit();
                } else if (i == R.id.item2) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_cont, new ProfileFragment())
                            .commit();
                }
            }
        });
    }

}