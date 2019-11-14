package com.example.szl.ServiceAPI;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.szl.RequiestTest.FileUpload;
import com.example.szl.RequiestTest.RetrofitGet;
import com.example.szl.ResponseModel.File;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Retrofit;

public class FileFunc {




    public static boolean isExternalStorageDocument(Uri uri)
    {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadDocument(Uri uri)
    {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri)
    {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context,Uri uri,String selection,String[] selectionArgs)
    {
        Cursor cursor = null;
        final String colum="_data";
        final String[] projection = {colum};
        try {
            cursor=context.getContentResolver().query(uri,projection,selection,selectionArgs,null);
            if(cursor!=null&&cursor.moveToFirst())
            {
                final int colum_index=cursor.getColumnIndexOrThrow(colum);
                return cursor.getString(colum_index);
            }
        }finally {
            if(cursor!=null)
                cursor.close();
        }
        return null;
    }

    //获取文件路径
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public  static String getPath(final Context context, Uri uri)
    {
       try {


           if (isExternalStorageDocument(uri)) {
               final String docID = DocumentsContract.getDocumentId(uri);
               final String[] split = docID.split(":");
               final String type = split[0];
               if ("primary".equalsIgnoreCase(type)) {
                   return Environment.getExternalStorageDirectory() + "/" + split[1];
               }
           } else if (isDownloadDocument(uri)) {
               final String id = DocumentsContract.getDocumentId(uri);
               final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
               return getDataColumn(context, contentUri, null, null);

           } else if (isMediaDocument(uri)) {
               final String docID = DocumentsContract.getDocumentId(uri);
               final String[] split = docID.split(":");
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
           } else if ("content".equalsIgnoreCase(uri.getScheme())) {
               return getDataColumn(context, uri, null, null);
           } else if ("file".equalsIgnoreCase(uri.getScheme())) {
               return uri.getPath();
           } else
               return null;
       }catch (Exception e)
       {
           Toast.makeText(context,"不能选择该文件",Toast.LENGTH_SHORT).show();
       }


        return null;
    }

    public static void fileUpload(int userID,String fileName,Context context)
    {
        Retrofit retrofit= RetrofitGet.getRetrofit();
        FileUpload fileUpload=retrofit.create(FileUpload)
    }

}
