package se.maj7.imagegallerythree;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class ImageProcessor {

    public static Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedByte;
    }

    public static String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 85;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitMapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, byteArrayBitMapStream);
        byte[] b = byteArrayBitMapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encodedImage;
    }

    public static Bitmap scaleDownBitmap(Bitmap sourceBitmap, int newSize) {
        Bitmap bitmap = sourceBitmap;
        Bitmap squaredBitmap = cropToSquare(bitmap);
        bitmap = Bitmap.createScaledBitmap(squaredBitmap, newSize, newSize, true);

        return bitmap;
    }

    public static Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int cropX = (width > height) ? ((width - height) / 2) : 0;
        int cropY = (width < height) ? ((height - width) / 2) : 0;

        if (cropX > 0) {
            width = width - (cropX * 2);
        } else {
            height = height - (cropY * 2);
        }

        Bitmap imageSquare = Bitmap.createBitmap(bitmap, cropX, cropY, width, height);

        return imageSquare;
    }
}
