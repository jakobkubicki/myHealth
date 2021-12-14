package com.jklt.myHealth;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "FirebaseFunTag";

    ActivityResultLauncher<Intent> launcher;

    String userName = "Anonymous";
    List<ChatMessage> chatMessageList;

    // firebase fields
    // we are going to add an object called messages
    DatabaseReference mMessagesDatabaseReference;
    ChildEventListener mMessagesChildEventListener;
    // firebase authentication fields
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Toast.makeText(MainActivity.this, "You are now signed in", Toast.LENGTH_SHORT).show();
                        }
                        else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                            // they backed out of the sign in activity
                            // let's exit
                            finish();
                        }
                    }
                });

        setupFirebase();
    }

    private void setupFirebase() {
        // initialize the firebase references
            FirebaseApp.initializeApp(this);

        // server side setup
        // 1. enable authentication providers like
        // email or google or facebook etc.
        // today we will do email and google
        // 2. return the default values for db
        // read and write to be authenticated
        // client side setup
        // 3. declare a FirebaseAuth.AuthStateListener
        // listens for authentication events
        // signed in and signed out are our two states
        // 4. if the user is signed in...
        // let's get their user name
        // wire up our childeventlistener mMessagesChildEventListener
        // 5. if the user is not signed in...
        // start an activity using FirebaseUI to
        // log our user in
        // 6. wire up the AuthStateListener in onResume()
        // and detach it onPause()
        // 7. add support for the user logging out
        // with an options menu action

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // we have two auth states, signed in and signed out
                // get the get current user, if there is one
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    // step 4
                    setupUserSignedIn(user);
                } else {
                    // user is signed out
                    // step 5
                    // we need an intent
                    // the firebaseUI Github repo README.md
                    // we have used builders before in this class
                    // AlertDialog.Builder
                    // return instance to support chaining
                    Intent intent = AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()
                                    )
                            ).build();
                    launcher.launch(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        // attach the authstatelistener
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // remove it
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void setupUserSignedIn(FirebaseUser user) {
        // get the user's name
        userName = user.getDisplayName();
        String userEmail = user.getEmail();
        // listen for database changes with childeventlistener
        // wire it up!
        //Intent intent = new Intent(MainActivity.this, Messages.class);
        Intent intent = new Intent(MainActivity.this, DrugSearchActivity.class);
        //.putExtra("drug_name","Nexium");
        //intent.putExtra("name", userName);
        //intent.putExtra("email", userEmail);
        launcher.launch(intent);

    }

}