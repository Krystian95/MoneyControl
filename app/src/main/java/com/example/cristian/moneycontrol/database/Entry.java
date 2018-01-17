package com.example.cristian.moneycontrol.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Category.class,
        parentColumns = "idCategory",
        childColumns = "idCategory"))
public class Entry {

    @PrimaryKey(autoGenerate = true)
    private int idEntry;

    private int idCategory;

    private float amount;

    private String description;

    private String address;

    private long dateTime;

    private String recurrenceRule;

    public void setIdEntry(int idEntry) {
        this.idEntry = idEntry;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public int getIdEntry() {
        return this.idEntry;
    }

    public int getIdCategory() {
        return this.idCategory;
    }

    public float getAmount() {
        return this.amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(long date_time) {
        this.dateTime = date_time;
    }

    public String getRecurrenceRule() {
        return this.recurrenceRule;
    }

    public void setRecurrenceRule(String recurrence_rule) {
        this.recurrenceRule = recurrence_rule;
    }

    public String toString() {

        return "Category{" +
                "\n\t\t\t idEntry: " + idEntry +
                "\n\t\t\t idCategory: " + idCategory +
                "\n\t\t\t amount: " + amount +
                "\n\t\t\t description: " + description +
                "\n\t\t\t address: " + address +
                "\n\t\t\t dateTime: " + dateTime +
                "\n\t\t\t recurrenceRule: " + recurrenceRule +
                "\n}";
    }
}
