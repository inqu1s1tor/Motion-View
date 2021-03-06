package com.erminesoft.motionview.motionview.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.PlusShare;

import java.io.ByteArrayOutputStream;

public class Utils {
    public static void sharePhotoToFacebook(Bitmap bitmap, final Context context, String textContent) {
        StringBuilder string = new StringBuilder();
        if(!TextUtils.isEmpty(textContent)){
            string.append(textContent);
        } else {
            string.append("Look at my fitness track!");
        }
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setCaption(string.toString())
                .build();

        final SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(context, "Successfully Shared to Facebook", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancel() {
                Toast.makeText(context, "Sharing Cancelled", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException error) {
                Toast.makeText(context, "Sharing Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Intent shareToGooglePlus(Bitmap bitmap, final Context context, String textContent) {
        Uri uri;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ContentResolver contentResolver = context.getContentResolver();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null);
        uri = Uri.parse(path);
        String mime = contentResolver.getType(uri);

        PlusShare.Builder shareBuild = new PlusShare.Builder(context);
        shareBuild.setText(textContent);
        shareBuild.addStream(uri);
        shareBuild.setType(mime);
        Intent shareIntent = shareBuild.getIntent();

        return shareIntent;
    }

    public static float calculateDistanceBetweenPoints(LatLng p1, LatLng p2) {
        float[] distance = new float[1];

        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, distance);

        return distance[0] / 1000;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
