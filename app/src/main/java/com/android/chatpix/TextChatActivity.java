package com.android.chatpix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TextChatActivity extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_chat);


        TextView fullname = findViewById(R.id.textView13);
        TextView status = findViewById(R.id.textView14);
        ImageButton back = findViewById(R.id.imageButton3);
        ImageButton send = findViewById(R.id.button4);
        ListView listView = findViewById(R.id.listview5);
        EditText message = findViewById(R.id.textView15);
        ImageView dp = findViewById(R.id.chatDp2);

        Date date = new Date();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users");
        Intent intent = getIntent();
        String recNum = intent.getStringExtra("fname");

        StorageReference storage = FirebaseStorage.getInstance().getReference("Users");
        storage.child("+91"+recNum).child("Profile").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(dp);
            }
        });
        database.child("Phone").child("+91" + recNum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullname.setText(snapshot.child("Name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        database.child("LastActive").child("+91"+recNum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lstSeen = snapshot.child("LastActive").getValue().toString();
//                status.setText(lstSeen);
                runThread(status,recNum,lstSeen);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        final String sender = firebaseUser.getPhoneNumber() + "+91" + recNum;
        final String receiver = "+91" + recNum + firebaseUser.getPhoneNumber();

        List<ChatConstructor> list = new ArrayList<>();
        CustomChatAdaptor adaptor = new CustomChatAdaptor(getApplicationContext(), list);
        listView.setDivider(null);
        listView.setAdapter(adaptor);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot shot : snapshot.child("Chats").child(sender).getChildren()) {
                    ChatConstructor val = shot.getValue(ChatConstructor.class);
                    list.add(val);
                }
                if(!list.isEmpty()){
                    database.child("RecentMessage").child(sender).child("Message").setValue(list.get(list.size()-1).message);
                }
//                MediaPlayer player = MediaPlayer.create(getApplicationContext(),R.raw.inter1);
//                player.start();
                adaptor.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = message.getText().toString();
                message.setText("");
                if (msg.isEmpty()) {
                    message.setError("Enter a message to send");
                } else {
                    String localTime = String.valueOf(date.getHours()) + ":" + String.valueOf(date.getMinutes());
                    MediaPlayer player = MediaPlayer.create(getApplicationContext(), R.raw.sendersound);
                    player.start();
                    ChatConstructor constructor = new ChatConstructor(msg, "true", localTime,"false");
                    ChatConstructor constructor1 = new ChatConstructor(msg, "false", localTime,"false");
                    database.child("Chats").child(sender).push().setValue(constructor);
                    database.child("Chats").child(receiver).push().setValue(constructor1);
                    database.child("RecentMessage").child(sender).child("Message").setValue(msg);
                    database.child("RecentMessage").child(sender).child("IsNew").setValue("true");
                    database.child("RecentMessage").child(sender).child("IsSender").setValue("true");
                }
            }
        });
    }

    int currPos= 0;

    public void runThread(TextView status,String recNum,String lstSeen){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.new_chat_anim);
        String[] lst = {lstSeen,recNum};
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(currPos==0){
                    status.startAnimation(animation);
                    status.setText("Last Active at "+lst[currPos]);
                }
                else{
                    status.startAnimation(animation);
                    status.setText("+91"+lst[currPos]);
                }
                currPos = (currPos+1)%lst.length;
                handler.postDelayed(this,10000);
            }
        });
    }
    public void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }
}
