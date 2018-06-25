package demo.android.com.instagram_clone.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Share.GalleryFragment;


public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.GridImageViewHolder>{



    private Context mContext;
    //imageURIs must be of appropriate form for ex
    // web images should be of form https://image_url
    // Images from sd card should be of form file://file_path (@refer UniversalImageLoader docs)
    private ArrayList<String> imageURI_ArrayList;
    private OnRecyclerItemClickListener onRecyclerItemClickListener;


    /**
     * Interface to send information of clicked Recycler items back to(if needed) calling Activity/fragment
     */
    public interface OnRecyclerItemClickListener {
        void recyclerViewClickedItemURL(String imageURL, int index);
    }


    public GridImageAdapter(
            Context context, ArrayList<String> imageURI_ArrayList, @Nullable OnRecyclerItemClickListener recyclerItemClickListener) {
        mContext = context;
        this.imageURI_ArrayList = imageURI_ArrayList;
        if(recyclerItemClickListener != null) {
            onRecyclerItemClickListener = recyclerItemClickListener;
        }
    }


    @NonNull
    @Override
    public GridImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.grid_individual_item, null);

        return new GridImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GridImageViewHolder holder, final int position) {
        String imageURL = imageURI_ArrayList.get(position);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imageURL, holder.gridItemImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(holder.gridItemProgressBar != null) {
                    holder.gridItemProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(holder.gridItemProgressBar != null) {
                    holder.gridItemProgressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(holder.gridItemProgressBar != null) {
                    holder.gridItemProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(holder.gridItemProgressBar != null) {
                    holder.gridItemProgressBar.setVisibility(View.GONE);
                }
            }
        });


        //set on click listener
        holder.gridItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecyclerItemClickListener.recyclerViewClickedItemURL(imageURI_ArrayList.get(position), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageURI_ArrayList.size();
    }


    class GridImageViewHolder extends RecyclerView.ViewHolder {
        ImageView gridItemImage;
        ProgressBar gridItemProgressBar;

        public GridImageViewHolder (View itemView) {
            super(itemView);
            gridItemImage = itemView.findViewById(R.id.gridItemImage);
            gridItemProgressBar = itemView.findViewById(R.id.gridItemProgrssBar);
        }
    }

}
