package demo.android.com.instagram_clone.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import demo.android.com.instagram_clone.R;

/**
 * Created by Admin on 28-05-2018.
 */

public class UniversalImageLoader {

    private static int defaultImage = R.drawable.ic_instagram_logo;
    private Context mContext;

    public UniversalImageLoader(Context mContext) {
        this.mContext = mContext;
    }


    public ImageLoaderConfiguration getImageLoaderConfig() {

        //To display default image, in case of some problems in accessing the originally intended image
        DisplayImageOptions defafultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .cacheOnDisk(true).cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                //to make sure that our photo is of its original orientation when displayed by universal image loader
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defafultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024)
                .build();

        return  imageLoaderConfiguration;
    }

    /**
     * This method can be used to set images that are static. It can't be used if the
     * images are being changed by fragments/activities, or if images are being set in list or grids
     * @param imageURL
     * @param imageView
     * @param mProgressBar
     * @param append
     */
    public static void setImage(String imageURL, ImageView imageView, final ProgressBar mProgressBar, String append) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imageURL, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(mProgressBar != null) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
