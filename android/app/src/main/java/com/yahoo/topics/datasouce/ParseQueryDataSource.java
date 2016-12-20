package com.yahoo.topics.datasouce;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.yahoo.topics.parse.ParseCommonFields;
import com.yahoo.topics.utils.TaskObservable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ParseQueryDataSource<T extends ParseObject> extends ListDataSource<T> {

    // The number of mObjects to show per page (default: 25)
    private int mObjectsPerPage = 25;
    private Set<ParseQuery> mRunningQueries =
            Collections.newSetFromMap(new ConcurrentHashMap<ParseQuery, Boolean>());
    // Used to keep track of the pages of mObjects when using CACHE_THEN_NETWORK. When using this,
    // the data will be flattened and put into the mObjects list.
    private boolean mHasNextPage = true;
    private boolean mPendingQuery = false;
    private QueryFactory<T> mQueryFactory;
    private Date mLatestMessageDate;
    private Subscription mSubscription;

    public ParseQueryDataSource(QueryFactory<T> queryFactory) {
        this.mQueryFactory = queryFactory;
    }

    private void loadObjects(final Date date) {
        final ParseQuery<T> query = this.mQueryFactory.create();
        //if (query.getCachePolicy() != ParseQuery.CachePolicy.IGNORE_CACHE) {
        //    throw new UnsupportedOperationException();
        //}

        if (this.mObjectsPerPage > 0) {
            query.setLimit(mObjectsPerPage + 1);
        }
        if (date != null) {
            query.whereLessThan(ParseCommonFields.KEY_CREATED_AT, date);
        }

        notifyLoading();
        mRunningQueries.add(query);

        // TODO convert to Tasks and CancellationTokens
        // (depends on https://github.com/ParsePlatform/Parse-SDK-Android/issues/6)https://github.com/ParsePlatform/Parse-SDK-Android/issues/6)
        query.findInBackground(
                new FindCallback<T>() {
                    @Override
                    public void done(List<T> foundObjects, ParseException e) {
                        if (!mRunningQueries.contains(query)) {
                            return;
                        }
                        // In the case of CACHE_THEN_NETWORK, two callbacks will be called. We can only remove the
                        // query after the second callback.
                        mRunningQueries.remove(query);

                        if ((e != null) &&
                                ((e.getCode() == ParseException.CONNECTION_FAILED) ||
                                        (e.getCode() != ParseException.CACHE_MISS))) {
                            mHasNextPage = true;
                            notifyError(e);
                        } else if (foundObjects != null) {
                            updateData(foundObjects);
                        } else if (e != null) {
                            notifyError(e);
                        } else {
                            notifyError(new Throwable("Unknown error"));
                        }

                    }
                });
    }

    private void updateData(List<T> foundObjects) {
        int index = getCount();
        mHasNextPage = (foundObjects.size() > mObjectsPerPage);

        if (foundObjects.size() > mObjectsPerPage) {
            // Remove the last object, fetched in order to tell us whether there was a "next page"
            foundObjects.remove(mObjectsPerPage);
        }
        if (getCount() == 0 && foundObjects.size() > 0) {
            mLatestMessageDate = foundObjects.get(0).getCreatedAt();
        }
        addItems(index, foundObjects);

        if (mHasNextPage && mPendingQuery) {
            mPendingQuery = false;
            loadNextPage();
        }
    }

    @Override
    public void clear() {
        cancelAllQueries();
        super.clear();
        mHasNextPage = true;
        mLatestMessageDate = null;
    }

    private void cancelAllQueries() {
        for (ParseQuery q : mRunningQueries) {
            q.cancel();
        }
        mRunningQueries.clear();
        mPendingQuery = false;
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
        mSubscription = null;
    }

    @Override
    public boolean hasMore() {
        return mHasNextPage;
    }

    /**
     * Loads the next page of mObjects, appends to table, and notifies the UI that the model has changed.
     */
    @Override
    public void loadNextPage() {
        if (!hasMore()) {
            throw new UnsupportedOperationException("No next page");
        }
        if (getCount() == 0 && mRunningQueries.size() == 0) {
            loadObjects(null);
        } else if (mRunningQueries.size() == 0) {
            loadObjects(getItem(getCount() - 1).getCreatedAt());
        } else {
            mPendingQuery = true;
        }
    }

    @Override
    public void fetchNewData() {
        if (mSubscription != null) {
            return;
        }
        ParseQuery<T> query = mQueryFactory.create()
                .setLimit(1000);
        if (mLatestMessageDate != null) {
            query.whereGreaterThan(ParseCommonFields.KEY_CREATED_AT, mLatestMessageDate);
        } else if (getCount() > 0) {
            query.whereGreaterThan(ParseCommonFields.KEY_CREATED_AT, getItem(0).getCreatedAt());
        } else if (hasMore()) {
            return;
        }
        mSubscription = TaskObservable.just(query.findInBackground())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<T>>() {
                    @Override
                    public void call(List<T> ts) {
                        if (mSubscription != null && mSubscription.isUnsubscribed()) {
                            return;
                        }
                        mSubscription = null;
                        if (ts != null && ts.size() > 0) {
                            addItems(0, ts);
                            mLatestMessageDate = getItem(0).getCreatedAt();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mSubscription = null;
                    }
                });
    }

    /**
     * Implement to construct your own custom {@link ParseQuery} for fetching mObjects.
     */
    public interface QueryFactory<T extends ParseObject> {
        ParseQuery<T> create();
    }
}
