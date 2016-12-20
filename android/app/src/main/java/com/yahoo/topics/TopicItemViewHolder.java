package com.yahoo.topics;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yahoo.topics.adapter.RecyclerViewAdapter;
import com.yahoo.topics.base.BaseViewHolder;
import com.yahoo.topics.parse.ParseTopic;

public class TopicItemViewHolder extends BaseViewHolder<ParseTopic> {
    public TextView title;
    public TextView description;
    public ImageView image;
    private Context mContext;

    public TopicItemViewHolder(View itemView, RecyclerViewAdapter adapter) {
        super(itemView, adapter);
        mContext = itemView.getContext();
        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        image = (ImageView) itemView.findViewById(R.id.topic_image);
    }

    @Override
    public void bind(ParseTopic topic) {
        title.setText(topic.getTitle());
        description.setText(topic.getDescription());

        String url = topic.getImageUrl();
        Log.e("here url ", "here url " + url);
        Glide
                .with(mContext)
                .load("https://pbs.twimg.com/profile_images/785812671086129153/v-vrlL3n.jpg")
                .error(R.drawable.ic_discover)
                .centerCrop()
                .into(image);
    }
}
