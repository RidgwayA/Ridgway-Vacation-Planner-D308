package com.example.ridgwayvacationplanner_d308.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ridgwayvacationplanner_d308.dao.ExcursionDAO;
import com.example.ridgwayvacationplanner_d308.dao.VacationDAO;
import com.example.ridgwayvacationplanner_d308.entities.Excursion;
import com.example.ridgwayvacationplanner_d308.entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class}, version = 1, exportSchema = false)
//If you increment the version number you will clear your database.
public abstract class VacationDatabaseBuilder extends RoomDatabase {
    public abstract VacationDAO vacationDAO();
    public abstract ExcursionDAO excursionDAO();
    private static volatile VacationDatabaseBuilder INSTANCE;

    static VacationDatabaseBuilder getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (VacationDatabaseBuilder.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), VacationDatabaseBuilder.class, "MyVacationDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
