package com.example.cristian.moneycontrol.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import com.example.cristian.moneycontrol.CustomCalendar;

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

    private String dateTime;

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

    public String getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(String date_time) {
        this.dateTime = date_time;
    }

    public String getRecurrenceRule() {
        return this.recurrenceRule;
    }

    public void setRecurrenceRule(String recurrence_rule) {
        this.recurrenceRule = recurrence_rule;
    }

    public String getDate() {
        String dateTime = this.getDateTime();
        CustomCalendar calendar = new CustomCalendar();
        String[] splitted = dateTime.split(" ");
        return calendar.convertToLocalFormat(splitted[0]);

    }

    public String getTime() {
        String dateTime = this.getDateTime();
        String[] splitted = dateTime.split(" ");
        return splitted[1].substring(0, 5);
    }

    public String toString() {

        return "Entry{" +
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
