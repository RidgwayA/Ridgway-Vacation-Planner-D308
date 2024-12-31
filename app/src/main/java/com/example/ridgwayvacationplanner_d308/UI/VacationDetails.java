package com.example.ridgwayvacationplanner_d308.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.example.ridgwayvacationplanner_d308.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationDetails extends AppCompatActivity {

    private int vacationId;
    private Repository repository;
    private Vacation currentVacation;

    private EditText vacationNameEdit;
    private EditText hotelNameEdit;
    private TextView startDateView;
    private TextView endDateView;

    private final Calendar myCalendarStart = Calendar.getInstance();
    private final Calendar myCalendarEnd = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacation_details);
        repository = new Repository(getApplication());

        initViews();
        extractIntentData();
        setupRecyclerView();
        setupActionButtons();
        setupDatePickers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.alert) {
            setAlert();
            return true;
        } else if (id == R.id.share) {
            shareVacationDetails();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        vacationNameEdit = findViewById(R.id.vacationNameEdit);
        hotelNameEdit = findViewById(R.id.hotelNameEdit);
        startDateView = findViewById(R.id.startDateView);
        endDateView = findViewById(R.id.endDateView);
    }

    // Extracts data from the intent and populates the UI with vacation details
    private void extractIntentData() {
        vacationId = getIntent().getIntExtra("vacationId", -1);
        String vacationName = getIntent().getStringExtra("vacationName");
        String hotelName = getIntent().getStringExtra("hotelName");
        String setStartDate = getIntent().getStringExtra("startdate");
        String setEndDate = getIntent().getStringExtra("enddate");

        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        vacationNameEdit.setText(vacationName);
        hotelNameEdit.setText(hotelName);

        try {
            startDateView.setText(setStartDate != null ? inputFormat.format(inputFormat.parse(setStartDate)) : "No Start Date");
            endDateView.setText(setEndDate != null ? inputFormat.format(inputFormat.parse(setEndDate)) : "No End Date");
        } catch (ParseException e) {
            startDateView.setText("No Start Date");
            endDateView.setText("No End Date");
        }

        if (vacationId != -1) {
            for (Vacation vacation : repository.getmAllVacations()) {
                if (vacation.getVacationID() == vacationId) {
                    currentVacation = vacation;
                    vacationNameEdit.setText(currentVacation.getVacationTitle());
                    hotelNameEdit.setText(currentVacation.getVacationLodging());
                    try {
                        startDateView.setText(inputFormat.format(inputFormat.parse(currentVacation.getStartDate())));
                        endDateView.setText(inputFormat.format(inputFormat.parse(currentVacation.getEndDate())));
                    } catch (ParseException e) {
                        startDateView.setText("No Start Date");
                        endDateView.setText("No End Date");
                    }
                    break;
                }
            }
        }
    }

    // Sets up the RecyclerView to display excursions related to the current vacation
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.vacationDetailsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);

        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion excursion : repository.getmAllExcursions()) {
            if (excursion.getVacationID() == vacationId) {
                filteredExcursions.add(excursion);
            }
        }
        excursionAdapter.setExcursions(filteredExcursions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            setupRecyclerView();
        }
    }

    // Sets up button actions for saving, deleting, and navigating to excursion details
    private void setupActionButtons() {
        Button saveButton = findViewById(R.id.saveButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button excursionButton = findViewById(R.id.excursionButton);

        saveButton.setOnClickListener(v -> saveVacation());
        deleteButton.setOnClickListener(v -> deleteVacation());
        excursionButton.setOnClickListener(v -> navigateToExcursionDetails());
    }

    private void navigateToExcursionDetails() {
        if (currentVacation == null) {
            Toast.makeText(this, "No vacation selected", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
        intent.putExtra("vacationId", vacationId);
        intent.putExtra("vacationName", currentVacation.getVacationTitle());
        intent.putExtra("startdate", currentVacation.getStartDate());
        intent.putExtra("enddate", currentVacation.getEndDate());
        intent.putExtra("hotelName", currentVacation.getVacationLodging());
        startActivityForResult(intent, 1);
    }

    // Sets up date pickers for start and end dates
    private void setupDatePickers() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        startDateView.setOnClickListener(view -> new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
            myCalendarStart.set(year, month, dayOfMonth);
            startDateView.setText(sdf.format(myCalendarStart.getTime()));
        }, myCalendarStart.get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH), myCalendarStart.get(Calendar.DAY_OF_MONTH)).show());

        endDateView.setOnClickListener(view -> new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
            myCalendarEnd.set(year, month, dayOfMonth);
            endDateView.setText(sdf.format(myCalendarEnd.getTime()));
        }, myCalendarEnd.get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH), myCalendarEnd.get(Calendar.DAY_OF_MONTH)).show());
    }

    // Saves the current vacation details to the database
    private void saveVacation() {
        String vacationName = vacationNameEdit.getText().toString().trim();
        String hotelName = hotelNameEdit.getText().toString().trim();
        String startDate = startDateView.getText().toString().trim();
        String endDate = endDateView.getText().toString().trim();

        if (vacationName.isEmpty() || hotelName.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            if (end.before(start)) {
                Toast.makeText(this, "End date must be after start date", Toast.LENGTH_LONG).show();
                return;
            }

            if (currentVacation != null) {
                currentVacation.setVacationTitle(vacationName);
                currentVacation.setVacationLodging(hotelName);
                currentVacation.setStartDate(startDate);
                currentVacation.setEndDate(endDate);
                repository.update(currentVacation);
                Toast.makeText(this, "Vacation updated successfully", Toast.LENGTH_LONG).show();
            } else {
                int newVacationId = repository.getmAllVacations().isEmpty() ? 1 : repository.getmAllVacations().get(repository.getmAllVacations().size() - 1).getVacationID() + 1;
                Vacation newVacation = new Vacation(newVacationId, vacationName, hotelName, startDate, endDate);
                repository.insert(newVacation);
                Toast.makeText(this, "New vacation saved", Toast.LENGTH_LONG).show();
            }

            finish();
        } catch (ParseException e) {
            Toast.makeText(this, "Error parsing dates", Toast.LENGTH_SHORT).show();
        }
    }

    // Deletes the current vacation, ensuring no associated excursions exist
    private void deleteVacation() {
        if (currentVacation == null) {
            Toast.makeText(this, "No vacation selected", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Excursion> associatedExcursions = new ArrayList<>();
        for (Excursion excursion : repository.getmAllExcursions()) {
            if (excursion.getVacationID() == vacationId) {
                associatedExcursions.add(excursion);
            }
        }

        if (!associatedExcursions.isEmpty()) {
            Toast.makeText(this, "Cannot delete vacation with associated excursions. Please delete the excursions first.", Toast.LENGTH_LONG).show();
            return;
        }

        repository.delete(currentVacation);
        Toast.makeText(this, "Vacation deleted successfully", Toast.LENGTH_SHORT).show();
        finish();
    }


    // Schedules an alert for the vacation start or end date
    private void setAlert() {
        String startDateText = startDateView.getText().toString().trim();
        String endDateText = endDateView.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        try {
            Date startDate = sdf.parse(startDateText);
            Date endDate = sdf.parse(endDateText);

            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
            currentCalendar.set(Calendar.MINUTE, 0);
            currentCalendar.set(Calendar.SECOND, 0);
            currentCalendar.set(Calendar.MILLISECOND, 0);
            Date currentDate = currentCalendar.getTime();

            if (!startDate.before(currentDate)) {
                scheduleAlert(startDate.getTime(), "Vacation starts today!");
            }

            if (!endDate.before(currentDate)) {
                scheduleAlert(endDate.getTime(), "Vacation ends today!");
            }

            Toast.makeText(this, "Alerts set for valid vacation dates!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (ParseException e) {
            Toast.makeText(this, "Error parsing vacation dates", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to schedule alerts
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
        }
    }

    // Shares the vacation details through available sharing options
    private void shareVacationDetails() {
        if (currentVacation == null) {
            Toast.makeText(this, "No vacation details to share", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder shareText = new StringBuilder();
        shareText.append(String.format(
                "Vacation Details:\n\nName: %s\nHotel: %s\nStart Date: %s\nEnd Date: %s\n\n",
                currentVacation.getVacationTitle(),
                currentVacation.getVacationLodging(),
                currentVacation.getStartDate(),
                currentVacation.getEndDate()
        ));

        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion excursion : repository.getmAllExcursions()) {
            if (excursion.getVacationID() == vacationId) {
                filteredExcursions.add(excursion);
            }
        }

        if (!filteredExcursions.isEmpty()) {
            shareText.append("Excursions:\n");
            for (Excursion excursion : filteredExcursions) {
                shareText.append(String.format("- %s (%s)\n", excursion.getExcursionTitle(), excursion.getExcursionDate()));
            }
        } else {
            shareText.append("Excursions:\nNone\n");
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        startActivity(Intent.createChooser(shareIntent, "Share vacation details via"));
    }
}
