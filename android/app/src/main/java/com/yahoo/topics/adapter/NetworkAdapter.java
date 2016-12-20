package com.yahoo.topics.adapter;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.view.ViewGroup;

import com.yahoo.topics.datasouce.DataSource;

import java.util.List;
import rx.Observable;
import rx.functions.Func1;

public abstract class NetworkAdapter<V, VH extends RecyclerViewAdapter.ViewHolder>
        extends RecyclerViewAdapter<VH> implements DataSource.Listener<V> {

    private static final int OBJECT_PREFETCH_NUMBER = 3;

    protected final DataSource<V> mDataSource;

    protected Context mContext;

    protected OnLoadListener<V> mLoadListener;

    public void setLoadListener(OnLoadListener<V> listener) {
        mLoadListener = listener;
    }

    public interface OnLoadListener<V> {
        void onLoading();

        void onLoaded(@IntRange(from = 0) int index, @NonNull List<V> objects, boolean hasMore);

        void onError(@NonNull Throwable e);

        void onNewDataFetched(@NonNull List<V> data);
    }

    public NetworkAdapter(
            @NonNull Context context,
            @NonNull DataSource<V> dataSource
    ) {

        mContext = context;

        mDataSource = dataSource;
        mDataSource.addListener(this);
        mDataSource.loadNextPage();
    }

    protected Context getContext() {
        return mContext;
    }

    public void reload() {
        mDataSource.clear();
        mDataSource.loadNextPage();
    }

    @Override
    public int getContentItemCount() {
        return mDataSource.getCount();
    }

    @Override
    @CallSuper
    protected void onBindContentItemViewHolder(VH contentViewHolder, int position) {
        if (mDataSource.getCount() > 0 && mDataSource.hasMore() &&
                mDataSource.getCount() - 1 < position + OBJECT_PREFETCH_NUMBER) {
            mDataSource.loadNextPage();
        }
    }

    public V getItem(int position) {
        return mDataSource.getItem(position);
    }

    @Override
    protected abstract VH onCreateContentItemViewHolder(ViewGroup parent, int contentViewType);

    protected Func1<List<V>, Observable<List<V>>> getPostProcessing() {
        return new Func1<List<V>, Observable<List<V>>>() {
            @Override
            public Observable<List<V>> call(List<V> ts) {
                return Observable.just(ts);
            }
        };
    }

    @Override
    @CallSuper
    public void onLoading(@NonNull DataSource<V> dataSource) {
        if (mLoadListener != null) {
            mLoadListener.onLoading();
        }
    }

    @Override
    public void onItemsAdded(@NonNull DataSource<V> dataSource, @IntRange(from = 0) final int index,
            @Size(min = 1) final List<V> data) {
        // Call notifyDataSourceChanged on the first insert
        if (index == 0 && data.size() == mDataSource.getCount()) {
            notifyDataSetChanged();
        } else {
            notifyContentItemRangeInserted(index, data.size());
        }
        if (mLoadListener != null) {
            mLoadListener.onLoaded(index, data, mDataSource.hasMore());
        }
    }

    @Override
    public void onItemsUpdated(@NonNull DataSource<V> dataSource, @IntRange(from = 0) int from,
            @IntRange(from = 1) int to) {
        notifyContentItemRangeChanged(from, to);
    }

    @Override
    public void onItemsRemoved(@NonNull DataSource<V> dataSource, @IntRange(from = 0) int index,
            @Size(min = 1) List<V> data) {
        notifyContentItemRangeRemoved(index, data.size());
    }

    @Override
    public void onError(@NonNull DataSource<V> dataSource, @NonNull Throwable e) {
        if (mLoadListener != null) {
            mLoadListener.onError(e);
        }
    }

}

