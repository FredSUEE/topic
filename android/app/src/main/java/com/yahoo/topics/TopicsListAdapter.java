package com.yahoo.topics;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yahoo.topics.adapter.NetworkAdapter;
import com.yahoo.topics.base.BaseViewHolder;
import com.yahoo.topics.datasouce.DataSource;
import com.yahoo.topics.parse.ParseTopic;

public class TopicsListAdapter extends NetworkAdapter<ParseTopic, BaseViewHolder<ParseTopic>> {

    public TopicsListAdapter(@NonNull Context context, @NonNull
            DataSource<ParseTopic> dataSource) {
        super(context, dataSource);
    }

    @Override
    protected TopicItemViewHolder onCreateContentItemViewHolder(ViewGroup parent,
            int contentViewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic, parent, false);
        return new TopicItemViewHolder(view, this);
    }

    @Override
    protected void onBindContentItemViewHolder(BaseViewHolder<ParseTopic> contentViewHolder,
            int position) {
        super.onBindContentItemViewHolder(contentViewHolder, position);
        contentViewHolder.bind(getItem(position));
    }

    @Override
    protected BaseViewHolder<ParseTopic> onCreateHeaderItemViewHolder(ViewGroup parent,
            int headerViewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topic_list_header, parent, false);
        return new TopicHeaderViewHolder(view, this);
    }

    @Override
    protected int getHeaderItemCount() {
        return 1;
    }

    @Override
    protected void onBindHeaderItemViewHolder(BaseViewHolder<ParseTopic> headerViewHolder,
            int position) {
        super.onBindHeaderItemViewHolder(headerViewHolder, position);
    }
}
