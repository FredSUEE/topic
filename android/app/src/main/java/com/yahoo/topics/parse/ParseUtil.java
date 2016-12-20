package com.yahoo.topics.parse;

import android.support.annotation.NonNull;

import com.parse.ParseObject;
import com.yahoo.topics.base.BaseActivity;
import com.yahoo.topics.utils.TaskObservable;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import bolts.Task;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class ParseUtil {


    public static <E extends ParseObject> List<E> uniqueObjects(List<E> input) {
        LinkedHashMap<String, E> set = new LinkedHashMap<>();
        for (E e : input) {
            set.put(e.getObjectId(), e);
        }
        return new LinkedList<>(set.values());
    }

    public static boolean isEqual(ParseObject a, ParseObject b) {
        if (a == b) {
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (a == null || b == null) {
            return false;
        }
        return (a.getClass().equals(b.getClass()) && a.getObjectId().equals(b.getObjectId()));
    }

    public static <E extends ParseObject> Observable<E> fetchInBackgroundObserveOnMain(
            @NonNull BaseActivity activity, @NonNull final E object) {
        return TaskObservable
                .defer(new Func0<Task<E>>() {
                    @Override
                    public Task<E> call() {
                        return object.fetchIfNeededInBackground();
                    }
                })
                .subscribeOn(Schedulers.io())
                .compose(activity.<E>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
