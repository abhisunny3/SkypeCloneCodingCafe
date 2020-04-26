package com.example.skypeclonecodingcafe;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class My {

    public static void setImage(CustomProgressView progress_bar_load,RelativeLayout relativeLayout, String image_url, ImageView profileImageView) {



        progress_bar_load.setVisibility(View.VISIBLE);
        //progress_bar_load.setColor(R.color.white);

        Picasso.get()
                .load(image_url)
                .fit()
                //.centerCrop()
                .placeholder(R.drawable.profile_image)
                .into(profileImageView,new Callback() {
                    @Override
                    public void onSuccess() {
                        progress_bar_load.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        //holder.PostImage.setBackgroundResource(R.color.gray1);
                        progress_bar_load.setVisibility(View.GONE);
                    }
                });
    }

    public static void setImage(CustomProgressView progress_bar_load, String image_url, ImageView profileImageView) {



        progress_bar_load.setVisibility(View.VISIBLE);
        //progress_bar_load.setColor(R.color.white);

        Picasso.get()
                .load(image_url)
                .fit()
                //.centerCrop()
                .placeholder(R.drawable.profile_image)
                .into(profileImageView,new Callback() {
                    @Override
                    public void onSuccess() {
                        progress_bar_load.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        //holder.PostImage.setBackgroundResource(R.color.gray1);
                        progress_bar_load.setVisibility(View.GONE);
                    }
                });
    }
}
