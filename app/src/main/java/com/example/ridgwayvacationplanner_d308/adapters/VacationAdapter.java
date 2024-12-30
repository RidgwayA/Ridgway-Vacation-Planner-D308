package com.example.ridgwayvacationplanner_d308.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridgwayvacationplanner_d308.R;
import com.example.ridgwayvacationplanner_d308.UI.VacationDetails;
import com.example.ridgwayvacationplanner_d308.entities.Excursion;
import com.example.ridgwayvacationplanner_d308.entities.Vacation;

import java.util.List;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder>{

    private List<Vacation> mVacations;
    private List<Excursion> mExcursions;
    private final Context context;
    private final LayoutInflater mInflater;

    public class VacationViewHolder extends RecyclerView.ViewHolder {
        private final TextView vacationItemView;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            vacationItemView = itemView.findViewById(R.id.textViewVacationItem);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                final Vacation current = mVacations.get(position);

                // Logs to debug clicked vacation data
                Log.d("VacationAdapter", "Clicked position: " + position);
                Log.d("VacationAdapter", "Vacation ID being passed: " + current.getVacationID());
                Log.d("VacationAdapter", "Vacation Title being passed: " + current.getVacationTitle());

                Intent intent = new Intent(context, VacationDetails.class);
                intent.putExtra("vacationId", current.getVacationID());
                intent.putExtra("vacationName", current.getVacationTitle());
                intent.putExtra("hotelName", current.getVacationLodging());
                intent.putExtra("startdate", current.getStartDate());
                intent.putExtra("enddate", current.getEndDate());
                context.startActivity(intent);
            });
        }
    }

    @NonNull
    @Override
    public VacationAdapter.VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.vacation_list_item,parent,false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationAdapter.VacationViewHolder holder, int position) {
        if(mVacations != null){
            Vacation current = mVacations.get(position);
            String name = current.getVacationTitle();
            holder.vacationItemView.setText(name);
        }
        else{
            holder.vacationItemView.setText("No Vacation Name");
        }
    }

    @Override
    public int getItemCount() {
        if(mVacations != null) {
            return mVacations.size();
        }
        else return 0;
    }

    public VacationAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setVacations(List<Vacation> vacations){
        mVacations = vacations;
        notifyDataSetChanged();
    }

    public void setExcursions(List<Excursion> excursions){
        mExcursions = excursions;
        notifyDataSetChanged();
    }

}
