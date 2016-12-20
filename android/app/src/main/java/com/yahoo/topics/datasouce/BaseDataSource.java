package com.yahoo.topics.datasouce;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import rx.functions.Func1;

public abstract class BaseDataSource<T> implements DataSource<T> {
    private final WeakHashMap<Listener<T>, Void> mListeners = new WeakHashMap<>();

    @Override
    public void addListener(@NonNull Listener<T> listener) {
        mListeners.put(listener, null);
    }

    @Override
    public void removeListener(@NonNull Listener<T> listener) {
        mListeners.remove(listener);
    }

    protected void notifyItemsAdded(@IntRange(from = 0) int index, @Size(min = 1) List<T> items) {
        for (Listener<T> listener : mListeners.keySet()) {
            if (listener != null) {
                listener.onItemsAdded(this, index, items);
            }
        }
    }

    protected void notifyItemsRemoved(@IntRange(from = 0) int index, @Size(min = 1) List<T> items) {
        for (Listener<T> listener : mListeners.keySet()) {
            if (listener != null) {
                listener.onItemsRemoved(this, index, items);
            }
        }
    }

    protected void notifyItemsChanged(@IntRange(from = 0) int from, @IntRange(to = 1) int to) {
        for (Listener<T> listener : mListeners.keySet()) {
            if (listener != null) {
                listener.onItemsUpdated(this, from, to);
            }
        }
    }


    protected void notifyLoading() {
        for (Listener<T> listener : mListeners.keySet()) {
            if (listener != null) {
                listener.onLoading(this);
            }
        }
    }

    protected void notifyError(@NonNull Throwable e) {
        for (Listener<T> listener : mListeners.keySet()) {
            if (listener != null) {
                listener.onError(this, e);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (T token : toList()) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(", ");
            }
            sb.append(token);
        }
        return sb.toString();
    }

    @Override
    public List<T> toList() {
        List<T> data = new ArrayList<>(getCount());
        for (int i = 0; i < getCount(); i++) {
            data.add(getItem(i));
        }
        return data;
    }

    @Override
    public int find(Func1<T, Boolean> comparator) {
        int count = getCount();
        for (int i = 0; i < count; i++) {
            if (comparator.call(getItem(i))) {
                return i;
            }
        }
        return -1;
    }

}
