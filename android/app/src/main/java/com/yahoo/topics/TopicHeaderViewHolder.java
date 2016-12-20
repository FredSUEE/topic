package com.yahoo.topics;


import android.view.View;

import com.yahoo.topics.adapter.RecyclerViewAdapter;
import com.yahoo.topics.base.BaseViewHolder;
import com.yahoo.topics.parse.ParseTopic;

public class TopicHeaderViewHolder extends BaseViewHolder<ParseTopic> {

    public TopicHeaderViewHolder(View itemView,
            RecyclerViewAdapter adapter) {
        super(itemView, adapter);
    }

    @Override
    public void bind(ParseTopic message) {

    }
}
