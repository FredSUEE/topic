package com.yahoo.topics.datasouce;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ListDataSource<T> extends BaseDataSource<T> {

    private final List<T> mData = new ArrayList<>();

    public ListDataSource() {
    }

    public ListDataSource(@NonNull Collection<T> data) {
        mData.addAll(data);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(@IntRange(from = 0) int index) {
        return mData.get(index);
    }

    @Override
    public void addItems(@Size(min = 1) List<T> items) {
        addItems(mData.size(), items);
    }

    @Override
    public void addItems(@IntRange(from = 0) int index, @Size(min = 1) List<T> items) {
        mData.addAll(index, items);
        notifyItemsAdded(index, items);
    }

    @Override
    public T removeItem(@IntRange(from = 0) int index) {
        T remove = mData.remove(index);
        notifyItemsRemoved(index, Collections.singletonList(remove));
        return remove;
    }

    @Override
    public List<T> removeItems(@IntRange(from = 0) int index, @IntRange(from = 1) int to) {
        List<T> items = new ArrayList<>(to - index);
        for (int i = index; i < to; i++) {
            T remove = mData.remove(index);
            items.add(remove);
        }

        notifyItemsRemoved(index, items);
        return items;
    }

    @Override
    public void clear() {
        if (mData.size() > 0) {
            List<T> data = new ArrayList<>(mData);
            mData.clear();
            notifyItemsRemoved(0, data);
        }
    }

    @Override
    public boolean hasMore() {
        return false;
    }

    @Override
    public void loadNextPage() {
    }

    @Override
    public void fetchNewData() {
    }
}
