package com.yahoo.topics.utils;

import bolts.Continuation;
import bolts.Task;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;

/**
 * Created by jyuen on 1/29/16.
 */
public class TaskObservable {

    public static <R> Observable<R> just(final Task<R> task, final boolean nullable) {
        return Observable.create(
                new Observable.OnSubscribe<R>() {
                    @Override
                    public void call(final Subscriber<? super R> subscriber) {
                        task.continueWith(
                                new Continuation<R, Object>() {
                                    @Override
                                    public Object then(Task<R> t) throws Exception {
                                        if (t.isCancelled()) {
                                            subscriber.unsubscribe();
                                        } else if (t.isFaulted()) {
                                            Throwable error = t.getError();
                                            subscriber.onError(error);
                                        } else {
                                            R r = t.getResult();
                                            if (nullable || r != null) {
                                                subscriber.onNext(r);
                                            }
                                            subscriber.onCompleted();
                                        }
                                        return null;
                                    }
                                });
                    }
                });
    }

    public static <R> Observable<R> justNullable(Task<R> task) {
        return just(task, true);
    }

    public static <R> Observable<R> just(Task<R> task) {
        return just(task, false);
    }

    @Deprecated
    public static <R> Observable<R> just(Func0<Task<R>> task) {
        return defer(task);
    }

    public static <R> Observable<R> defer(final Func0<Task<R>> task) {
        return Observable.defer(
                new Func0<Observable<R>>() {
                    @Override
                    public Observable<R> call() {
                        return just(task.call());
                    }
                });
    }

    public static <R> Observable<R> deferNullable(final Func0<Task<R>> task) {
        return Observable.defer(
                new Func0<Observable<R>>() {
                    @Override
                    public Observable<R> call() {
                        return justNullable(task.call());
                    }
                });
    }

    public static <R> Observable<R> deferNonNull(Func0<Task<R>> task) {
        return deferNullable(task).doOnNext(
                new Action1<R>() {
                    @Override
                    public void call(R r) {
                        if (r == null) {
                            throw new NullPointerException();
                        }

                    }
                });
    }

}
