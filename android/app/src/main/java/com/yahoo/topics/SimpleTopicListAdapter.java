package com.yahoo.topics;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yahoo.topics.base.BaseViewHolder;
import com.yahoo.topics.parse.ParseTopic;

import java.util.List;

public class SimpleTopicListAdapter extends RecyclerView.Adapter<SimpleTopicListAdapter.ViewHolder> {

    // Store a member variable for the contacts
    private List<ParseTopic> mContacts;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public SimpleTopicListAdapter(Context context, List<ParseTopic> topics) {
        mContacts = topics;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    @Override
    public SimpleTopicListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_topic, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SimpleTopicListAdapter.ViewHolder viewHolder, int position) {

        // Get the data model based on position
        ParseTopic topic = mContacts.get(position);

        // Set item views based on your views and data model
        TextView title = viewHolder.title;
        title.setText(topic.getTitle());
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView title;
        public TextView description;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }
}
