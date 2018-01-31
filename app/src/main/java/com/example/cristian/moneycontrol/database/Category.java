package com.example.cristian.moneycontrol.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Category {

    @PrimaryKey(autoGenerate = true)
    private int idCategory;

    private String name;

    private int icon;

    private String type;

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return "Category{" +
                "\n\t\t\t idCategory: " + idCategory +
                "\n\t\t\t name: " + name +
                "\n\t\t\t icon: " + icon +
                "\n\t\t\t type: " + type +
                "\n}";
    }
}
