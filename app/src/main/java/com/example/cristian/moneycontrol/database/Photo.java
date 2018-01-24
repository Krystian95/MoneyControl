package com.example.cristian.moneycontrol.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Photo {

    @PrimaryKey(autoGenerate = true)
    private int idPhoto;

    private String absolute_path;

    private String idEntry;

    public String toString() {
        return "Photo{" +
                "\n\t\t\t idPhoto: " + idPhoto +
                "\n\t\t\t absolute_path: " + absolute_path +
                "\n\t\t\t idEntry: " + idEntry +
                "\n}";
    }

    public int getIdPhoto() {
        return this.idPhoto;
    }

    public String getAbsolute_path() {
        return this.absolute_path;
    }

    public String getIdEntry() {
        return this.idEntry;
    }

    public void setIdEntry(String idEntry) {
        this.idEntry = idEntry;
    }

    public void setAbsolute_path(String absolute_path) {
        this.absolute_path = absolute_path;
    }

    public void setIdPhoto(int idPhoto) {
        this.idPhoto = idPhoto;
    }
}
