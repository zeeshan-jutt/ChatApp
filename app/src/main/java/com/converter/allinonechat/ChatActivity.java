package com.converter.allinonechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String ReciverImage, ReciverUID, ReciverName, SenderUid;
    CircleImageView ChatImage;
    TextView reciverName;
    FirebaseDatabase database;
    FirebaseAuth auth;
    public static String sImage;
    public static String rImage;
    CardView sendMsg;
    EditText writeMsg;

    String senderRoom, reciverRoom;
    RecyclerView messageAdapter;
    ArrayList<Messages> messagesArrayList;

    MessagesAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        ReciverName=getIntent().getStringExtra("name");
        ReciverImage=getIntent().getStringExtra("ReciverImage");
        ReciverUID=getIntent().getStringExtra("uid");

        messagesArrayList=new ArrayList<>();

        ChatImage=findViewById(R.id.chat_img);
        reciverName=findViewById(R.id.Chat_name);
        sendMsg=findViewById(R.id.sendMsg);
        writeMsg=findViewById(R.id.writeMsg);

        messageAdapter=findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        Adapter=new MessagesAdapter(ChatActivity.this,messagesArrayList );
        messageAdapter.setAdapter(Adapter);


        Picasso.get().load(ReciverImage).into(ChatImage);
        reciverName.setText(""+ReciverName);

        SenderUid=auth.getUid();
        senderRoom=SenderUid+ReciverUID;
        reciverRoom=ReciverUID+SenderUid;

        DatabaseReference reference=database.getReference().child("User").child(auth.getUid());
        DatabaseReference ChatReference=database.getReference().child("Chats").child(senderRoom).child("Messages");

        ChatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();
               for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                   Messages messages=dataSnapshot.getValue(Messages.class);
                   messagesArrayList.add(messages);

               }

               Adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage= snapshot.child("imageUri").getValue().toString();
                rImage=ReciverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=writeMsg.getText().toString();
                if(message.isEmpty()){
                    Toast.makeText(ChatActivity.this,"Message field is Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    writeMsg.setText("");
                    Date date= new Date();
                    Messages messages=new Messages( message, SenderUid, date.getTime());

                    database=FirebaseDatabase.getInstance();
                    database.getReference().child("Chats")
                            .child(senderRoom)
                            .child("Messages").push()
                            .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            database.getReference().child("Chats")
                                    .child(reciverRoom)
                                    .child("Messages").push()
                                    .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }
                    });
                }
            }
        });
    }
}