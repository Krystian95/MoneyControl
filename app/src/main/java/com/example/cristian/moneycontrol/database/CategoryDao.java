package com.example.cristian.moneycontrol.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Category category);

    @Query("SELECT * FROM category")
    public Category[] getAll();

    @Query("DELETE FROM category")
    public void deleteAll();

}
