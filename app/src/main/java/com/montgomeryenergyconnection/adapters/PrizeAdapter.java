package com.montgomeryenergyconnection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.montgomeryenergyconnection.R;
import com.montgomeryenergyconnection.data.Task;

import java.util.ArrayList;

public class PrizeAdapter extends RecyclerView.Adapter<PrizeAdapter.ViewHolder> {

    private String[][] prizes;
    private Context context;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, pointVal;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            name = view.findViewById(R.id.name);
            pointVal = view.findViewById(R.id.pointVal);
        }

        public TextView getName() {
            return name;
        }
        public TextView getPointVal() { return pointVal; }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public PrizeAdapter(String[][] dataSet, Context context) {
        this.prizes = dataSet;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.prize_card, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getName().setText(prizes[position][0]);
        viewHolder.getPointVal().setText("Points required: " + prizes[position][1]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return prizes.length;
    }
}

