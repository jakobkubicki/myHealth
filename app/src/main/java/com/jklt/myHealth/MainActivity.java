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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivityTag";
    ActivityResultLauncher<Intent> launcher;
    DrugOpenHelper helper;
    ArrayList<Drug> apiList = new ArrayList<>();
    CustomAdapter adapter = new CustomAdapter();
    JSONObject jsonResponse = new JSONObject();
    JSONObject jSONObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        helper = new DrugOpenHelper(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        getDatabase();

        launcher = registerForActivityResult( // add drug to user's list
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String resultName = data.getStringExtra("newName");
                        String resultDesc = data.getStringExtra("newDesc");
                        int id = data.getIntExtra("_id", 0);
                        Drug drug = new Drug(id, resultName, resultDesc);
                        helper.insertVideo(drug);
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
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.GET, "https://api.fda.gov/drug/ndc.json?search=finished:true&limit=10", new Response.Listener<String>() { //you can change here POST/GET
            @Override
            public void onResponse(String response) {
                try {
                    jsonResponse = new JSONObject(response);
                    JSONArray locations = jsonResponse.getJSONArray("results");
                    for (int i = 0; i < 10; i++) {
                        jSONObject = locations.getJSONObject(i);
                        System.out.println(jSONObject);
                        String name = jSONObject.getString("brand_name");
                        String desc = "Ingredients: " + jSONObject.getString("generic_name");
                        Drug newDrug = new Drug(name, desc);
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
                System.out.println("Volloy Error " + error);
                Toast.makeText(MainActivity.this, "Network Connection Error...!!!", Toast.LENGTH_SHORT).show();
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
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(request);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView myText1;
            TextView myText2;
            TextView myText3;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                myText1 = itemView.findViewById(R.id.myText1);
                myText2 = itemView.findViewById(R.id. myText2);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            public void updateView(Drug b) {
                myText1.setText(b.toString());
                myText2.setText(b.getDescription());
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DrugDetailActivity.class);
                intent.putExtra("index", getAdapterPosition());
                intent.putExtra("title", apiList.get(getAdapterPosition()).getName());
                intent.putExtra("watched", apiList.get(getAdapterPosition()).getDescription());
                launcher.launch(intent);
            }

            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick: ");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete item")
                        .setMessage("Are you sure you would like to delete this item?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                apiList.remove(getAdapterPosition());
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Dismiss", null);
                builder.show();
                return true;
            }
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this)
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