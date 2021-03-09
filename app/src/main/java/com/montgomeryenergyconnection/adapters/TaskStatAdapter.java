package com.montgomeryenergyconnection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.montgomeryenergyconnection.R;
import com.montgomeryenergyconnection.data.Task;
import com.montgomeryenergyconnection.data.User;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TaskStatAdapter extends RecyclerView.Adapter<TaskStatAdapter.ViewHolder> {

    private ArrayList<Task> tasks;
    private Context context;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, streak, pointVal;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            name = view.findViewById(R.id.name);
            streak = view.findViewById(R.id.streak);
            pointVal = view.findViewById(R.id.pointVal);
        }

        public TextView getName() {
            return name;
        }
        public TextView getStreak() {
            return streak;
        }
        public TextView getPointVal() { return pointVal; }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public TaskStatAdapter(ArrayList<Task> dataSet, Context context) {
        this.tasks = dataSet;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.task_stat, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getName().setText(tasks.get(position).getName());
        viewHolder.getPointVal().setText(Integer.toString(tasks.get(position).getPointVal()));
        viewHolder.getStreak().setText(Integer.toString(tasks.get(position).getStreak()));


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return tasks.size();
    }
}

