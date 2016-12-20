package com.yahoo.topics.parse;

import android.support.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Topic")
public class ParseTopic extends ParseObject {
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE_URL = "image_url";

    @NonNull
    public static ParseTopic create(
            @NonNull String title,
            @NonNull String description
    ) {
        ParseTopic topic = new ParseTopic();
        topic.put(KEY_TITLE, title);
        topic.put(KEY_DESCRIPTION, description);
        return topic;
    }

    public static ParseQuery<ParseTopic> find() {
        ParseQuery<ParseTopic> query = ParseQuery.getQuery(ParseTopic.class);
        query.addDescendingOrder(ParseCommonFields.KEY_CREATED_AT);
        return query;
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public String getImageUrl() {
        return getString(KEY_IMAGE_URL);
    }

}
