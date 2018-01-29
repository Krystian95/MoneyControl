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
    public void update(Entry entry);

    @Query("SELECT * FROM entry WHERE recurrenceRule<>'null' ORDER BY dateTime")
    public Entry[] getAllWithRrule();

    @Query("DELETE FROM entry")
    public void deleteAll();

    @Query("DELETE FROM entry WHERE idEntry=:entry_id")
    public void deleteEntryById(String entry_id);

    @Query("SELECT * FROM entry WHERE idEntry=:entry_id")
    public Entry getEntryById(String entry_id);

    @Query("SELECT * FROM entry WHERE dateTime >= Datetime(:from) AND dateTime <= Datetime(:to) AND recurrenceRule='null' ORDER BY dateTime")
    public Entry[] getEntriesByDateRange(String from, String to);

    @Delete
    void delete(Entry entry);

    @Query("SELECT * FROM entry ORDER BY dateTime")
    public Entry[] getAll();

}
