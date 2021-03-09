package com.montgomeryenergyconnection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.montgomeryenergyconnection.R;
import com.montgomeryenergyconnection.data.Task;
import com.montgomeryenergyconnection.data.User;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SelectTaskAdapter extends RecyclerView.Adapter<SelectTaskAdapter.ViewHolder> {

    private ArrayList<Task> tasks;
    private Context context;
    private User user;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView description;
        private final CheckBox check;
        private final CardView card;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            name = view.findViewById(R.id.name);
            description = view.findViewById(R.id.description);
            check = view.findViewById(R.id.checkBox);
            card = view.findViewById(R.id.card_view);
        }

        public TextView getName() {
            return name;
        }
        public TextView getDescription() {
            return description;
        }
        public CheckBox getCheck() { return check; }
        public CardView getCard(){ return card;}
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public SelectTaskAdapter(ArrayList<Task> dataSet, Context context, User user) {
        this.tasks = dataSet;
        this.context = context;
        this.user = user;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.task_card, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getName().setText(tasks.get(position).getName());
        viewHolder.getDescription().setText(tasks.get(position).getDescription());

        ArrayList<String> myTasks = new ArrayList<String>();
        for(Task task : user.getInProgress()){
            myTasks.add(task.getName());
        }
        for(Task task : user.getComplete()){
            myTasks.add(task.getName());
        }
        viewHolder.getCheck().setChecked(myTasks.indexOf(tasks.get(position).getName()) != -1 || myTasks.indexOf(tasks.get(position).getName()) != -1);
        //user.getComplete().indexOf(tasks.get(position)) != -1 || user.getInProgress().indexOf(tasks.get(position)) != -1

        if(tasks.get(position).getCategory().equals("Water")){
            viewHolder.getCard().setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue));
        } else if(tasks.get(position).getCategory().equals("Electricity")){
            viewHolder.getCard().setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        }else if(tasks.get(position).getCategory().equals("Transportation")){
            viewHolder.getCard().setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else if(tasks.get(position).getCategory().equals("Home")){
            viewHolder.getCard().setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        viewHolder.getCard().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox check = v.findViewById(R.id.checkBox);
                check.setChecked(!check.isChecked());
            }});

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return tasks.size();
    }
}

