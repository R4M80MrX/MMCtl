package com.cc.core.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.cc.core.ApplicationContext;
import com.cc.core.log.KLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

import static android.os.Environment.MEDIA_MOUNTED;


/**
 * Created by majinhai on 2016/10/17.
 */

public class FileUtil {

    public final static String ROOT_DIR = "/mmctrl";
    public final static String CACHE_DIR = "cache";
    public final static String LOG_DIR = "logs";
    public final static String IMAGE_CACHE = "img";//存储的文件夹
    public final static String VIDEO_CACHE = "video";//存储的文件夹
    public final static String VOICE_CACHE = "voice";//存储的文件夹
    public final static String PRODUCT_CACHE_DIR = "package";

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * 文件读取
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    return getCacheDir(context) + "/" + split[1];
//                    returnstock "/storage/usbcard1/app.exe/盘点导出单/盘点单_5.txt";
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 获取文件内容
     *
     * @return
     */
    public static String getFileContent(File file) {
        InputStreamReader inputStreamReader = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("#");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static File getTmpDir() {
        return getSubDirectory("tmp");
    }

    public static File getCrashDir() {
        return getSubDirectory("crash");
    }

    /**
     * get internal cache dir, the path of the internal cache directory is '/data/data/package_name_of_app/cache', write/read file(s) in this
     * directory no need permission request.
     *
     * @return
     */
    public static File getInternalCacheDir() {
        return ApplicationContext.application().getCacheDir();
    }

    /**
     * get internal files dir, the path of the internal files directory is '/data/data/package_name_of_app/files', write/read file(s) in this
     * directory no need permission request.
     *
     * @return
     */
    public static File getInternalFileDir() {
        return ApplicationContext.application().getFilesDir();
    }

    /**
     * get internal product package download dir, the path of the directory is '/data/data/package_name_of_app/cache/package', write/read file(s) in this
     * directory no need permission request.
     *
     * @return
     */
    public static File getInternalProductCacheDir() {
        File packageCache = new File(getInternalCacheDir(), PRODUCT_CACHE_DIR);
        if (!packageCache.exists()) {
            packageCache.mkdir();
        }
        return packageCache;
    }

    /**
     * get external cache directory, the path of this directory is '/mnt/sdcard/nexttao/cache', write/read file(s) in this
     * directory need permission request.
     */
    public static File getExternalCacheDir() {
        return getSubDirectory(CACHE_DIR);
    }

    /**
     * get external cache directory, the path of this directory is '/mnt/sdcard/nexttao/logs', write/read file(s) in this
     * directory need permission request.
     */
    public static File getExternalLogDir() {
        return getSubDirectory(LOG_DIR);
    }

    private static File getSubDirectory(String name) {
        File file = getExternalRootDir();
        File cache = new File(file, name);
        if (!cache.exists()) {
            cache.mkdir();
        }

        return cache;
    }

    /**
     * get external root directory, the path of this directory is '/mnt/sdcard/nexttao', write/read file(s) in this
     * directory need permission request.
     */
    public static File getExternalRootDir() {
        File appCacheDir = null;
        if (canAccessExternalStorage()) {
            appCacheDir = getExternalStorageRootDir();
        }
        if (appCacheDir == null) {
            appCacheDir = ApplicationContext.application().getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + ApplicationContext.application().getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }

        if(!appCacheDir.exists()) {
            appCacheDir.mkdir();
        }
        return appCacheDir;
    }

    public static boolean canAccessExternalStorage() {
        return MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                && hasExternalStoragePermission(ApplicationContext.application());
    }

    private static File getExternalStorageRootDir() {
        File rootDir = new File(Environment.getExternalStorageDirectory(),
                ROOT_DIR);
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }
        return rootDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context
                .checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    public static File getImageCacheDirectory() {
        File file;
        if (hasExternalStoragePermission(ApplicationContext.application())) {
            file = getSubDirectory(IMAGE_CACHE);
        } else {
            file = new File(getInternalCacheDir(), IMAGE_CACHE);
        }
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public static File getVideoCacheDirectory() {
        File file;
        if (hasExternalStoragePermission(ApplicationContext.application())) {
            file = getSubDirectory(VIDEO_CACHE);
        } else {
            file = new File(getInternalCacheDir(), VIDEO_CACHE);
        }
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public static File getVoiceCacheDirectory() {
        File file;
        if (hasExternalStoragePermission(ApplicationContext.application())) {
            file = getSubDirectory(VOICE_CACHE);
        } else {
            file = new File(getInternalCacheDir(), VOICE_CACHE);
        }
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    /**
     * * 获取外设根路径
     *
     * @param context 参考资料  https://www.mobibrw.com/2016/5035
     *                http://www.voidcn.com/blog/Chark_Leo/article/p-4955984.html
     * @return
     */
    public static String getCacheDir(Context context) {
        String cachFileRootDir = "";
        try {
            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            String[] volumePaths = new String[0];
            try {
                volumePaths = (String[]) sm.getClass().getMethod("getVolumePaths", context.getClass()).invoke(sm, context.getClass());
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if (volumePaths != null && volumePaths.length > 0) {
                for (String sdcardPath : volumePaths) {
                    File file = new File(sdcardPath + "/app.exe");
                    if (file.exists() && file.isDirectory()) {
                        cachFileRootDir = sdcardPath;
                        break;
                    }
                }
//                if(StringUtil.isBlank(cachFileRootDir)){
//                    for(String sdcardPath: volumePaths){
//                        File file = new File(sdcardPath+"/app.exe");
//                        try{
//                            file.mkdirs();
//                        }catch(Exception e){
//                            e.printStackTrace();
//                        }
//                        file = new File(sdcardPath+"/app.exe");
//                        if(file.exists() && file.isDirectory()){
//                            cachFileRootDir = sdcardPath+"/app.exe";
//                            break;
//                        }
//                    }
//                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return cachFileRootDir;
    }

    public static File copyFile(String src, String desPath) {
        if (TextUtils.isEmpty(src) || TextUtils.isEmpty(desPath)) {
            return null;
        }
        File outFile = null;
        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            File temp = new File(src);
            outFile = new File(desPath + "/" + (temp.getName()).toString());
            input = new FileInputStream(temp);

            output = new FileOutputStream(outFile);
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = input.read(b)) != -1) {
                output.write(b, 0, len);
            }
            output.flush();
        } catch (Exception e) {
            KLog.i("复制整个文件夹内容操作出错");
            e.printStackTrace();

        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return outFile;
        }
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    copyFile(temp.getAbsolutePath(), newPath);
                } else if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            KLog.i("复制整个文件夹内容操作出错");
            e.printStackTrace();
        }
    }

    public static void copyFile(File src, String target) {
        try {
            FileInputStream input = new FileInputStream(src);
            FileOutputStream output = new FileOutputStream(target);
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = input.read(b)) != -1) {
                output.write(b, 0, len);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Write string content to a file
     * @param data the string content
     * @param dest the file that will be saved
     */
    public static void writeString2File(String data, File dest) {
        if (TextUtils.isEmpty(data) || dest == null) return;

        if (dest.isDirectory()) {
            return;
        }

        dest.delete();
        PrintWriter out = null;

        try {
            out = new PrintWriter(new BufferedWriter(
                    new FileWriter(dest, false)));
            out.println(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String readStringFromFile(File file){
        if (file == null || file.isDirectory()) {
            return "";
        }

        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try{
            in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null){
                content.append(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (reader !=null){
                try{
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    /**
     * Save the bitmap to file.
     *
     * @param bitmap the bitmap
     * @return the path of the saved image.
     */
    public static File saveBitmap2File(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        File cacheDirPath = getExternalCacheDir();
        File file = new File(cacheDirPath, System.currentTimeMillis() + ".jpg");
        try {
            if (file.exists() && file.length() > 0) {
                file.delete();
            }

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    /**
     * Delete a file directory
     *
     * @param file the directory
     */
    public static void deleteDir(File file) {
        if (file == null) {
            return;
        }

        clearDir(file);
        file.delete();
    }

    /**
     * Delete a file directory
     *
     * @param file the directory
     */
    public static void clearDir(File file) {
        if (file == null) {
            return;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    deleteDir(f);
                }
            }
        }
    }

    public static long getFolderSize(File folder) {
        if (folder == null || !folder.exists()) {
            return 0;
        }

        if (folder.isFile()) {
            return folder.length();
        }

        long cacheSize = 0;
        File[] cachedFiles = folder.listFiles();
        if (cachedFiles != null) {
            for (File f : cachedFiles) {
                if (f.exists() && f.isFile()) {
                    cacheSize += f.length();
                }
            }
        }

        return cacheSize;
    }

    /*public static void getCacheSizeAsync() {
        return Observable.just(0).map(new Func1<Integer, String>() {
            @Override
            public String invoke(Integer integer) {
                return getCacheFileSize();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Boolean> clearCacheAsync() {
        return Observable.just(0).map(new Func1<Integer, Boolean>() {
            @Override
            public Boolean invoke(Integer integer) {
                clearCache();
                return true;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }*/

    /**
     * Clear cache files.
     */
    public static void clearCache() {
        clearDir(getImageCacheDirectory());
        clearDir(getVideoCacheDirectory());
        clearDir(getExternalLogDir());
        clearDir(getInternalCacheDir());
        clearDir(getExternalCacheDir());
        clearDir(getTmpDir());
        //clearDir(ApplicationContext.getInstance().getFilesDir());
        ApplicationContext.application().deleteDatabase("webview.db");
        ApplicationContext.application().deleteDatabase("webviewCache.db");
    }

    /**
     * Get size of the cache.
     */
    public static String getCacheFileSize() {
        long cacheSize = getImageCacheSize();
        cacheSize += getLogFileCacheSize();
        cacheSize += getSystemCacheSize();
        cacheSize += getFolderSize(getExternalCacheDir());
        cacheSize += getFolderSize(getExternalCacheDir());
        cacheSize += getTmpCacheSize();
        return calcCacheSizeString(cacheSize);
    }


    private static long getImageCacheSize() {
        long size;
        File cachedFileDir = new File(getInternalCacheDir(), IMAGE_CACHE) ;
        size = getFolderSize(cachedFileDir);
        if (hasExternalStoragePermission(ApplicationContext.application())) {
            cachedFileDir = getImageCacheDirectory();
            size += getFolderSize(cachedFileDir);
        }
        return size;
    }

    private static long getLogFileCacheSize() {
        File cachedFileDir = getExternalLogDir();
        return getFolderSize(cachedFileDir);
    }

    private static long getSystemCacheSize() {
        File cacheDir = getInternalCacheDir();
        long size = getFolderSize(cacheDir);
        /*cacheDir = ApplicationContext.getInstance().getFilesDir();
        size += getFolderSize(cacheDir);*/
        return size;
    }

    private static long getTmpCacheSize() {
        File file = getSubDirectory("tmp");
        return getFolderSize(file);
    }

    /**
     * The file's size unit converter
     *
     * @param size the size of file
     */
    public static String calcCacheSizeString(long size) {
        DecimalFormat df = new DecimalFormat("###.##");
        float f = ((float) size / (float) (1048576)); // 1024 * 1024

        if (f < 1.0) {
            float f2 = ((float) size / (float) (1024));

            return df.format(new Float(f2).doubleValue()) + "KB";

        } else {
            return df.format(new Float(f).doubleValue()) + "M";
        }

    }



    public static void deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }

        deleteFile(new File(filePath));

    }

    public static void deleteFile(File file) {
        if (null == file) {
            return;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null || files.length == 0) {
                file.delete();
                return;
            }

            // Recursive delete the file if it's a directory.
            for (File f : files) {
                deleteFile(f);
            }
        } else {
            file.delete();
        }
    }


}
