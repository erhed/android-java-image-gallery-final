package se.maj7.imagegallerythree;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageItem {

    private String mTitle;
    private String mImageUriString;
    private String mThumbnail;
    private Date mDateCreated;

    public ImageItem(String title, String uriString, String thumb) {
        mTitle = title;
        mImageUriString = uriString;
        mThumbnail = thumb;
        mDateCreated = new Date();
    }

    // GET

    public String getTitle() {
        return mTitle;
    }

    public String getImageUriString() {
        return mImageUriString;
    }

    public String getThumb() {
        return mThumbnail;
    }

    public Date getDate() {
        return mDateCreated;
    }

    public String getDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(mDateCreated);
    }
}
