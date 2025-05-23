package com.example.ridgwayvacationplanner_d308.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridgwayvacationplanner_d308.R;
import com.example.ridgwayvacationplanner_d308.UI.ExcursionDetails;
import com.example.ridgwayvacationplanner_d308.entities.Excursion;

import java.util.List;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {

    private List<Excursion> mExcursions;
    private final Context context;
    private final LayoutInflater mInflater;
    private int selectedPosition = -1; // Track selected item

    class ExcursionViewHolder extends RecyclerView.ViewHolder {

        private final TextView excursionItemView1;
        private final TextView excursionItemView2;

        private ExcursionViewHolder(View itemView) {
            super(itemView);
            excursionItemView1 = itemView.findViewById(R.id.textViewExcursion1);
            excursionItemView2 = itemView.findViewById(R.id.textViewExcursion2);

            itemView.setOnClickListener(v -> {
                selectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            });
        }
    }

    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.excursion_list_item, parent, false);
        return new ExcursionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        if (mExcursions != null) {
            Excursion current = mExcursions.get(position);
            holder.excursionItemView1.setText(current.getExcursionTitle());
            holder.excursionItemView2.setText(current.getExcursionDate());

            holder.itemView.setBackgroundColor(selectedPosition == position ?
                    context.getResources().getColor(R.color.selected_item) :
                    context.getResources().getColor(android.R.color.transparent));
        } else {
            holder.excursionItemView1.setText("No Excursion Name");
            holder.excursionItemView2.setText("No Date");
        }
    }

    @Override
    public int getItemCount() {
        return mExcursions == null ? 0 : mExcursions.size();
    }

    public ExcursionAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setExcursions(List<Excursion> excursions) {
        mExcursions = excursions;
        notifyDataSetChanged();
    }

    public Excursion getSelectedExcursion() {
        return selectedPosition != -1 ? mExcursions.get(selectedPosition) : null;
    }
}
