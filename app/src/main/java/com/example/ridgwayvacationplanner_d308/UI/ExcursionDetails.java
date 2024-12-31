package com.example.ridgwayvacationplanner_d308.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ridgwayvacationplanner_d308.R;
import com.example.ridgwayvacationplanner_d308.adapters.ExcursionAdapter;
import com.example.ridgwayvacationplanner_d308.database.Repository;
import com.example.ridgwayvacationplanner_d308.entities.Excursion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {

    private int vacationId;
    private String vacationName, vacationStartDate, vacationEndDate, hotelName;
    private Repository repository;
    private EditText excursionNameInput, excursionDateInput;
    private RecyclerView excursionRecyclerView;
    private ExcursionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_details);

        repository = new Repository(getApplication());

        // Extract vacation data
        Intent intent = getIntent();
        vacationId = intent.getIntExtra("vacationId", -1);
        vacationName = intent.getStringExtra("vacationName");
        vacationStartDate = intent.getStringExtra("startdate");
        vacationEndDate = intent.getStringExtra("enddate");
        hotelName = intent.getStringExtra("hotelName");

        initViews();
        populateVacationDetails();
        setupRecyclerView();
        setupAddButton();
        setupDeleteButton();
        setupSetAlertButton();
        setupEditButton();
        setupCancelEditButton();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void initViews() {
        TextView vacationDetailsText = findViewById(R.id.vacationDetailsText);
        vacationDetailsText.setText(String.format(
                "Vacation: %s\nHotel: %s\nStart: %s\nEnd: %s",
                vacationName, hotelName, vacationStartDate, vacationEndDate
        ));

        excursionNameInput = findViewById(R.id.excursionNameInput);
        excursionDateInput = findViewById(R.id.excursionDateInput);
        Button addExcursionButton = findViewById(R.id.addExcursionButton);
        addExcursionButton.setOnClickListener(v -> addExcursion());
    }

    private void populateVacationDetails() {
        TextView vacationDetailsText = findViewById(R.id.vacationDetailsText);
        vacationDetailsText.setText(String.format(
                "Vacation: %s\nHotel: %s\nStart: %s\nEnd: %s",
                vacationName, hotelName, vacationStartDate, vacationEndDate
        ));
    }

    private void setupRecyclerView() {
        excursionRecyclerView = findViewById(R.id.excursionRecyclerView);
        excursionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExcursionAdapter(this);
        excursionRecyclerView.setAdapter(adapter);

        List<Excursion> excursions = new ArrayList<>();
        for (Excursion excursion : repository.getmAllExcursions()) {
            if (excursion.getVacationID() == vacationId) {
                excursions.add(excursion);
            }
        }
        adapter.setExcursions(excursions);
    }


    private void setupAddButton() {
        excursionDateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                        excursionDateInput.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void addExcursion() {
        String name = excursionNameInput.getText().toString().trim();
        String date = excursionDateInput.getText().toString().trim();

        if (name.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        try {
            Date excursionDate = sdf.parse(date);
            Date startDate = sdf.parse(vacationStartDate);
            Date endDate = sdf.parse(vacationEndDate);

            if (excursionDate.before(startDate) || excursionDate.after(endDate)) {
                Toast.makeText(this, "Excursion date must be within vacation dates", Toast.LENGTH_SHORT).show();
                return;
            }

            Excursion newExcursion = new Excursion(0, name, vacationId, date);
            repository.insert(newExcursion);

            Toast.makeText(this, "Excursion added successfully", Toast.LENGTH_SHORT).show();

            setResult(RESULT_OK);
            finish();
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }


    private void refreshRecyclerView() {
        List<Excursion> excursions = new ArrayList<>();
        for (Excursion excursion : repository.getmAllExcursions()) {
            if (excursion.getVacationID() == vacationId) {
                excursions.add(excursion);
            }
        }
        adapter.setExcursions(excursions);
    }

    private void resetToAddMode() {

        excursionNameInput.setText("");
        excursionDateInput.setText("");

        Button cancelEditButton = findViewById(R.id.cancelEditButton);
        Button addButton = findViewById(R.id.addExcursionButton);
        Button editButton = findViewById(R.id.editExcursionButton);

        cancelEditButton.setVisibility(View.GONE);
        addButton.setText("Add Excursion");
        addButton.setOnClickListener(view -> addExcursion());
        editButton.setVisibility(View.VISIBLE);
    }

    private void updateExcursion(Excursion excursion) {
        String newName = excursionNameInput.getText().toString().trim();
        String newDate = excursionDateInput.getText().toString().trim();

        if (newName.isEmpty() || newDate.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        try {
            Date date = sdf.parse(newDate);
            Date startDate = sdf.parse(vacationStartDate);
            Date endDate = sdf.parse(vacationEndDate);

            if (date.before(startDate) || date.after(endDate)) {
                Toast.makeText(this, "Excursion date must be within vacation dates", Toast.LENGTH_SHORT).show();
                return;
            }

            excursion.setExcursionTitle(newName);
            excursion.setExcursionDate(newDate);
            repository.update(excursion);

            Toast.makeText(this, "Excursion updated successfully", Toast.LENGTH_SHORT).show();
            refreshRecyclerView();

            resetToAddMode();
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }
    private void setupCancelEditButton() {
        Button cancelEditButton = findViewById(R.id.cancelEditButton);
        Button editButton = findViewById(R.id.editExcursionButton);
        Button addButton = findViewById(R.id.addExcursionButton);

        cancelEditButton.setOnClickListener(v -> {

            excursionNameInput.setText("");
            excursionDateInput.setText("");

            cancelEditButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            addButton.setText("Add Excursion");
            addButton.setOnClickListener(view -> addExcursion());
        });
    }

    private void setupDeleteButton() {
        Button deleteButton = findViewById(R.id.deleteExcursionButton);
        deleteButton.setOnClickListener(v -> deleteSelectedExcursion());
    }

    private void deleteSelectedExcursion() {
        Excursion selectedExcursion = adapter.getSelectedExcursion();
        if (selectedExcursion != null) {
            repository.delete(selectedExcursion);
            Toast.makeText(this, "Excursion deleted successfully", Toast.LENGTH_SHORT).show();

            refreshRecyclerView();

            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "No excursion selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSetAlertButton() {
        Button setAlertButton = findViewById(R.id.setAlertButton);
        setAlertButton.setOnClickListener(v -> {
            Excursion selectedExcursion = adapter.getSelectedExcursion();
            if (selectedExcursion != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                try {
                    Date excursionDate = sdf.parse(selectedExcursion.getExcursionDate());
                    if (excursionDate != null) {
                        scheduleAlert(excursionDate.getTime(), "Reminder: Excursion '" + selectedExcursion.getExcursionTitle() + "' is today!");
                    }
                } catch (ParseException e) {
                    Toast.makeText(this, "Invalid date format for excursion", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No excursion selected", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupEditButton() {
        Button editButton = findViewById(R.id.editExcursionButton);
        Button cancelEditButton = findViewById(R.id.cancelEditButton);
        Button addButton = findViewById(R.id.addExcursionButton);

        editButton.setOnClickListener(v -> {
            Excursion selectedExcursion = adapter.getSelectedExcursion();
            if (selectedExcursion != null) {
                excursionNameInput.setText(selectedExcursion.getExcursionTitle());
                excursionDateInput.setText(selectedExcursion.getExcursionDate());

                // Hide the Edit button and show Cancel/Edit buttons
                editButton.setVisibility(View.GONE);
                cancelEditButton.setVisibility(View.VISIBLE);
                addButton.setText("Update Excursion");

                addButton.setOnClickListener(view -> updateExcursion(selectedExcursion));
            } else {
                Toast.makeText(this, "No excursion selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scheduleAlert(long timeInMillis, String message) {
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("alertMessage", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            Toast.makeText(this, "Alert set for the excursion", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to set alert", Toast.LENGTH_SHORT).show();
        }
    }



}
