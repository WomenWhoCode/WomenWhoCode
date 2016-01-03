package com.example.womenwhocode.womenwhocode.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by zassmin on 10/16/15.
 */
@ParseClassName("Feature")
public class Feature extends ParseObject {
    public static final String AUTO_SUBSCRIBE_KEY = "auto_subscribe";
    private static final String IMAGE_URL_KEY = "image_url";
    private static final String TITLE_KEY = "title";
    private static final String NETWORK_KEY = "network";
    private static final String DESCRIPTION_KEY = "description";
    private static final String HEX_COLOR_KEY = "hex_color";
    private static final String SUBSCRIBE_COUNT = "subscribe_count";
    private static final String PHOTO_KEY = "photo_file";

    public void setSubscribeCount(int count) {
        put(SUBSCRIBE_COUNT, count);
    }

    public int getSubscribeCount() {
        int count;
        Object c = this.get(SUBSCRIBE_COUNT);
        if (c != null) {
            count = Integer.parseInt(c.toString());
        } else {
            count = 0;
        }
        return count;
    }

    public void setAutoSubscribe(boolean autoSubscribe) {
        put(AUTO_SUBSCRIBE_KEY, autoSubscribe);
    }

    public boolean getAutoSubscribe() {
        return this.getBoolean(AUTO_SUBSCRIBE_KEY);
    }

    public void setDescription(String description) {
        put(DESCRIPTION_KEY, description);
    }

    public String getDescription() {
        return this.get(DESCRIPTION_KEY).toString();
    }

    public void setTitle(String title) {
        put(TITLE_KEY, title);
    }

    public String getTitle() {
        return this.get(TITLE_KEY).toString();
    }

    public void setNetwork(Network network) {
        put(NETWORK_KEY, network);
    }

    public Network getNetwork() {
        return (Network) getParseObject(NETWORK_KEY);
    }

    public void setImageUrl(String imageUrl) {
        put(IMAGE_URL_KEY, imageUrl);
    }

    public String getImageUrl() {
        String url = null;
        Object image = this.get(IMAGE_URL_KEY);
        if (image != null) {
            url = image.toString();
        }
        return url;
    }

    public String getHexColor() {
        return this.get(HEX_COLOR_KEY).toString();
    }

    public void setHexColor(String hexColor) {
        put(HEX_COLOR_KEY, hexColor);
    }

    public ParseFile getPhotoFile() {
        return getParseFile(PHOTO_KEY);
    }

    public void setPhotoFile(ParseFile file) {
        put(PHOTO_KEY, file);
    }
}
