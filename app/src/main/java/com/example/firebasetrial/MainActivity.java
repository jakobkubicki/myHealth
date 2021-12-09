package com.example.firebasetrial;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference myDatabase;
    private String name = "default";
    private Boolean setName = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDatabase = FirebaseDatabase.getInstance().getReference("Message");

        final TextView myText = findViewById(R.id.textView);

       /* Button myName = findViewById(R.id.nameB);
        myName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText username = findViewById(R.id.name);
                name = username.getText().toString();
                setName = true;
            }
        });*/

        myDatabase.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] Messages = dataSnapshot.getValue().toString().split(",");
                myText.setText(""); // Cleaning the text Area

               for (int i=0; i<Messages.length;i++){
                    String[] finalMsg = Messages[i].split("=");
                    myText.append(finalMsg[1] + "\n");
                }
             //   myText.setText(dataSnapshot.getValue().toString());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                myText.setText("CANCELLED");

            }
        });
    }

    public void sendMessage(View view){
        EditText myEditText = findViewById(R.id.editText);

        myDatabase.push().setValue(myEditText.getText().toString());
        //myDatabase.child(Long.toString(System.currentTimeMillis())).setValue(myEditText.getText().toString());
        myEditText.setText("");
    }
}