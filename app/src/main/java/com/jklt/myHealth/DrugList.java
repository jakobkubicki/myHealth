package com.jklt.myHealth;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DrugList extends AppCompatActivity {
    ActivityResultLauncher<Intent> launcher;
    DrugOpenHelper helper;
    CustomAdapter adapter = new CustomAdapter();
    String username;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_list);

        Intent dataU = getIntent();
        username = dataU.getStringExtra("name");
        email= dataU.getStringExtra("email");
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        helper = new DrugOpenHelper(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

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
                Intent intent = new Intent(DrugList.this, DrugDetailActivity.class);
                launcher.launch(intent);
                return true;
            case R.id.list:
                Intent intent2 = new Intent(DrugList.this, DrugList.class);
                launcher.launch(intent2);
                return true;
            case R.id.currentLocation:
                Intent intent3 = new Intent(DrugList.this, PharmActivity.class);
                launcher.launch(intent3);
                return true;
            case R.id.price:
                Intent intent4 = new Intent(DrugList.this, Prices.class);
                intent4.putExtra("drug_name","");
                launcher.launch(intent4);
                return true;
            case R.id.message:
                Intent intent5 = new Intent(DrugList.this, Messages.class);
                intent5.putExtra("name",username);
                intent5.putExtra("email",email);
                launcher.launch(intent5);
                return true;
            case R.id.search:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        boolean multiSelect = false;
        ActionMode actionMode;
        ActionMode.Callback callbacks;
        List<Drug> selectedItems = new ArrayList<>();
        List<CardView> selectedCards = new ArrayList<>();

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView myText1;
            TextView myText2;
            CardView myCardView1;
            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                myCardView1 = itemView.findViewById(R.id.myCardView1);
                selectedCards.add(myCardView1);
                myText1 = itemView.findViewById(R.id.myText1);
                myText2 = itemView.findViewById(R.id.myText2);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            public void updateView(Drug b) {
                myText1.setText(b.getName());
                myText2.setText(b.getDescription());
            }

            public void selectItem(Drug v) {
                if (multiSelect) {
                    if (selectedItems.contains(v)) {
                        selectedItems.remove(v);
                        myCardView1.setCardBackgroundColor(getResources().getColor(R.color.white));
                        notifyDataSetChanged();
                    }
                    else {
                        selectedItems.add(v);
                        myCardView1.setCardBackgroundColor(getResources().getColor(R.color.teal_200));
                    }
                    actionMode.setTitle(selectedItems.size() + " item(s) selected");
                } else {
                    Intent intent = new Intent(DrugList.this, DrugDetailActivity.class);
                    intent.putExtra("index", getAdapterPosition());
                    intent.putExtra("name", helper.getSelectDrugById(v.getID()).getName());
                    intent.putExtra("dosage", helper.getSelectDrugById(v.getID()).getDescription());
                    launcher.launch(intent);
                }
            }

            @Override
            public void onClick(View v) {
                selectItem(helper.getSelectAllDrugs().get(getAdapterPosition()));
            }

            @Override
            public boolean onLongClick(View v) {
                DrugList.this.startActionMode(callbacks);
                selectItem(helper.getSelectAllDrugs().get(getAdapterPosition()));
                return true;
            }
        }

        public CustomAdapter() {
            super();
            callbacks = new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    multiSelect = true;
                    actionMode = mode;
                    MenuInflater menuInflater = getMenuInflater();
                    menuInflater.inflate(R.menu.cam_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.deleteMenuItem:
                            AlertDialog.Builder builder = new AlertDialog.Builder(DrugList.this);
                            builder.setTitle("Delete item")
                                    .setMessage("Are you sure you would like to delete these items?")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (selectedCards.size() >= 0) {
                                                for (int j = 0; j < selectedItems.size(); j++) {
                                                    helper.deleteDrugById(selectedItems.get(j).getID());
                                                }
                                            }
                                            mode.finish();
                                            notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton("Dismiss", null);
                            builder.show();
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    multiSelect = false;
                    for (int i = 0; i < selectedCards.size(); i++){
                        selectedCards.get(i).setCardBackgroundColor(getResources().getColor(R.color.white));
                    }
                    selectedItems.clear();
                    selectedCards.clear();
                    notifyDataSetChanged();
                }
            };
        }

        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(DrugList.this)
                    .inflate(R.layout.card_view_list_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            Drug b = helper.getSelectAllDrugs().get(position);
            holder.updateView(b);
        }

        @Override
        public int getItemCount() {
            return helper.getSelectAllDrugs().size();
        }
    }
}
