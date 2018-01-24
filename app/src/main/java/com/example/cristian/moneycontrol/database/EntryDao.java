package com.example.cristian.moneycontrol.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

@Dao
public interface EntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public long insert(Entry entry);

    @Update
    public abstract void update(Entry entry);

    @Query("DELETE FROM entry")
    public void deleteAll();

    @Query("SELECT * FROM entry WHERE dateTime >= Datetime(:from) AND dateTime <= Datetime(:to)")
    public Entry[] getEntriesByDateRange(String from, String to);

    @Delete
    void delete(Entry entry);

    /*@Query("SELECT * FROM entry "
            + "INNER JOIN category ON category.idCategory = entry.idCategory "
            + "WHERE dateTime = :date_time")
    public Entry[] loadEntriesByDate(long date_time);*/

    @Query("SELECT * FROM entry")
    public Entry[] getAll();

}
