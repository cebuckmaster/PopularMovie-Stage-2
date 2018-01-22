package com.example.android.popularmovies.data;

/**
 * MovieReview class stores details about review of movies.
 */

public class MovieReview {

    private String mReviewId;
    private String mReviewAuthor;
    private String mReview;
    private String mReviewUrl;

    public MovieReview (String id, String author, String review, String url) {
        mReviewId = id;
        mReviewAuthor = author;
        mReview = review;
        mReviewUrl = url;
    }

    public String getId() {
        return mReviewId;
    }
    public String getAuthor() {
        return "By: " + mReviewAuthor;
    }
    public String getReview() {
        return mReview;
    }
    public String getUrl() {
        return mReviewUrl;
    }

}
