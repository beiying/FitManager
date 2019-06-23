// IBookManager.aidl
package com.beiying.demo;

// Declare any non-default types here with import statements
import com.beiying.demo.Book;
import com.beiying.demo.IOnNewBook;

interface IBookManager {
    List<Book> getBooks();


    void addBook(in Book book);

    void addListener(in IOnNewBook listener);

    void removeListener(in IOnNewBook listener);
}
