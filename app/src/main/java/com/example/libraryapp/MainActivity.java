package com.example.libraryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BookViewModel bookViewModel;
    private final String TAG = "Main activity";
    public static final int NEW_BOOK_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_BOOK_ACTIVITY_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BookAdapter adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
        bookViewModel.findAllBooks().observe(this, adapter::setBooks);

        FloatingActionButton addBookButton = findViewById(R.id.add_button);
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditBookActivity.class);
                startActivityForResult (intent, NEW_BOOK_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Book book = new Book(data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE),
                    data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR));

            if (requestCode == NEW_BOOK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
                bookViewModel.insert(book);
                Snackbar.make(findViewById(R.id.coordinator_layout),
                                getString(R.string.book_added),
                                Snackbar.LENGTH_LONG)
                                .show();
            } else if (requestCode == EDIT_BOOK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
                book.setId(data.getIntExtra(EditBookActivity.EXTRA_EDIT_BOOK_ID, -1));
                bookViewModel.update(book);
                Snackbar.make(findViewById(R.id.coordinator_layout),
                                getString(R.string.book_edited, book.getTitle()),
                                Snackbar.LENGTH_LONG)
                                .show();
            } else {
                Snackbar.make(findViewById(R.id.coordinator_layout),
                                getString(R.string.empty_not_saved),
                                Snackbar.LENGTH_LONG)
                                .show();
            }
        }
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener {

        private TextView bookTitleTextView;
        private TextView bookAuthorTextView;
        private Book currentBook;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.book_list_item, parent, false));

            bookTitleTextView = itemView.findViewById(R.id.book_name);
            bookAuthorTextView = itemView.findViewById(R.id.author_name);

            itemView.setOnTouchListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Book book) {
            currentBook = book;
            bookTitleTextView.setText(book.getTitle());
            bookAuthorTextView.setText(book.getAuthor());
        }

        @Override
        public boolean onLongClick(View view) {
            bookViewModel.delete(currentBook);
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    getString(R.string.deleted_book, currentBook.getTitle()),
                    Snackbar.LENGTH_LONG)
                    .show();
            return true;
        }

        @Override
        public void onClick(View view) {
            Intent editIntent = new Intent(MainActivity.this, EditBookActivity.class);
            editIntent.putExtra(EditBookActivity.EXTRA_EDIT_BOOK_ID, currentBook.getId());
            editIntent.putExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE, currentBook.getTitle());
            editIntent.putExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR, currentBook.getAuthor());
            startActivityForResult(editIntent, EDIT_BOOK_ACTIVITY_REQUEST_CODE);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    Snackbar.make(findViewById(R.id.coordinator_layout),
                                    getString(R.string.archived_book, currentBook.getTitle()),
                                    Snackbar.LENGTH_LONG)
                                    .show();
                    return true;
                default:
                    return false;
            }
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {

        private List<Book> books;

        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BookHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookHolder holder, int position) {
            if (books != null) {
                Book book = books.get(position);
                holder.bind(book);
            } else {
                Log.d(TAG, "No books");
            }
        }

        @Override
        public int getItemCount() {
            if (books != null) {
                return books.size();
            } else {
                return 0;
            }
        }

        void setBooks(List<Book> books) {
            this.books = books;
            notifyDataSetChanged();
        }
    }
}