package com.yahoo.topics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.Date;

public class TopicPreferences {
    private static final String SP_KEY_INVITATION_CODE_FROM_CAMPAIGN =
            "SP_KEY_INVITATION_CODE_FROM_CAMPAIGN";
    private static final String SP_LAST_SEEN_MESSAGE_ = "SP_LAST_SEEN_MESSAGE_";
    private static final String SP_KEY_FIRST_TIME_GROUP_ID = "SP_KEY_FIRST_TIME_GROUP_ID";
    private static final String SP_KEY_IS_FIRST_TIME = "SP_KEY_IS_FIRST_TIME";

    private static final String SHARED_PREF_FILE = "spotter_pref";
    private static final String SP_KEY_LATEST_SHARE_LINK_CLICK_MESSAGE_TIME_ =
            "SP_KEY_LATEST_SHARE_LINK_CLICK_MESSAGE_TIME_";
    private static TopicPreferences sPreferences;
    private final SharedPreferences mAppSharedPref;
    private final SharedPreferences.Editor mPrefsEditor;

    @SuppressLint("CommitPrefEdits")
    private TopicPreferences(@NonNull Context context) {
        this.mAppSharedPref = context.getApplicationContext()
                .getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        this.mPrefsEditor = mAppSharedPref.edit();
    }

    public static TopicPreferences getInstance(@NonNull Context context) {
        if (sPreferences == null) {
            sPreferences = new TopicPreferences(context);
        }
        return sPreferences;
    }

    public void clearCampaignInviteCode() {
        removeEntryFromSP(SP_KEY_INVITATION_CODE_FROM_CAMPAIGN);
    }

    public String getAndClearCampaignInviteCode() {
        String inviteCodeFromSP = readString(SP_KEY_INVITATION_CODE_FROM_CAMPAIGN);
        removeEntryFromSP(SP_KEY_INVITATION_CODE_FROM_CAMPAIGN);
        return inviteCodeFromSP;
    }

    private String readString(@NonNull String key) {
        return mAppSharedPref.getString(key, null);
    }

    private void removeEntryFromSP(@NonNull String key) {
        mPrefsEditor.remove(key);
        mPrefsEditor.commit();
    }

    public long getLastSeenMessageForGroup(String groupId) {
        return readLong(SP_LAST_SEEN_MESSAGE_ + groupId, 0);
    }

    private long readLong(@NonNull String key, long defaultValue) {
        return mAppSharedPref.getLong(key, defaultValue);
    }

    public void storeLastSeenMessageForGroup(String objectId, long mLatestMessageTime) {
        writeLong(SP_LAST_SEEN_MESSAGE_ + objectId, mLatestMessageTime);
    }

    private void writeLong(@NonNull String key, long value) {
        mPrefsEditor.putLong(key, value);
        mPrefsEditor.commit();
    }

    public void storeCampaignInviteCode(String referral) {
        writeString(SP_KEY_INVITATION_CODE_FROM_CAMPAIGN, referral);
    }

    private void writeString(@NonNull String key, String value) {
        mPrefsEditor.putString(key, value);
        mPrefsEditor.commit();
    }

    public boolean getIsFirstTime() {
        return mAppSharedPref.getBoolean(SP_KEY_IS_FIRST_TIME, true);
    }

    public void storeIsFirstTime(boolean isFirstTime) {
        mPrefsEditor.putBoolean(SP_KEY_IS_FIRST_TIME, isFirstTime);
        mPrefsEditor.commit();
    }

    public Date getLatestShareLinkClickMessageTime(@NonNull String groupId) {
        long time =
                mAppSharedPref.getLong(SP_KEY_LATEST_SHARE_LINK_CLICK_MESSAGE_TIME_ + groupId, 0L);
        if (time == 0L) {
            return null;
        }
        return new Date(time);
    }

    public void setLatestShareLinkClickMessageTime(@NonNull String groupId, @NonNull Date date) {
        writeLong(SP_KEY_LATEST_SHARE_LINK_CLICK_MESSAGE_TIME_ + groupId, date.getTime());
    }
}
