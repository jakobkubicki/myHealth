package com.jklt.myHealth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Prices  extends AppCompatActivity {
    static final String TAG = "PricesTag";

    CustomAdapter adapter = new CustomAdapter();

    PricesHelper helper = new PricesHelper(this);
    String drug_name;
    ActivityResultLauncher<Intent> launcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button


        //adapter.notifyDataSetChanged();
        if(helper.getAllPrices().size() == 0){
            Price p1 = new Price(0,"Amazon", 4.49, "https://www.amazon.com", "Advil");
            Price p2 = new Price(0, "Safeway",7.99,"https://www.safeway.com","Advil");
            Price p3 = new Price(0,"CVS",12.49,"https://www.cvs.com","Nexium");
            Price p4 = new Price(0,"Safeway",19.79,"https://www.safeway.com/","Nexium");

            helper.insertPrice(p1);
            helper.insertPrice(p2);
            helper.insertPrice(p3);
            helper.insertPrice(p4);
        }

        Intent data = getIntent();
        drug_name = data.getStringExtra("drug_name");
        if((drug_name.compareTo("") == 0) ||(helper.getPricesByDrug(drug_name).size() > 0)){
            setContentView(R.layout.prices_layout);
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setAdapter(adapter);
        }else{
            setContentView(R.layout.empty_prices);
        }

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Log.d(TAG, "onActivityResult: ");
                    }
        });



    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView drug;
            TextView seller;
            TextView price;

            //DVC
            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);

                drug = itemView.findViewById(R.id.drug);
                seller = itemView.findViewById(R.id.seller);
                price = itemView.findViewById(R.id.price);

                // wire 'em up!!
                itemView.setOnClickListener(this);

            }
            /**
             * Updates view of a card given a video object
             * Updates view of a card given a video object
             *
             * @param: Bundle saved instance state
             * @return
             */
            public void updateView(Price p) {
                drug.setText("Drug name: " + p.getDrug());
                seller.setText("Provider: " + p.getSeller());
                price.setText("Price: " + p.getPrice().toString() + " USD");
            }

            /**
             * Handles short clicks on a card, by lanuching a seconf activty that will handle all logic of a single card
             *
             * @param: a view (card)
             * @return
             */
            @Override
            public void onClick(View v) {
                if(drug_name.compareTo("") == 0){
                    Price p = helper.getAllPrices().get(getAdapterPosition());
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(p.getWebsite()));
                    startActivity(i);
                }else{
                    Price p = helper.getPricesByDrug(drug_name).get(getAdapterPosition());
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(p.getWebsite()));
                    startActivity(i);
                }
            }

        }

            /**
             onCreateViewHolder creates a view holder for recycler view to use
             *
             * @param: view group to inflate a particular list item view
             * @return
             */
        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(Prices.this)
                    .inflate(R.layout.card_price, parent, false);
            return new CustomViewHolder(view);
        }

        /**
         * OnBindViewHolder binds a view to an available holder if available for use/reuse
         *
         * @param: view holder and position with respect to data source
         * @return
         */
        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            if(drug_name.compareTo("") == 0){
                Price p = helper.getAllPrices().get(position);
                holder.updateView(p);
            }else{
                Log.d(TAG,""+position);
                Price p = helper.getPricesByDrug(drug_name).get(position);
                if(p.getDrug().compareTo(drug_name) == 0){
                    holder.updateView(p);
                }
            }
        }


        public int getDrugCount(String name) {
            int size = helper.getPricesByDrug(name).size();
            Log.d(TAG,""+size);
            return size;
        }

        @Override
        public int getItemCount() {
            if(drug_name.compareTo("") == 0){
                return helper.getAllPrices().size();
            }
            return getDrugCount(drug_name);
        }


    }
}
