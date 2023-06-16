package com.psm.mytable.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.psm.mytable.R


object BindingConversions {




    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(imageView : ImageView, url : String?){
        val context = imageView.context
        val defaultImg: Drawable? = ResourcesCompat.getDrawable(context.resources, R.mipmap.ic_launcher, null)
        if(!url.isNullOrEmpty()){
            Glide.with(imageView.context).load(url)
                .error(defaultImg)
                .into(imageView)
        }else{
            Glide.with(imageView.context).load(defaultImg)
                .error(defaultImg)
                .into(imageView)
        }
    }

    @BindingAdapter("circleImageUrl")
    @JvmStatic
    fun loadCircleImage(imageView : ImageView, url : String?){
        if(!url.isNullOrEmpty()){
            val context = imageView.context
            val defaultImg: Drawable? = ResourcesCompat.getDrawable(context.resources, R.mipmap.ic_launcher, null)

            Glide.with(imageView.context)
                .load(url)
                .circleCrop()
                .error(defaultImg)
                .into(imageView);

        }
    }

    @BindingAdapter("roundRectImageUrl")
    @JvmStatic
    fun loadRoundRectImage(imageView : ImageView, url : String?){
        if(!url.isNullOrEmpty()){
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))

            val context = imageView.context
            val defaultImg: Drawable? = ResourcesCompat.getDrawable(context.resources, R.mipmap.ic_launcher, null)

            Glide.with(imageView.context)
                .load(url)
                .apply(requestOptions)
                .error(defaultImg)
                .into(imageView);
        }
    }

    //tagItems
}
