package com.jklt.myHealth;
/**
 * This program is a watch list for any type of video content
 * CPSC 312-01, Fall 2021
 * Programming Assignment #6
 * No sources to cite.
 *
 * @authors Jakob Kubicki & Lin Ai Tan
 * @version v5.0 11/10/21
 */

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DrugSearchActivity extends AppCompatActivity {
    static final String TAG = "MainActivityTag";
    ActivityResultLauncher<Intent> launcher;
    DrugOpenHelper helper;
    ArrayList<Drug> apiList = new ArrayList<>();
    CustomAdapter adapter = new CustomAdapter();
    JSONObject jsonResponse = new JSONObject();
    JSONObject jSONObject;
    EditText search;
    Button searchButton;
    String email;
    String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_search);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        helper = new DrugOpenHelper(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        Intent dataU = getIntent();
        email = dataU.getStringExtra("email");
        userName = dataU.getStringExtra("name");
        search = findViewById(R.id.editTextSearch);
        searchButton = findViewById(R.id.buttonSearch);
        Button viewPrice = findViewById(R.id.viewPrice);
        viewPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrugSearchActivity.this, Prices.class);
                intent.putExtra("drug_name",search.getText().toString());
                launcher.launch(intent);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                viewPrice.setVisibility(View.VISIBLE);
                apiList.clear();
                adapter.notifyDataSetChanged();
                getDatabase();
            }
        });

        launcher = registerForActivityResult( // add drug to user's list
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String resultName = data.getStringExtra("name");
                        String resultDesc = data.getStringExtra("dosage");
                        int id = data.getIntExtra("_id", 0);
                        Drug drug = new Drug(id, resultName, resultDesc, "");
                        helper.insertDrug(drug);
                        adapter.notifyDataSetChanged();
                        Context context = getApplicationContext();
                        CharSequence text = "Successfully saved!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });
    }

    public void getDatabase(){
        String searchText = search.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.GET, "https://api.fda.gov/drug/ndc.json?search=generic_name:" + searchText + "&limit=10", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jsonResponse = new JSONObject(response);
                    JSONArray locations = jsonResponse.getJSONArray("results");
                    for (int i = 0; i < locations.length(); i++) {
                        jSONObject = locations.getJSONObject(i);
                        System.out.println(jSONObject);
                        String name = jSONObject.getString("generic_name");
                        String strength = "Form of dosage: " + jSONObject.getString("dosage_form");
                        String manu = "Manufacturer: " + jSONObject.getString("labeler_name");
                        Drug newDrug = new Drug(name, strength, manu);
                        apiList.add(newDrug);
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Volley Error " + error);
                Toast.makeText(DrugSearchActivity.this, "Network Connection Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() { return 50000; }

            @Override
            public int getCurrentRetryCount() { return 50000; }

            @Override
            public void retry(VolleyError error) throws VolleyError { }
        });
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case R.id.add:
                Intent intent = new Intent(DrugSearchActivity.this, DrugDetailActivity.class);
                launcher.launch(intent);
                return true;
            case R.id.list:
                Intent intent2 = new Intent(DrugSearchActivity.this, DrugList.class);
                intent2.putExtra("name",userName);
                intent2.putExtra("email",email);
                launcher.launch(intent2);
                return true;
            case R.id.currentLocation:
                Intent intent3 = new Intent(DrugSearchActivity.this, PharmActivity.class);
                launcher.launch(intent3);
                return true;
            case R.id.price:
                Intent intent4 = new Intent(DrugSearchActivity.this, Prices.class);
                intent4.putExtra("drug_name","");
                launcher.launch(intent4);
                return true;
            case R.id.message:
                Intent intent5 = new Intent(DrugSearchActivity.this, Messages.class);
                intent5.putExtra("name",userName);
                intent5.putExtra("email",email);
                launcher.launch(intent5);
                return true;
            case R.id.search:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView myText1;
            TextView myText2;
            TextView myText3;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                myText1 = itemView.findViewById(R.id.myText1);
                myText2 = itemView.findViewById(R.id.myText2);
                myText3 = itemView.findViewById(R.id.myText3);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            public void updateView(Drug b) {
                myText1.setText(b.toString());
                myText2.setText(b.getDescription());
                myText3.setText(b.getManufacturer());
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DrugSearchActivity.this, DrugDetailActivity.class);
                intent.putExtra("index", getAdapterPosition());
                intent.putExtra("name", apiList.get(getAdapterPosition()).getName());
                intent.putExtra("strength", apiList.get(getAdapterPosition()).getDescription());
                launcher.launch(intent);
            }

            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(DrugSearchActivity.this)
                    .inflate(R.layout.card_view_list_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            Drug b = apiList.get(position);
            holder.updateView(b);
        }

        @Override
        public int getItemCount() {
            return apiList.size();
        }
    }
}