package com.example.libraryapp;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

public class BookRepository {
    private final BookDao bookDao;
    private final LiveData<List<Book>> books;

    public BookRepository(Application application) {
        BookDatabase database = BookDatabase.getDatabaseInstance(application);
        this.bookDao = database.bookDao();
        books = bookDao.findAll();
    }

    LiveData<List<Book>> findAllBooks() { return books; }

    public void insert(Book book) {
        BookDatabase.databaseWriteExecutor.execute(() -> bookDao.insert(book));
    }

    public void update(Book book) {
        BookDatabase.databaseWriteExecutor.execute(() -> bookDao.update(book));
    }

    public void delete(Book book) {
        BookDatabase.databaseWriteExecutor.execute(() -> bookDao.delete(book));
    }
}
