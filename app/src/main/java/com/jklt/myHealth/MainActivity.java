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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivityTag";
    ActivityResultLauncher<Intent> launcher;
    DrugOpenHelper helper;
    CustomAdapter adapter = new CustomAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        helper = new DrugOpenHelper(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);

        Drug drug2 = new Drug("New Drug", "Description");
        helper.insertVideo(drug2);
        adapter.notifyDataSetChanged();

        launcher = registerForActivityResult(
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            Intent intent = new Intent(MainActivity.this, DrugDetailActivity.class);
            intent.putExtra("index", adapter.getItemCount() - 1);
            intent.putExtra("title", "");
            intent.putExtra("watched", false);
            intent.putExtra("type", 0);
            launcher.launch(intent);
        } else if (id == R.id.trash){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Delete item")
                    .setMessage("Are you sure you would like to delete all items?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            helper.deleteAllContacts();
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Dismiss", null);
            builder.show();
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
            ImageView myImage1;
            CardView myCardView1;
            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                myCardView1 = itemView.findViewById(R.id.myCardView1);
                selectedCards.add(myCardView1);
                myText1 = itemView.findViewById(R.id.myText1);
                myImage1 = itemView.findViewById(R.id.myImage1);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            public void updateView(Drug b) {
                myText1.setText(b.toString());
                myImage1.setImageResource(R.drawable.placeholder);
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
                    Intent intent = new Intent(MainActivity.this, DrugDetailActivity.class);
                    intent.putExtra("index", getAdapterPosition());
                    intent.putExtra("name", helper.getSelectVideoById(v.getID()).getName());
                    intent.putExtra("description", helper.getSelectVideoById(v.getID()).getDescription());
                    launcher.launch(intent);
                }
            }

            @Override
            public void onClick(View v) {
                selectItem(helper.getSelectAllVideos().get(getAdapterPosition()));
            }

            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick: ");
                MainActivity.this.startActionMode(callbacks);
                selectItem(helper.getSelectAllVideos().get(getAdapterPosition()));
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Delete item")
                                    .setMessage("Are you sure you would like to delete these items?")
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (selectedCards.size() >= 0) {
                                                for (int j = 0; j < selectedItems.size(); j++) {
                                                    helper.deleteContactById(selectedItems.get(j).getID());
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
            View view = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.card_view_list_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            Drug b = helper.getSelectAllVideos().get(position);
            holder.updateView(b);
        }

        @Override
        public int getItemCount() {
            return helper.getSelectAllVideos().size();
        }
    }
}