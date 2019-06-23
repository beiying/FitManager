// IOnNewBook.aidl
package com.beiying.demo;

// Declare any non-default types here with import statements
import com.beiying.demo.Book;

interface IOnNewBook {
    void onNewBook(in Book newBook);
}
