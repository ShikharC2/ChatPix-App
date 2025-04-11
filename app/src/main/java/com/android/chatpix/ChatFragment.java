package com.android.chatpix;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Button btn = (Button) view.findViewById(R.id.out);
        Button newChat = (Button) view.findViewById(R.id.button6);
        ListView list = (ListView) view.findViewById(R.id.chatlst);
        pl.droidsonroids.gif.GifImageView gif = view.findViewById(R.id.gif1);

        List<String> names = new ArrayList<>();
        CustomListAdapter adapter = new CustomListAdapter(getActivity(), names);
        list.setAdapter(adapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child("LastActive").child(currentUser.getPhoneNumber()).child("LastActive").setValue("Active Now");

        reference.child("RecentChat").child(currentUser.getPhoneNumber()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                names.clear();
                for (DataSnapshot shot : snapshot.getChildren()) {
                    names.add(shot.getValue(String.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (names.size() == 0) {
            gif.setVisibility(View.INVISIBLE);
        } else {
            gif.setVisibility(View.VISIBLE);
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), TextChatActivity.class);
                intent.putExtra("fname", names.get(i).substring(3));
                startActivity(intent);
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "Long click", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        newChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.number_dialog);
                EditText input = dialog.findViewById(R.id.editTextTextPersonName2);
                Button find = dialog.findViewById(R.id.button7);

                find.setOnClickListener(new View.OnClickListener() {
                    Boolean isNumRegistered = false;

                    @Override
                    public void onClick(View view) {
                        String number = input.getText().toString();
                        reference.child("Phone").addValueEventListener(new ValueEventListener() {
                            int recentChatCount = 1;

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                reference.child("RecentChat").child("+91" + number).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot shot : snapshot.getChildren()) {
                                            recentChatCount++;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                for (DataSnapshot shot : snapshot.getChildren()) {
                                    if (shot.getKey().equals("+91" + number)) {
                                        if (names.contains("+91" + number)) {
                                            Intent intent = new Intent(getActivity(), TextChatActivity.class);
                                            intent.putExtra("fname", number);
                                            startActivity(intent);
                                            dialog.dismiss();
                                            isNumRegistered = true;
                                            break;
                                        } else {
                                            Intent intent = new Intent(getActivity(), TextChatActivity.class);
                                            intent.putExtra("fname", number);
                                            startActivity(intent);
                                            dialog.dismiss();
                                            names.add(0, "+91" + number);
                                            isNumRegistered = true;
                                            reference.child("RecentChat").child(currentUser.getPhoneNumber()).setValue(names);
                                            reference.child("RecentChat").child("+91" + number).child(String.valueOf(recentChatCount)).setValue(currentUser.getPhoneNumber());
                                            break;
                                        }

                                    }
                                }
                                if (!isNumRegistered) {
                                    Toast.makeText(getActivity(), "This Number is not registered on ChatPix", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                dialog.show();
            }
        });
    }
}