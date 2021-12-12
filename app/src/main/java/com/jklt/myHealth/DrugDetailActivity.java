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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DrugDetailActivity extends AppCompatActivity {
    String importedName = "";
    String importedDose = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_detail);

        Intent intent = getIntent();
        if (intent != null) {
            importedName = intent.getStringExtra("name");
            importedDose = intent.getStringExtra("dosage");
        }


        EditText editText = findViewById(R.id.name);
        EditText editText2 = findViewById(R.id.dosage);
        editText.setText(importedName);
        editText2.setText(importedDose);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString();
                importedName = name;
            }
        });
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    String dose = s.toString();
                    importedDose = dose;
                }
            }
        });


        Button backButton = findViewById(R.id.save);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                System.out.println(importedDose);
                intent.putExtra("name", importedName);
                intent.putExtra("dose", importedDose);
                DrugDetailActivity.this.setResult(DrugList.RESULT_OK, intent);
                DrugDetailActivity.this.finish();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}