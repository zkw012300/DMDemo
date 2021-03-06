package com.zspirytus.dmdemo.JavaSource.Utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;

import com.zspirytus.dmdemo.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ZSpirytus on 2017/10/15.
 */

public class PhotoUtil {

    public static final File picName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dmdemo/" + "temp.jpg");
    public static final File cropPicName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dmdemo/crop.jpg");
    public static final File compressFileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/dmdemo/compress.jpg");

    public static final int AVATAR_QUALITY = 50;
    public static final int REPAIRPHOTO_QUALITY = 30;
    public static final int UPLOAD_MAXSIZE = 1024*100;

    private static final String TAG = "PhotoUtil";
    private static final int REQ_CAMERA = 0x01;
    private static final int REQ_ALBUM = 0x02;
    private static final int REQ_CUT = 0x04;
    private static final int REQ_PERMISSION_FOR_CAMERA = 0x10;
    private static final int REQ_PERMISSION_FOR_ALBUM = 0x20;

    /**
     * 申请权限并打开相机
     *
     * @param activity 调用该方法的活动
     * @return 权限已赋予，返回照片Uri；权限未赋予，返回null
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static Uri applyPermissionForCamera(final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            if (ContextCompat.checkSelfPermission(activity, permissions[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(activity, permissions[1]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(activity, permissions[2]) == PackageManager.PERMISSION_GRANTED
                    ) {
                Uri uri = useCamera(activity);
                return uri;
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle(activity.getString(R.string.Need_Permission));
                dialog.setMessage(activity.getString(R.string.The_Application_Need_the_Permission));
                dialog.setCancelable(false);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] permissions = {
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        };
                        activity.requestPermissions(permissions, REQ_PERMISSION_FOR_CAMERA);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        } else {
            return useCamera(activity);
        }
        return null;
    }

    /**
     * 申请权限并打开相册
     *
     * @param activity 调用该方法的活动
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void applyPermissionForAlbum(final Activity activity) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            if (ContextCompat.checkSelfPermission(activity, permissions[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(activity, permissions[1]) == PackageManager.PERMISSION_GRANTED
                    ) {
                selectFromAlbum(activity);
            } else {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setTitle(activity.getString(R.string.Need_Permission));
                dialog.setMessage(activity.getString(R.string.The_Application_Need_the_Permission));
                dialog.setCancelable(false);
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String[] permissions = {
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        };
                        activity.requestPermissions(permissions, REQ_PERMISSION_FOR_ALBUM);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog.show();
            }
        } else {
            selectFromAlbum(activity);
        }
    }

    /**
     * 打开相机拍照拍照
     *
     * @param activity 调用该方法的活动
     * @return 返回拍照所得的照片的Uri
     */
    public static Uri useCamera(final Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        picName.getParentFile().mkdirs();
        Uri picUri = null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            picUri = FileProvider.getUriForFile(activity, "com.zspirytus.dmdemo.Activity.MainActivity.fileprovider", picName);
        } else {
            picUri = Uri.fromFile(picName);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        activity.startActivityForResult(intent, REQ_CAMERA);
        return picUri;
    }

    /**
     * 打开相册选择图片
     *
     * @param activity 调用该方法的活动
     */
    public static void selectFromAlbum(final Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, REQ_ALBUM);
    }

    /**
     * 截取图片
     *
     * @param activity 调用该方法的活动
     * @param picUri   需要裁剪的图片的Uri
     * @return 裁剪所得照片的Uri
     */
    public static Uri cropPicture(final Activity activity, final Uri picUri) {
        if (!cropPicName.exists())
            cropPicName.getParentFile().mkdirs();
        Uri cropPicUri = Uri.fromFile(cropPicName);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(picUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropPicUri);
        intent.putExtra("outputFormat",
                Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, REQ_CUT);
        return cropPicUri;
    }

    /**
     * String to Bitmap
     *
     * @param str 转换成String的图片
     * @return 图片Bitmap
     */
    public static Bitmap getBitmapbyString(final String str) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(str, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * Bitmap to String
     *
     * @param bitmap 需要转String的图片
     * @return String型的图片
     */
    public static String convertIconToString(final Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }

    /**
     * String to Bitmap
     *
     * @param str String型图片
     * @return Bitmap型图片
     */
    public static Bitmap convertStringToIcon(final String str) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(str, Base64.DEFAULT);
            bitmap =
                    BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static File saveCompressFile(final File file, final int quality) {
        Bitmap old = BitmapFactory.decodeFile(file.getAbsolutePath());
        Bitmap bitmap = getThumbnails(file.getAbsolutePath());
        int degree = readPictureDegree(file.getAbsolutePath());
        if (degree != 0) {
            rotateBitmap(bitmap, degree);
        }
        FileOutputStream fos = null;
        try {
            if (!compressFileName.exists()) {
                compressFileName.getParentFile().mkdirs();
            } else {
                compressFileName.delete();
            }
            fos = new FileOutputStream(compressFileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            return compressFileName;
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Compress Failed!");
            e.printStackTrace();
            return null;
        }
    }

    public static String convertFileToString(final File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {

            }
        }
        return base64;
    }

    private static Bitmap getThumbnails(final String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, 854, 480);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 获取照片角度
     *
     * @param path
     * @return
     */
    private static int readPictureDegree(final String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转照片
     *
     * @param bitmap
     * @param degress
     * @return
     */
    private static Bitmap rotateBitmap(Bitmap bitmap, final int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * android 4.4 获取 uri 真实路径
     *
     * @param uri uri
     * @return /sdcard/0/xx.jpg
     */
    public static String getRealFilePath(Context context, Uri uri) {
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

}
