package com.yahoo.topics.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

//import com.polyvore.utils.eventbus.PVBusEvent;

/**
 * Basic class for recycler view adapter handling
 * <p/>
 * This handles single header view and footer view case for now, and we can easily upgrade to multiple header or footer if we need.
 * Created by hongjiedong on 6/19/15.
 */

public abstract class RecyclerViewAdapter<VH extends RecyclerViewAdapter.ViewHolder>
        extends HeaderFooterRecyclerViewAdapter<VH> {

    public interface OnItemClickListener {
        void onHeaderClick(View v, int position);

        void onFooterClick(View v, int position);

        void onItemClick(View v, int position);
    }

    /**
     * Header view for the recycler
     */
    protected View mHeaderView;

    /**
     * Footer view the recycler
     */
    protected View mFooterView;

    protected boolean mIsHeaderVisible;
    protected boolean mIsFooterVisible;

    protected OnItemClickListener mListener;

    private String mLocation = "everything";

    /**
     * Default constructor
     */
    public RecyclerViewAdapter() {
    }

    public RecyclerViewAdapter(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Give header view
     *
     * @param header
     */
    public void setHeader(View header) {
        mHeaderView = header;
    }

    /**
     * Set footer view
     *
     * @param footer
     */
    public void setFooter(View footer) {
        mFooterView = footer;
    }

    /**
     * Update header visibility
     *
     * @param visible
     */
    public void setHeaderVisible(boolean visible) {
        if (mIsHeaderVisible != visible) {
            if (visible) {
                mIsHeaderVisible = visible;
                notifyHeaderItemInserted(0);
            } else {
                notifyHeaderItemRemoved(0);
                mIsHeaderVisible = visible;
            }
        }
    }

    /**
     * Set footer visibility
     *
     * @param visible
     */
    public void setFooterVisible(boolean visible) {
        if (mIsFooterVisible != visible) {
            if (visible) {
                mIsFooterVisible = visible;
                notifyFooterItemInserted(0);
            } else {
                notifyFooterItemRemoved(0);
                mIsFooterVisible = visible;
            }
        }
    }

    @Override
    protected int getHeaderItemCount() {
        return mHeaderView == null || !mIsHeaderVisible ? 0 : 1;
    }

    @Override
    protected int getFooterItemCount() {
        return mFooterView == null || !mIsFooterVisible ? 0 : 1;
    }

    @Override
    protected int getContentItemCount() {
        return 0;
    }

    @Override
    protected VH onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
        return null;
    }

    @Override
    protected VH onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType) {
        return null;
    }

    @Override
    protected VH onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        return null;
    }

    @Override
    protected void onBindHeaderItemViewHolder(VH headerViewHolder, int position) {

    }

    @Override
    protected void onBindFooterItemViewHolder(VH footerViewHolder, int position) {

    }

    @Override
    protected void onBindContentItemViewHolder(VH contentViewHolder, int position) {

    }

    private void onItemClick(VH viewHolder) {
        if (mListener == null) {
            return;
        }

        int position = viewHolder.getLayoutPosition();
        int headerItemCount = getHeaderItemCount();
        int contentItemCount = getContentItemCount();
        if (headerItemCount > 0 && position < headerItemCount) {
            mListener.onHeaderClick(viewHolder.itemView, position);
        } else if (contentItemCount > 0 && position - headerItemCount < contentItemCount) {
            mListener.onItemClick(viewHolder.itemView, position - headerItemCount);
        } else {
            mListener.onFooterClick(viewHolder.itemView,
                    position - headerItemCount - contentItemCount);
        }
    }

    /**
     * single view holder as wrapper of some views needed in recycler
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewAdapter mAdapter;

        public ViewHolder(View itemView, RecyclerViewAdapter adapter) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (adapter != null) {
                mAdapter = adapter;
                itemView.setOnClickListener(this);
            }
        }

        @Override
        public final void onClick(View v) {
            mAdapter.onItemClick(this);
        }

        /**
         * A caught all event function
         */
//        @SuppressWarnings("unused")
//        public void onEvent (PVBusEvent event) {
//        }
        protected RecyclerViewAdapter getAdapter() {
            return mAdapter;
        }

        public void onRecycle() {

        }
    }

    @Override
    public void onViewAttachedToWindow(VH holder) {
        super.onViewAttachedToWindow(holder);
        // somehow needs this function to compile
    }

    @Override
    public void onViewDetachedFromWindow(VH holder) {
        super.onViewDetachedFromWindow(holder);
        // somehow needs this function to compile
    }

    @Override
    protected void onBindEmptyItemViewHolder(VH contentViewHolder) {

    }

    @Override
    protected VH onCreateEmptyItemViewHolder(ViewGroup parent) {
        return null;
    }


    @NonNull
    public String getCurrentLocation() {
        return mLocation;
    }

    public void setCurrentLocation(@NonNull String location) {
        mLocation = location;
    }
}
