package com.example.android.popularmovies.utils;

import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;

/**
 * Generic Utils Class for Android applications.
 */

public class Utils {

    /**
     * This converts a bitmap into a byte[] for uploading into database
     * @param bitmap
     * @return
     */
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }
}
