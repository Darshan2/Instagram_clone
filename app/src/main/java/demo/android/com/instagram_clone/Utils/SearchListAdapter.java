package demo.android.com.instagram_clone.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.model.Comment;
import demo.android.com.instagram_clone.model.UserAccountSettings;
import demo.android.com.instagram_clone.model.UserMatched;

/**
 * Created by Darshan B.S on 12-06-2018.
 */

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

    private static final String TAG = "SearchListAdapter";

    //vars
    private Context mContext;
    private ArrayList<UserMatched> userMatchedArrayList;
    private OnSearchItemClickListener onSearchItemClickListener;

    public SearchListAdapter(Context context, ArrayList<UserMatched> userMatchedArrayList,
                             OnSearchItemClickListener listener) {
        mContext = context;
        this.userMatchedArrayList = userMatchedArrayList;
        onSearchItemClickListener = listener;
    }


    public interface OnSearchItemClickListener {
        //pass info of clicked search result, into calling Activity
        void onSearchItemClick(UserMatched userMatched);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.search_list_item, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final UserMatched userMatched = userMatchedArrayList.get(position);

        UniversalImageLoader.setImage(userMatched.getProfile_photo(), holder.mProfilePhoto, null, "");
        holder.mUserName.setText(userMatched.getUser_name());
        holder.mEmail.setText(userMatched.getEmail());

        //On-click listener
        if(onSearchItemClickListener != null) {
            holder.mItemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSearchItemClickListener.onSearchItemClick(userMatched);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userMatchedArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mProfilePhoto;
        TextView mUserName;
        TextView mEmail;
        RelativeLayout mItemContainer;

        public ViewHolder(View itemView) {
            super(itemView);

            mProfilePhoto = itemView.findViewById(R.id.profile_photo);
            mUserName = itemView.findViewById(R.id.user_name);
            mEmail = itemView.findViewById(R.id.email);
            mItemContainer = itemView.findViewById(R.id.itemContainer);

        }
    }




}

