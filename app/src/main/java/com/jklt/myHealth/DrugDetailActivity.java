/**
 * This program is a watch list for any type of video content
 * CPSC 312-01, Fall 2021
 * Programming Assignment #6
 * No sources to cite.
 *
 * @author Jakob Kubicki
 * @version v5.0 11/10/21
 */
package com.jklt.myHealth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DrugDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_detail);

        String importedName = "";
        String importedDose = "";
        Intent intent = getIntent();
        if (intent != null) {
            importedName = intent.getStringExtra("name");
            importedDose = intent.getStringExtra("name");
        }

        EditText name = findViewById(R.id.name);

        name.setText(importedName);

        Button backButton = findViewById(R.id.save);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}