package com.example.user.your_breakfast.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.example.user.your_breakfast.FoodListActivity;
import com.facebook.share.Share;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ShareToFacebook {
    String imgURL;
    Activity parentContext;
    Target target;
    ShareDialog shareDialog;
    public ShareToFacebook(String imgURL, Activity parentContext){
        this.imgURL = imgURL;
        this.parentContext = parentContext;
        this.shareDialog = new ShareDialog(parentContext);

        target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap)
                        .build();
                if (ShareDialog.canShow(SharePhotoContent.class)) {
                    SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo)
                            .build();
                    shareDialog.show(content);
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }


    public void share(){
        Picasso.get().load(imgURL).into(target);
    }
}
