package com.beiying.fitmanager.framework.recycleview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by yangyang on 2017/9/16.
 */

public abstract class BYRecycleAdapter<E, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements List<E>{

    private final Object mLock = new Object();

    private final List<E> mList;
    private boolean mHasHeader;
    private boolean mHasFooter;

    public BYRecycleAdapter() {
        mList = new ArrayList<E>();
    }

    public BYRecycleAdapter(int capacity) {
        mList = new ArrayList<E>(capacity);
    }

    public BYRecycleAdapter(List<E> collection) {
        if (collection == null) {
            mList = new ArrayList<>();
        } else {
            mList = collection;
        }
    }

    @Override
    public int getItemCount() {
        return size();
    }

    @Override
    public void add(int location, E object) {
        synchronized (mLock) {
            mList.add(location, object);
            notifyItemInserted(location + getHeaderSize());
        }
    }

    @Override
    public boolean add(E object) {
        synchronized (mLock) {
            int lastIndex = mList.size();
            if (mList.add(object)) {
                notifyItemInserted(lastIndex + getHeaderSize());
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean addAll(int location, Collection<? extends E> collection) {
        synchronized (mLock) {
            if (mList.addAll(location, collection)) {
                notifyItemRangeInserted(location + getHeaderSize(), collection.size());
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        synchronized (mLock) {
            int lastIndex = mList.size();
            if (mList.addAll(collection)) {
                notifyItemRangeInserted(lastIndex + getHeaderSize(), collection.size());
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void clear() {
        synchronized (mLock) {
            int size = mList.size();
            if (size > 0) {
                mList.clear();
                notifyItemRangeRemoved(getHeaderSize(), size);
            }
        }
    }

    @Override
    public boolean contains(Object object) {
        return mList.contains(object);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return mList.containsAll(collection);
    }

    @Override
    public E get(int location) {
        return mList.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return mList.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return mList.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return mList.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return mList.lastIndexOf(object);
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator() {
        return mList.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator(int location) {
        return mList.listIterator(location);
    }

    @Override
    public E remove(int location) {
        synchronized (mLock) {
            E item = mList.remove(location);
            notifyItemRemoved(location + getHeaderSize());
            return item;
        }
    }

    @Override
    public boolean remove(Object object) {
        synchronized (mLock) {
            int index = indexOf(object);
            if (mList.remove(object)) {
                notifyItemRemoved(index + getHeaderSize());
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean updatePosition(E object, int position) {
        synchronized (mLock) {
            int index = indexOf(object);
            if (index != -1 && index != position) {
                mList.remove(object);
                mList.add(position, object);
                notifyItemMoved(index + getHeaderSize(), position + getHeaderSize());
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        boolean modified = false;

        Iterator<E> iterator = mList.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (collection.contains(object)) {
                synchronized (mLock) {
                    int index = indexOf(object);
                    iterator.remove();
                    notifyItemRemoved(index + getHeaderSize());
                }

                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        boolean modified = false;

        Iterator<E> iterator = mList.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (!collection.contains(object)) {
                synchronized (mLock) {
                    int index = indexOf(object);
                    iterator.remove();
                    notifyItemRemoved(index + getHeaderSize());
                }

                modified = true;
            }
        }

        return modified;
    }

    @Override
    public E set(int location, E object) {
        synchronized (mLock) {
            E origin = mList.set(location, object);
            notifyItemChanged(location + getHeaderSize());
            return origin;
        }
    }

    @Override
    public int size() {
        return mList.size();
    }

    @NonNull
    @Override
    public List<E> subList(int start, int end) {
        return mList.subList(start, end);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return mList.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] array) {
        return mList.toArray(array);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof List && mList.equals(o);
    }

    public void replaceWith(List<E> data) {
        synchronized (mLock) {
            if (mList.isEmpty() && data.isEmpty()) {
                return;
            }

            if (mList.isEmpty()) {
                addAll(data);
                return;
            }

            if (data.isEmpty()) {
                clear();
                return;
            }

            // 首先将旧列表有、新列表没有的从旧列表去除
            retainAll(data);

            // 如果列表被完全清空了，那就直接全部插入好了
            if (mList.isEmpty()) {
                addAll(data);
                return;
            }

            // 然后遍历新列表，对旧列表的数据更新、移动、增加
            for (int indexNew = 0; indexNew < data.size(); indexNew++) {
                E item = data.get(indexNew);

                int indexOld = indexOf(item);

                if (indexOld == -1) {
                    add(indexNew, item);
                } else if (indexOld == indexNew) {
                    set(indexNew, item);
                } else {
                    mList.remove(indexOld);
                    if (mList.size() - 1 < indexNew) {
                        add(item);
                    } else {
                        mList.add(indexNew, item);
                        notifyItemMoved(indexOld + getHeaderSize(), indexNew + getHeaderSize());
                    }
                }
            }
        }
    }

    public int getHeaderSize() {
        return mHasHeader ? 1 : 0;
    }

    public int getFooterSize() {
        return mHasFooter ? 1 : 0;
    }

    public void setHasHeader(boolean hasHeader) {
        mHasHeader = hasHeader;
    }

    public void setHasFooter(boolean hasFooter) {
        mHasFooter = hasFooter;
    }

    public interface AddDataListener<T>{
        void addDataCallback(T data);
    }
}
