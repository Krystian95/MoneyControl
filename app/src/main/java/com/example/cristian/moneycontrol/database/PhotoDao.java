package com.example.cristian.moneycontrol.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(Photo photo);

    @Query("SELECT * FROM photo WHERE idEntry=:id_entry")
    public Photo[] getPhotosByEntryId(String id_entry);

    @Query("DELETE FROM photo WHERE absolute_path=:absolute_path")
    public void deletePhotoByAbsolutePath(String absolute_path);

    @Query("SELECT * FROM photo WHERE idEntry='null'")
    public Photo[] getAllUnlinked();

    @Query("DELETE FROM photo WHERE idEntry='null'")
    public void deleteAllUnlinked();

    @Query("UPDATE photo SET idEntry=:id_entry WHERE absolute_path=:absolute_path")
    public void updateIdEntryByAbsolutePath(String absolute_path, String id_entry);

    @Delete
    void delete(Photo photo);
}
