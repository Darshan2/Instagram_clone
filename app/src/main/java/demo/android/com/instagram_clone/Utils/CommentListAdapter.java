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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.model.Comment;
import demo.android.com.instagram_clone.model.User;
import demo.android.com.instagram_clone.model.UserAccountSettings;

/**
 * Created by Darshan B.S on 12-06-2018.
 */

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {

    private static final String TAG = "CommentListAdapter";

    //vars
    private Context mContext;
    private ArrayList<Comment> commentsArrayList;

    public CommentListAdapter(Context context, ArrayList<Comment> commentsArrayList) {
        mContext = context;
        this.commentsArrayList = commentsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.comments_individual_list_iem, null);

        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return commentsArrayList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentsArrayList.get(position);
        if(position == 0) {
            //This item represents Actual Image post, on which all users commented on
            holder.mCommentReplay.setVisibility(View.GONE);
            holder.mCommentLikes.setVisibility(View.GONE);
            holder.mHeartContainer.setVisibility(View.GONE);
        } else {
           // holder.mCommentLikes.setText(comment.getLikes().size());
        }

        setUserName(holder, comment.getUser_id());

        String timeDiff = MyDateHandler.getTimeDiffernce(comment.getDate_created());
        String timeDiffString = "";
        if(timeDiff.equals("0")) {
            timeDiffString = "TODAY";
        } else if (timeDiff.equals("1")) {
            timeDiffString = "YESTERDAY";
        } else {
            timeDiffString = timeDiff + "d";
        }
        holder.mCommentPostedTime.setText(timeDiffString);

        holder.mCommentUserComment.setText(comment.getComment());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mComment_ProfilePhoto;
        TextView mCommentUserName, mCommentUserComment, mCommentPostedTime, mCommentLikes, mCommentReplay;
        RelativeLayout mHeartContainer;
        ImageView mWhiteHeart, mRedHeart;

        public ViewHolder(View itemView) {
            super(itemView);

            mComment_ProfilePhoto = itemView.findViewById(R.id.comment_profile_photo);
            mCommentUserName = itemView.findViewById(R.id.comment_user_name);
            mCommentUserComment = itemView.findViewById(R.id.comment_user_comment);
            mCommentPostedTime = itemView.findViewById(R.id.comment_posted_time);
            mCommentLikes = itemView.findViewById(R.id.comment_likes);
            mCommentReplay = itemView.findViewById(R.id.comment_replay);
            mHeartContainer = itemView.findViewById(R.id.likeHeartsContainer);
            mWhiteHeart = itemView.findViewById(R.id.likeHeart_white);
            mRedHeart = itemView.findViewById(R.id.likeHeart_red);
        }
    }


    private void setUserName(final ViewHolder holder, String user_id) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        Query query = myRef.child(mContext.getString(R.string.user_account_settings_node))
                .child(user_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: received from DB" + dataSnapshot.getValue());
                UserAccountSettings userAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                holder.mCommentUserName.setText(userAccountSettings.getUser_name());
                UniversalImageLoader.setImage(
                        userAccountSettings.getProfile_photo(), holder.mComment_ProfilePhoto,
                        null, "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}

