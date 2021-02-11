package com.mygreenmc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mygreenmc.data.Task;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SelectTaskAdapter extends RecyclerView.Adapter<SelectTaskAdapter.ViewHolder> {

    private ArrayList<Task> tasks;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView description;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            name = view.findViewById(R.id.name);
            description = view.findViewById(R.id.description);
        }

        public TextView getName() {
            return name;
        }
        public TextView getDescription() {
            return description;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public SelectTaskAdapter(ArrayList<Task> dataSet) {
        tasks = dataSet;
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
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return tasks.size();
    }
}

