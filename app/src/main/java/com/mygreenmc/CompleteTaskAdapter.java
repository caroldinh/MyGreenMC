package com.mygreenmc;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mygreenmc.data.BooleanTask;
import com.mygreenmc.data.IntTask;
import com.mygreenmc.data.Task;

import java.util.ArrayList;

public class CompleteTaskAdapter extends RecyclerView.Adapter<CompleteTaskAdapter.ViewHolder> {

    private ArrayList<Task> tasks;
    private Context context;
    private String recycler;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView description;
        private final CardView card;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            name = view.findViewById(R.id.name);
            description = view.findViewById(R.id.description);
            card = view.findViewById(R.id.card_view);
        }

        public TextView getName() {
            return name;
        }
        public TextView getDescription() {
            return description;
        }
        public CardView getCard(){ return card;}
    }

    public static class IntViewHolder extends ViewHolder{

        private final EditText entry;
        private final TextView checkVal;

        public IntViewHolder(View view){
            super(view);
            entry = view.findViewById(R.id.textInput);
            checkVal = view.findViewById(R.id.checkVal);

        }

        public EditText getEntry(){ return entry; }
        public TextView getCheckVal() { return checkVal; }
    }

    public static class BoolViewHolder extends ViewHolder{
        private final Switch switcher;

        public BoolViewHolder(View view){
            super(view);
            switcher = view.findViewById(R.id.switch1);
        }

        public Switch getSwitcher() { return switcher; }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public CompleteTaskAdapter(ArrayList<Task> dataSet, Context context, String recycler) {
        this.tasks = dataSet;
        this.context = context;
        this.recycler = recycler;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if(tasks.get(position) instanceof IntTask){
            return 0;
        } else if (tasks.get(position) instanceof BooleanTask){
            return 1;
        } else{
            return -1;
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view;

        switch(viewType){
            case 0:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.int_task_card, viewGroup, false);
                return new IntViewHolder(view);
            default:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.bool_task_card, viewGroup, false);
                return new BoolViewHolder(view);

        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getName().setText(tasks.get(position).getName());
        viewHolder.getDescription().setText(tasks.get(position).getDescription());

        if(tasks.get(position).getCategory().equals("Water")){
            viewHolder.getCard().setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue));
        } else if(tasks.get(position).getCategory().equals("Electricity")){
            viewHolder.getCard().setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorSecondary));
        }else if(tasks.get(position).getCategory().equals("Transportation")){
            viewHolder.getCard().setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else if(tasks.get(position).getCategory().equals("Home")){
            viewHolder.getCard().setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        switch(getItemViewType(position)){
            case 0:
                ((IntViewHolder) viewHolder).getEntry().setText(Integer.toString(((IntTask)tasks.get(position)).getInput()));
                ((IntViewHolder) viewHolder).getEntry().setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    // When focus is lost check that the text field has valid values.
                    try {

                        if (!hasFocus) {
                            String s = ((IntViewHolder) viewHolder).getEntry().getText().toString();
                            ((IntTask) tasks.get(position)).setInput(Integer.parseInt(s));
                            Intent intent = new Intent("custom-message");
                            intent.putExtra("RECYCLER", recycler);
                            intent.putExtra("POSITION", position);
                            intent.putExtra("VERIFIED", tasks.get(position).verify());
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                        }
                    } catch (Exception e){}

                }});
                ((IntViewHolder) viewHolder).getCheckVal().setText("Goal: " + ((IntTask)tasks.get(position)).getCheckVal());
                break;
            case 1:
                ((BoolViewHolder) viewHolder).getSwitcher().setChecked(((BooleanTask)tasks.get(position)).getInput());
                ((BoolViewHolder) viewHolder).getSwitcher().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        try{

                            ((BooleanTask)tasks.get(position)).setInput(isChecked);
                            Intent intent = new Intent("custom-message");
                            intent.putExtra("RECYCLER", recycler);
                            intent.putExtra("POSITION", position);
                            intent.putExtra("VERIFIED", tasks.get(position).verify());
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                        } catch(Exception e){}

                    }

                });
                break;
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return tasks.size();
    }
}

