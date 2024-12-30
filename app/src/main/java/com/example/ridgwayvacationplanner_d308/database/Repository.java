package com.example.ridgwayvacationplanner_d308.database;

import android.app.Application;
import android.util.Log;

import com.example.ridgwayvacationplanner_d308.dao.ExcursionDAO;
import com.example.ridgwayvacationplanner_d308.dao.VacationDAO;
import com.example.ridgwayvacationplanner_d308.entities.Excursion;
import com.example.ridgwayvacationplanner_d308.entities.Vacation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private ExcursionDAO mExcursionDAO;
    private VacationDAO mVacationDAO;

    private List<Excursion> mAllExcursions;
    private List<Vacation> mAllVacations;

    private static int NUM_OF_THREADS=4;
    static final ExecutorService databaseExecutor= Executors.newFixedThreadPool(NUM_OF_THREADS);

//Constructor creation
    public Repository(Application application){
        VacationDatabaseBuilder db = VacationDatabaseBuilder.getDatabase(application);
        mExcursionDAO = db.excursionDAO();
        mVacationDAO = db.vacationDAO();
    }
// retrieves or creates vacations from the db with logging for debugging
public List<Vacation> getmAllVacations() {
    databaseExecutor.execute(() -> {
        mAllVacations = mVacationDAO.getAllVacations();
        Log.d("Repository", "Vacations retrieved: " + mAllVacations.size());
        for (Vacation vacation : mAllVacations) {
            Log.d("Repository", "Vacation - ID: " + vacation.getVacationID() + ", Title: " + vacation.getVacationTitle());
        }
    });
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return mAllVacations;
}
// Method for vacation creation
    public void insert (Vacation vacation) {
        databaseExecutor.execute(() -> {
            mVacationDAO.insert(vacation);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
// Method for Vacation updates
    public void update(Vacation vacation) {
        databaseExecutor.execute(() -> {
            mVacationDAO.update(vacation);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
// Method for Vacation deletes
    public void delete(Vacation vacation) {
        databaseExecutor.execute(() -> {
            mVacationDAO.delete(vacation);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

// Creates or retrieves excursions for the database
    public List<Excursion> getmAllExcursions() {
        databaseExecutor.execute(()-> {
            mAllExcursions = mExcursionDAO.getAllExcursions();
        });
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return mAllExcursions;
    }

    public List<Excursion> getAssociatedExcursion(int vacationID) {
        databaseExecutor.execute(() -> {
            mAllExcursions = mExcursionDAO.getAssociatedExcursions(vacationID);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return mAllExcursions;
    }

    public void insert(Excursion excursion) {
        databaseExecutor.execute(() -> {
            mExcursionDAO.insert(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> {
            mExcursionDAO.update(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void delete (Excursion excursion) {
        databaseExecutor.execute(() -> {
            mExcursionDAO.delete(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
