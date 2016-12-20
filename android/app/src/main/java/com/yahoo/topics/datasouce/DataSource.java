package com.yahoo.topics.datasouce;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.List;

import rx.functions.Func1;

public interface DataSource<T> {

    interface Listener<T> {
        void onItemsAdded(@NonNull DataSource<T> dataSource, @IntRange(from = 0) int index,
                @Size(min = 1) List<T> data);

        void onItemsRemoved(@NonNull DataSource<T> dataSource, @IntRange(from = 0) int index,
                @Size(min = 1) List<T> data);

        void onItemsUpdated(@NonNull DataSource<T> dataSource, @IntRange(from = 0) int from,
                @IntRange(from = 1) int to);

        void onError(@NonNull DataSource<T> dataSource, @NonNull Throwable e);

        void onLoading(@NonNull DataSource<T> dataSource);
    }

    @IntRange(from = 0)
    int getCount();

    T getItem(@IntRange(from = 0) int index);

    void addItems(@Size(min = 1) List<T> items);

    void addItems(@IntRange(from = 0) int index, @Size(min = 1) List<T> items);

    T removeItem(@IntRange(from = 0) int index);

    List<T> removeItems(@IntRange(from = 0) int index, @IntRange(from = 1) int to);

    void clear();

    boolean hasMore();

    void loadNextPage();

    void fetchNewData();

    void addListener(@NonNull Listener<T> listener);

    void removeListener(@NonNull Listener<T> listener);

    int find(Func1<T, Boolean> comparator);

    List<T> toList();
}
