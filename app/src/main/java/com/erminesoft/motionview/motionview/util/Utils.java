package com.erminesoft.motionview.motionview.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Utils {
    public void sharePhotoToFacebook(Bitmap bitmap, final Context context,String textContent) {
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
                Toast.makeText(context, "Successfully Shared", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancel() {
                Toast.makeText(context, "Sharing Cancelled", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException error) {
                Log.d("!!!!!!", "" + error.toString());
                Toast.makeText(context, "Sharing Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public float calculateDistanceBetweenPoints(List<LatLng> points){
        Location startLocation = new Location("");
        startLocation.setLatitude(points.get(0).latitude);
        startLocation.setLongitude(points.get(0).longitude);

        Location endPosition = new Location("");
        endPosition.setLatitude(points.get(points.size() - 1).latitude);
        endPosition.setLongitude(points.get(points.size() - 1).longitude);

        Log.d("!!!!!!", "" + startLocation.getLatitude() + " " + startLocation.getLongitude());
        Log.d("!!!!!!", "" + endPosition.getLatitude() + " " + endPosition.getLongitude());

        float distance = startLocation.distanceTo(endPosition)/1000;
        return distance;
    }
}
