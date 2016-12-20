package com.yahoo.topics.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yahoo.topics.R;
import com.yahoo.topics.TopicPreferences;
import com.yahoo.topics.utils.RxEventBus;

import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public abstract class BaseActivity extends RxAppCompatActivity {

    protected ActionBar mActionBar;
    protected ViewGroup mAlertView;
    protected CharSequence mTitle;
    private boolean mIsActive;
    private TopicPreferences mPreferences;
    private CompositeSubscription mCompositeSubscription;

    protected static final String LEFT_BUFFER_FOR_TOOLBAR_COLLAPSED_TITLE_WHEN_NO_BACK_BUTTON =
            "   ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = TopicPreferences.getInstance(this);

        // Only allow portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // A default activity view with only a framelayout place holder
        setContentView(getLayout());

        ButterKnife.bind(this);
    }

    // try to set toolbar if there are any
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        if (toolbar != null) {
            try {
                setSupportActionBar(toolbar);
            } catch (Throwable e) {
                // This is one of the fixes on the web trying to fix the old samsung tab crash on toolbar, please refer ANDROID-3205
                // This is not needed in theory, since we tried to fix it using the proguard omitting way, but put it here for a safe check point
            }
        }

        mActionBar = getSupportActionBar();

        View view = findViewById(R.id.main_content_stub);
        if (view instanceof ViewStub) {
            ViewStub viewStub = (ViewStub) view;
            viewStub.setLayoutResource(getContentLayout());
            viewStub.setVisibility(View.VISIBLE);
        }

        View fabView = findViewById(R.id.fab_layout_stub);
        if (fabView instanceof ViewStub && getFabLayout() != 0) {
            ViewStub viewStub = (ViewStub) fabView;
            viewStub.setLayoutResource(getFabLayout());
            viewStub.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Get the layout of the activity which use R.layout.default_activity as default
     *
     * @return the layout resource ID to use for this activity
     */
    @LayoutRes
    protected int getLayout() {
        return R.layout.default_activity;
    }

    /**
     * Get the layout of the activity which use R.layout.default_activity_content as default. This layout of the
     * activity will be added to the default layout which has toolbar etc.
     *
     * @return the layout resource ID to use for this activity
     */
    @LayoutRes
    protected int getContentLayout() {
        return R.layout.default_activity_content;
    }

    /**
     * Get the layout of the fab if there is
     *
     * @return the layout resource ID
     */
    @LayoutRes
    protected int getFabLayout() {
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsActive = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsActive = false;

        clearSubscription();
    }

    private void clearSubscription() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
    }

    protected <E> void subscribeEvent(@NonNull final Class<E> clazz, @NonNull Action1<E> handler) {
        addSubscription(
                RxEventBus.getDefault().subscribe(clazz, handler, AndroidSchedulers.mainThread()));
    }

    private void addSubscription(Subscription s) {
        if (mCompositeSubscription == null || mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(s);
    }

    public boolean isActive() {
        return mIsActive;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (mActionBar != null) {
            mActionBar.setTitle(mTitle);
        }
    }

    /**
     * Go to the default activity and clear all the back stack
     */
    public void startDefaultActivityAndClearBackStack() {
        startNextActivityAndClearBackStack(null);
    }

    /**
     * Go to the next activity and clear all the back stack
     *
     * @param nextActivityClass activity to go next
     */
    public void startNextActivityAndClearBackStack(Class<?> nextActivityClass) {
        startNextActivityAndClearBackStack(nextActivityClass, null);
    }

    /**
     * Go to the next activity with bundle and clear all the back stack
     *
     * @param nextActivityClass activity to go next
     * @param bundle            bundle in the intent
     */
    public void startNextActivityAndClearBackStack(Class<?> nextActivityClass, Bundle bundle) {

        // flag FLAG_ACTIVITY_CLEAR_TASK and FLAG_ACTIVITY_NEW_TASK is going to clear to the activity history
        startNextActivity(nextActivityClass, bundle,
                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * start next activity
     * <p/>
     * When nextActivity is Null, if logged in, then take users to Feed, if not logged in, then take users to Trend
     * screen.
     *
     * @param nextActivityClass activity to go next
     * @param b                 bundle in the intent
     * @param flags             flags in the intent
     */
    private void startNextActivity(Class<?> nextActivityClass, Bundle b, int flags) {
//        if (nextActivityClass == null) {
//            nextActivityClass = DispatchActivity.class;
//        }

        if (getClass() == nextActivityClass) {
            return;
        }

        Intent intent = new Intent(this, nextActivityClass);
        if (flags != 0) {
            intent.setFlags(flags);
        }

        if (b != null) {
            intent.putExtras(b);
        }

        startActivity(intent);
    }

    /**
     * On back pressed listener
     */
    public interface onBackPressedListener {
        boolean onBackPressedHandled();
    }

    /**
     * Interface for Fragment back stack state monitor
     */
    public interface FragmentPoppedOutStackCallback {
        /**
         * Fragment interested gets popped out
         */
        void popped();
    }

    private View focusedViewOnActionDown;
    private boolean touchWasInsideFocusedView;

    protected boolean dismissKeyboardOnTouchOutside() {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (!dismissKeyboardOnTouchOutside()) {
            return super.dispatchTouchEvent(ev);
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                focusedViewOnActionDown = getCurrentFocus();
                if (focusedViewOnActionDown != null) {
                    final Rect rect = new Rect();
                    final int[] coordinates = new int[2];

                    focusedViewOnActionDown.getLocationOnScreen(coordinates);

                    rect.set(coordinates[0], coordinates[1],
                            coordinates[0] + focusedViewOnActionDown.getWidth(),
                            coordinates[1] + focusedViewOnActionDown.getHeight());

                    final int x = (int) ev.getX();
                    final int y = (int) ev.getY();

                    touchWasInsideFocusedView = rect.contains(x, y);
                }
                break;

            case MotionEvent.ACTION_UP:

                if (focusedViewOnActionDown != null) {
                    // dispatch to allow new view to (potentially) take focus
                    final boolean consumed = super.dispatchTouchEvent(ev);

                    final View currentFocus = getCurrentFocus();

                    // if the focus is still on the original view and the touch was inside that view,
                    // leave the keyboard open.  Otherwise, if the focus is now on another view and that view
                    // is an EditText, also leave the keyboard open.
                    if (currentFocus != null) {
                        if (currentFocus.equals(focusedViewOnActionDown)) {
                            if (touchWasInsideFocusedView) {
                                return consumed;
                            }
                        } else if (currentFocus instanceof EditText) {
                            return consumed;
                        }
                    }

                    // the touch was outside the originally focused view and not inside another EditText,
                    // so close the keyboard
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(
                            focusedViewOnActionDown.getWindowToken(), 0);
                    focusedViewOnActionDown.clearFocus();

                    return consumed;
                }
                break;
        }

        return super.dispatchTouchEvent(ev);
    }
}
