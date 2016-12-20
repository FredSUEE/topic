package com.yahoo.topics.base;

import android.view.View;

import com.yahoo.topics.adapter.NetworkAdapter;
import com.yahoo.topics.adapter.RecyclerViewAdapter;

import butterknife.ButterKnife;

public abstract class BaseViewHolder<T> extends NetworkAdapter.ViewHolder {

    public BaseViewHolder(View itemView, RecyclerViewAdapter adapter) {
        super(itemView, adapter);
        ButterKnife.bind(itemView);
    }

    public abstract void bind(T message);
}
