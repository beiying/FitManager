package com.beiying.demo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by beiying on 2019/6/6.
 */

public class AIDLService extends Service {
    public final String TAG = this.getClass().getSimpleName();
    private List<Book> mBooks = new ArrayList<>();
    private RemoteCallbackList<IOnNewBook> mListeners = new RemoteCallbackList<>();

    private final IBookManager.Stub mBookeManager = new IBookManager.Stub() {
        @Override
        public List<Book> getBooks() throws RemoteException {
            synchronized (this) {
                if (mBooks != null) {
                    return mBooks;
                }
                return new ArrayList<>();
            }
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            synchronized (this) {
                if (mBooks == null) {
                    mBooks = new ArrayList<>();
                }
                if (book == null) {
                    Log.e(TAG, "Book is null in In");
                    book = new Book();
                }
                //尝试修改book的参数，主要是为了观察其到客户端的反馈
                book.setPrice(2333);
                if (!mBooks.contains(book)) {
                    mBooks.add(book);
                }
                //打印mBooks列表，观察客户端传过来的值
                Log.e(TAG, "invoking addBooks() method , now the list is : " + mBooks.toString());
            }
        }

        @Override
        public void addListener(IOnNewBook listener) throws RemoteException {
            mListeners.register(listener);
        }

        @Override
        public void removeListener(IOnNewBook listener) throws RemoteException {
            mListeners.unregister(listener);
        }
    };

    private final IBinderPool.Stub mBinderPool = new IBinderPool.Stub() {
        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BinderPool.BINDER_BOOK:
                    binder = mBookeManager;
                    break;
            }
            return binder;
        }
    };

    private void onNewBook(Book book) {
        int count = mListeners.beginBroadcast();
        for (int i = 0; i < count; i++) {
            IOnNewBook onNewPersonIn = mListeners.getBroadcastItem(i);
            try {
                if (onNewPersonIn != null) {
                    onNewPersonIn.onNewBook(book);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mListeners.finishBroadcast();
    }

    @Override
    public IBinder onBind(Intent intent) {
//        return mBookeManager.asBinder();
        return mBinderPool.asBinder();
    }
}
