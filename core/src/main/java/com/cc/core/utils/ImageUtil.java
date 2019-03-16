package com.cc.core.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;
import android.util.Log;

import com.cc.core.log.KLog;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.Assert;

import static android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC;

public class ImageUtil {

    private static final String TAG = ImageUtil.class.getSimpleName();

    private static final int WX_THUMB_SIZE = 120;
    private static final int WX_IMAGE_SIZE = 32768;//微信分享图片大小限制

    public static byte[] imageToByteArray(String imgPath) {
        BufferedInputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream(imgPath));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int size = 0;
            byte[] temp = new byte[1024];
            while ((size = in.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            in.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

    public static Bitmap extractThumbNail(final String path, final int height, final int width, final boolean crop) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }

            KLog.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            KLog.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;

            KLog.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            if (bm == null) {
                Log.e(TAG, "bitmap decode failed");
                return null;
            }

            KLog.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
            if (scale != null) {
                if (bm != scale) {
                    KLog.i(TAG, "Full size image recycled");
                    bm.recycle();
                    bm = scale;
                } else {
                    KLog.i(TAG, "Resized bitmap was the same as the fullsize bitmap");
                }
            }

            if (crop) {
                final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
                if (cropped == null) {
                    return bm;
                }

                bm.recycle();
                bm = cropped;
                KLog.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
            }
            return bm;

        } catch (final OutOfMemoryError e) {
            Log.e(TAG, "decode bitmap failed: " + e.getMessage());
            options = null;
        }

        return null;
    }

    public static byte[] fileToWxThumb(String filePath) {
        Bitmap thumbBmp = extractThumbNail(filePath, WX_THUMB_SIZE, WX_THUMB_SIZE, false);
        if (thumbBmp == null) {
            return null;
        }
        byte[] bytes = bitmap2Bytes(thumbBmp, WX_IMAGE_SIZE);

        thumbBmp.recycle();

        return bytes;
    }

    public static byte[] bitmap2Bytes(Bitmap bitmap, int maxkb) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        int options = 100;
        while (output.toByteArray().length > maxkb && options != 10) {
            output.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, output);
            options -= 10;
        }
        return output.toByteArray();
    }

    public static String getVideoPathCover(String videoPath) {
        if (TextUtils.isEmpty(videoPath)) {
            return null;
        }
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videoPath);
        Bitmap bitmap = mmr.getFrameAtTime(1 * 1000 * 1000, OPTION_CLOSEST_SYNC);//获取1秒附近的关键帧
        if (bitmap == null) {
            return null;
        }

        File file = FileUtil.saveBitmap2File(bitmap);
        if (file == null) {
            return null;
        }

        return file.getAbsolutePath();
    }
}
