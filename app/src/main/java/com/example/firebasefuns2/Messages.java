package com.example.firebasefuns2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Messages extends AppCompatActivity {
    static final String TAG = "FirebaseFunTag";

    ActivityResultLauncher<Intent> launcher;

    String userName = "Anonymous";
    String chatMessageKey = "test@gmail.com_test2@gmail.com";
    List<ChatMessage> chatMessageList;
    Messages.CustomAdapter adapter;
    String userEmail;

    // firebase fields
    FirebaseDatabase mFirebaseDatabase;
    // we are going to add an object called messages
    DatabaseReference mMessagesDatabaseReference;
    ChildEventListener mMessagesChildEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        Button send = findViewById(R.id.sendButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendButtonClick();
            }
        });

        Intent data = getIntent();
        userName = data.getStringExtra("name");
        userEmail = data.getStringExtra("email");

        Button submitEmail = findViewById(R.id.sendEmail);
        submitEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = findViewById(R.id.provider);
                String personEmail = text.getText().toString();
                if(personEmail.compareTo(userEmail) > 0){
                    chatMessageKey = userEmail + "_" + personEmail;
                }else{
                    chatMessageKey = personEmail + "_" + userEmail;
                }
                TextView textPrompt = findViewById(R.id.prompt);
                textPrompt.setVisibility(View.GONE);
                submitEmail.setVisibility(View.GONE);
                text.setVisibility(View.GONE);
                RecyclerView thisview = findViewById(R.id.recyclerView);
                thisview.setVisibility(View.VISIBLE);
                send.setVisibility(View.VISIBLE);
                EditText chat = findViewById(R.id.editText);
                chat.setVisibility(View.VISIBLE);


                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                LinearLayoutManager layoutManager = new LinearLayoutManager(Messages.this);
                layoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(layoutManager);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Messages.this, DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(dividerItemDecoration);
                adapter = new Messages.CustomAdapter();
                recyclerView.setAdapter(adapter);

                setupFirebase();
            }
        });


        chatMessageList = new ArrayList<>();

    }

    private void setupFirebase() {
        // initialize the firebase references
      //  FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        chatMessageKey = chatMessageKey.replaceAll("\\.","");
        try{
            mMessagesDatabaseReference =
                    mFirebaseDatabase.getReference().child("Chats").child(chatMessageKey);
        }catch(Exception e){

            mMessagesDatabaseReference =
                    mFirebaseDatabase.getReference().child("Chats");
            mMessagesDatabaseReference.child(chatMessageKey).setValue("hi");
            mMessagesDatabaseReference =
                    mFirebaseDatabase.getReference().child("Chats").child(chatMessageKey);

        }


        mMessagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // called for each message already in our db
                // called for each new message add to our db
                // dataSnapshot stores the ChatMessage
                Log.d(TAG, "onChildAdded: " + s);
                ChatMessage chatMessage =
                        dataSnapshot.getValue(ChatMessage.class);
                // add it to our list and notify our adapter
                chatMessageList.add(chatMessage);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mMessagesDatabaseReference.addChildEventListener(mMessagesChildEventListener);

    }


    public void onSendButtonClick (){
        // show a log message
        Log.d(TAG, "onSendButtonClick: ");
        // push up to "messages" whatever is
        // in the edittext
        EditText editText = (EditText)
                findViewById(R.id.editText);
        String currText = editText.getText().toString();
        if (currText.isEmpty()) {
            Toast.makeText(this, "Please enter a message first", Toast.LENGTH_SHORT).show();
        } else {
            // we have a message to send
            // create a ChatMessage object to push
            // to the database
            ChatMessage chatMessage = new
                    ChatMessage(userName,
                    currText);
            mMessagesDatabaseReference
                    .push()
                    .setValue(chatMessage);
            // warmup task #1
            editText.setText("");

        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_signout) {
            AuthUI.getInstance().signOut(this);
            chatMessageList.clear();
            adapter.notifyDataSetChanged();
            mMessagesDatabaseReference.removeEventListener(mMessagesChildEventListener);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    class CustomAdapter extends RecyclerView.Adapter<Messages.CustomAdapter.CustomViewHolder> {
        class CustomViewHolder extends RecyclerView.ViewHolder {
            TextView header;
            TextView body;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                header = itemView.findViewById(R.id.header);
                body = itemView.findViewById(R.id.body);
            }

            public void updateView(ChatMessage c) {
                header.setText(c.getAuthor());
                header.setTypeface(header.getTypeface(), Typeface.BOLD);
                body.setText(c.getContent());
            }
        }

        @NonNull
        @Override
        public Messages.CustomAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(Messages.this)
                    .inflate(R.layout.card_message, parent, false);
            return new Messages.CustomAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Messages.CustomAdapter.CustomViewHolder holder, int position) {
            holder.updateView(chatMessageList.get(position));
        }

        @Override
        public int getItemCount() {
            return chatMessageList.size();
        }
    }
}

