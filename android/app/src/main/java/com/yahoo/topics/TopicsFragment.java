package com.yahoo.topics;


import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseQuery;
import com.yahoo.topics.adapter.NetworkAdapter;
import com.yahoo.topics.datasouce.ListDataSource;
import com.yahoo.topics.datasouce.ParseQueryDataSource;
import com.yahoo.topics.parse.ParseTopic;
import com.yahoo.topics.utils.RecyclerViewMarginDecoration;

import java.util.ArrayList;
import java.util.List;

public class TopicsFragment extends Fragment implements
        NetworkAdapter.OnLoadListener<ParseTopic> {
    private String title;
//    private SimpleTopicListAdapter simpleTopicAdapter;
    private TopicsListAdapter simpleTopicAdapter;
    private RecyclerView topiclist;

    // newInstance constructor for creating fragment with arguments
    public static TopicsFragment feeds(String title) {
        TopicsFragment fragment = new TopicsFragment();
        Bundle args = new Bundle();
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("someTitle");

    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topics, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        topiclist = (RecyclerView) view.findViewById(R.id.topics_list);

        ParseQueryDataSource.QueryFactory<ParseTopic> queryFactory =
                new ParseQueryDataSource.QueryFactory<ParseTopic>() {
                    @Override public ParseQuery<ParseTopic> create() {
                        return ParseTopic.find();
                    }
                };
        List<ParseTopic> topics = new ArrayList<>();
        topics.add(ParseTopic.create("test", "djofejoajfe"));
        ListDataSource<ParseTopic> dataSource = new ListDataSource<>(topics);
        topiclist.setHasFixedSize(true);
        topiclist.setLayoutManager(new LinearLayoutManager(getContext()));
        topiclist.addItemDecoration(RecyclerViewMarginDecoration
                .fromDimenIdForBottom(getContext(), R.dimen.recycler_view_item_margin));

        simpleTopicAdapter = new TopicsListAdapter(getContext(),
                new ParseQueryDataSource<>(queryFactory));
//        simpleTopicAdapter = new TopicsListAdapter(getContext(),
//                new ListDataSource<>(topics));
//        simpleTopicAdapter = new SimpleTopicListAdapter(getContext(), topics);
        topiclist.setAdapter(simpleTopicAdapter);

        simpleTopicAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {

            }
        });
        simpleTopicAdapter.setLoadListener(this);
    }

    @Override public void onLoading() {

    }

    @Override public void onLoaded(@IntRange(from = 0) int index, @NonNull List<ParseTopic> objects,
            boolean hasMore) {

    }

    @Override public void onError(@NonNull Throwable e) {

    }

    @Override public void onNewDataFetched(@NonNull List<ParseTopic> data) {

    }
}
