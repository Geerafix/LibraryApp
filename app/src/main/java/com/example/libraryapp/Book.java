package com.example.libraryapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "book")
public class Book {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private final String title;

    private final String author;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public int getId() { return this.id; }

    public void setId(int id) { this.id = id; }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

}
