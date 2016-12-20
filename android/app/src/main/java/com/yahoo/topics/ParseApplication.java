/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.yahoo.topics;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.yahoo.topics.parse.ParseTopic;

public class ParseApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setTheme(R.style.AppTheme);

        ParseObject.registerSubclass(ParseTopic.class);
//        ParseObject.registerSubclass(ParseGroup.class);
//        ParseObject.registerSubclass(ParseMessage.class);
//        ParseObject.registerSubclass(ParseActivityLog.class);
//        ParseObject.registerSubclass(ParsePhoto.class);
//        ParseObject.registerSubclass(ParseActivityProgress.class);
//        ParseObject.registerSubclass(ParseActivityTemplate.class);
//        ParseObject.registerSubclass(ParseGroupInvite.class);

        String applicationId = BuildConfig.PARSE_APP_ID;
        String host = BuildConfig.HOST;
        String server = String.format("https://%s/parse/", host);
        if (BuildConfig.FLAVOR.equals("local")) {
            String port = BuildConfig.PORT;
            server = String.format("http://%s:%s/parse/", host, port);
        }

        Parse.Configuration.Builder parseBuilder = new Parse.Configuration.Builder(this)
                .applicationId(applicationId)
                .clientKey("")
                .server(server)
                .enableLocalDataStore();

        // Add your initialization code here
        Parse.initialize(parseBuilder.build());

        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        // defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * Check whether the app is in debug mode
     *
     * @return True if the app is in debug mode
     */
    public boolean isDebugMode() {
        return (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    }
}
