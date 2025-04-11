package com.android.chatpix;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    List<String> names;
    LayoutInflater inflater;
    public CustomListAdapter(Context context, List<String> names){
        this.names = names;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int i) {
        return names.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view = inflater.inflate(R.layout.chat_list,viewGroup,false);
        }
        TextView textView = view.findViewById(R.id.textView16);
        ImageView dp = view.findViewById(R.id.imageView6);
        TextView recentSend = view.findViewById(R.id.textView25);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storage = FirebaseStorage.getInstance().getReference("Users");
        storage.child(names.get(i)).child("Profile").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(dp);
            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child("Phone").child(names.get(i)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.child("Name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        textView.setText((CharSequence) names.get(i));
        reference.child("RecentMessage").child(currentUser.getPhoneNumber()+names.get(i)).child("Message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(inflater.getContext(), ""+names.get(i), Toast.LENGTH_SHORT).show();
                if(!(snapshot.getValue() ==null)){
                    recentSend.setText(snapshot.getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}
