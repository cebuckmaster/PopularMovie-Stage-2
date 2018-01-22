package com.example.android.popularmovies.data;

/**
 * Trailer class used to store movie trailer details
 */

public class Trailer {

    private String mId;
    private String mVideoKey;
    private String mVideoName;
    private String mVideoSite;
    private String mVideoType;

    public Trailer(String id, String videoKey, String videoName, String videoSite, String videoType) {
        mId = id;
        mVideoKey = videoKey;
        mVideoName = videoName;
        mVideoSite = videoSite;
        mVideoType = videoType;
    }

    public String getId() {
        return mId;
    }

    public String getVideoKey() {
        return mVideoKey;
    }
    public String getVideoName() {
        return mVideoName;
    }
    public String getVideoSite() {
        return mVideoSite;
    }
    public String getVideoType() {
        return mVideoType;
    }

    public String getWebLink() {
        return "https://www.youtube.com/watch?v=" + mVideoKey;
    }
    public String getAppLink() {
        return "vnd.youtube:" + mVideoKey;
    }
}
