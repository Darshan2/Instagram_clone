package demo.android.com.instagram_clone.Utils;

import android.app.DownloadManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.model.LikedUser;
import demo.android.com.instagram_clone.model.Photo;
import demo.android.com.instagram_clone.model.User;
import demo.android.com.instagram_clone.model.UserAccountSettings;

/**
 * Created by Darshan B.S on 16-06-2018.
 */

public class MainFeedListAdapter extends RecyclerView.Adapter<MainFeedListAdapter.ViewHolder> {

    private static final String TAG = "MainFeedListAdapter";

    //FireBase Auth, DataBase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DataBaseHandler dataBaseHandler;
    private String currentUserId;

    private Context mContext;
    private ArrayList<Photo> mMainFeedItemsList;
    private String mLikeString = "";
    private OnMainFeedListItemClickListener onMainFeedListItemClickListener;


    public interface OnMainFeedListItemClickListener {
        void onMainFeedItemCommentBubbleClick(Photo photo);
        void onMainFeedItemMenuClick(String photoUploadedUserID);
        void onLoadMoreItems(int currentItemCount);
    }


    public MainFeedListAdapter(Context context, ArrayList<Photo> mainFeedItemsList,
                               OnMainFeedListItemClickListener onMainFeedListItemClickListener) {
        mContext = context;
        mMainFeedItemsList = mainFeedItemsList;
        this.onMainFeedListItemClickListener = onMainFeedListItemClickListener;

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference();
        dataBaseHandler = new DataBaseHandler(mContext, null);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.mainfeed_item, null);

        return new ViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return mMainFeedItemsList.size();
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mProgressBar.setVisibility(View.GONE);


        final Photo receivedPhoto = mMainFeedItemsList.get(position);

        Log.d(TAG, "onBindViewHolder: mMainFeedItemsList.size() " + mMainFeedItemsList.size());

        if(position >= mMainFeedItemsList.size() - 1) {
            Log.d(TAG, "onBindViewHolder: reached end of list");
            //reached end of list add more items
            onMainFeedListItemClickListener.onLoadMoreItems(position + 1);
        }

        if(receivedPhoto != null) {
            holder.mProgressBar.setVisibility(View.VISIBLE);
            currentUserLikesPhoto(holder, receivedPhoto);
        }

        getAllPhotoLikedUsers(holder, receivedPhoto);

        //Heart on-click handler
        setupHeartToggle(holder.mRedHeart, holder.mWhiteHeart, holder, receivedPhoto);

        //Comment bubble and CommentInfo OnClick handlers
        holder.mCommentBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Comment onClick: Navigating to ViewCommentFragment");
                onMainFeedListItemClickListener.onMainFeedItemCommentBubbleClick(receivedPhoto);
            }
        });

        holder.mImageComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //same response as CommentBubble click
                Log.d(TAG, "Comment onClick: Navigating to ViewCommentFragment");
                onMainFeedListItemClickListener.onMainFeedItemCommentBubbleClick(receivedPhoto);
            }
        });

        //Profile menu on-click handler
        holder.mProfileMenus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Menu onClick: Navigating to ProfileFragment id " +receivedPhoto.getUser_id());
                onMainFeedListItemClickListener.onMainFeedItemMenuClick(receivedPhoto.getUser_id());
            }
        });


        setProfilePhotoAndUserName(holder, receivedPhoto);

        UniversalImageLoader.setImage(receivedPhoto.getImage_path(),
                holder.mPostedImage, null, "");

        String timeDiff = MyDateHandler.getTimeDiffernce(receivedPhoto.getDate_created());
        if(timeDiff.equals("0")) {
            holder.mImagePostedDate.setText("Posted: TODAY");
        } else if(timeDiff.equals("1")) {
            holder.mImagePostedDate.setText("Posted: YESTERDAY");
        } else {
           holder.mImagePostedDate.setText("Posted: " + timeDiff + " DAYS AGO");
        }

         holder.mImageCaption.setText(receivedPhoto.getCaption());

        String commentString = "";
        if(receivedPhoto.getComments() == null) {
            commentString = "No comments";
        } else if (receivedPhoto.getComments().size() == 1) {
            commentString = "View one comment";
        } else {
            commentString = "View all " +receivedPhoto.getComments().size() + " comments";
        }
        holder.mImageComments.setText(commentString);

    }



    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mProfileMenus;
        ImageView mPostedImage, mCommentBubble;
        ImageView mWhiteHeart, mRedHeart;
        TextView mUserName;
        TextView mImageLikes, mImageComments, mImageCaption, mImagePostedDate;
        CircleImageView mProfilePhoto;
        ProgressBar mProgressBar;


        public ViewHolder(View itemView) {
            super(itemView);

            mProfilePhoto = itemView.findViewById(R.id.profile_photo);
            mUserName = itemView.findViewById(R.id.user_name);
            mProfileMenus = itemView.findViewById(R.id.profileMenu);
            mPostedImage = itemView.findViewById(R.id.postImage);
            mWhiteHeart = itemView.findViewById(R.id.likeHeart_white);
            mRedHeart = itemView.findViewById(R.id.likeHeart_red);
            mCommentBubble = itemView.findViewById(R.id.commentBubble);
            mImageLikes = itemView.findViewById(R.id.image_likes);
            mImageCaption = itemView.findViewById(R.id.image_caption);
            mImageComments = itemView.findViewById(R.id.image_comments_link);
            mImagePostedDate = itemView.findViewById(R.id.image_postedTime);
            mProgressBar = itemView.findViewById(R.id.progressBar);
        }
    }


    //---------------------------------------------------------------------------------------------------//
        private void setProfilePhotoAndUserName(final ViewHolder holder, Photo photo) {
            String photoUploadedUserId = photo.getUser_id();
            //get uploaded user info
            Query query = myRef.child(mContext.getString(R.string.user_account_settings_node))
                    .child(photoUploadedUserId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UserAccountSettings userAccountSettings = dataSnapshot.getValue(UserAccountSettings.class);
                    holder.mUserName.setText(userAccountSettings.getUser_name());
                    UniversalImageLoader.setImage(userAccountSettings.getProfile_photo(),
                            holder.mProfilePhoto, null, "");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: 1");
                }
            });
        }


    private void currentUserLikesPhoto(final ViewHolder holder, Photo receivedPhoto) {
        Query queryLikes = myRef.child(mContext.getString(R.string.photos_node))
                .child(receivedPhoto.getPhoto_id())
                .child(mContext.getString(R.string.likes_field))
                .orderByChild(mContext.getString(R.string.user_id_field))
                .equalTo(currentUserId);
        queryLikes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.mProgressBar.setVisibility(View.GONE);
                //if dataSnapshot value exists, user liked selected photo
                if(dataSnapshot.getValue() != null) {
                   // Log.d(TAG, "onDataChange: user liked this photo" + dataSnapshot);
                    holder.mRedHeart.setVisibility(View.VISIBLE);
                    holder.mWhiteHeart.setVisibility(View.GONE);
                } else {
                   // Log.d(TAG, "onDataChange: user not liked this photo" + dataSnapshot);
                    holder.mRedHeart.setVisibility(View.GONE);
                    holder.mWhiteHeart.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: 2");
            }
        });
    }


    private void setupHeartToggle(final ImageView redHeart, final ImageView whiteHeart,
                                  final ViewHolder holder, final Photo receivedPhoto) {

        whiteHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Heart(whiteHeart, redHeart ).toggleLike();
                holder.mProgressBar.setVisibility(View.VISIBLE);
                String newNodeKey = myRef.child(mContext.getString(R.string.photos_node)).push().getKey();
                LikedUser likedUser = new LikedUser(currentUserId);

                //user likes this photo, update photos like field
                myRef.child(mContext.getString(R.string.photos_node))
                        .child(receivedPhoto.getPhoto_id())
                        .child(mContext.getString(R.string.likes_field))
                        .child(newNodeKey)
                        .setValue(likedUser);

                //user likes this photo, update user_photos like field
                myRef.child(mContext.getString(R.string.user_photos_node))
                        .child(receivedPhoto.getUser_id())
                        .child(receivedPhoto.getPhoto_id())
                        .child(mContext.getString(R.string.likes_field))
                        .child(newNodeKey)
                        .setValue(likedUser);

                Log.d(TAG, "onClick: whiteheart toggle getAllPhotoLikedUsers();" );
                getAllPhotoLikedUsers(holder, receivedPhoto);
            }
        });

        redHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Heart(whiteHeart, redHeart).toggleLike();
                Log.d(TAG, "setupHeartToggle: " + receivedPhoto.getTags());
                //user unlikes this photo. Remove current user_id, from photos likes field.
                //I cannot remove this user_id from user_photos like field, because of different
                //generated list keys
                Query query = myRef.child(mContext.getString(R.string.photos_node))
                        .child(receivedPhoto.getPhoto_id())
                        .child(mContext.getString(R.string.likes_field))
                        .orderByChild(mContext.getString(R.string.user_id_field))
                        .equalTo(currentUserId);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Log.d(TAG, "onDataChange: un-liked, remove user_id from list " + dataSnapshot);
                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                            String listKey = ds.getKey();
                            //Remove entry with this key under photos likes field, to unlike the photo
                            myRef.child(mContext.getString(R.string.photos_node))
                                    .child(receivedPhoto.getPhoto_id())
                                    .child(mContext.getString(R.string.likes_field))
                                    .child(listKey)
                                    .removeValue();

                            //Remove entry with this key under user_photos likes field, to unlike the photo
                            myRef.child(mContext.getString(R.string.user_photos_node))
                                    .child(receivedPhoto.getUser_id())
                                    .child(receivedPhoto.getPhoto_id())
                                    .child(mContext.getString(R.string.likes_field))
                                    .child(listKey)
                                    .removeValue();

                           getAllPhotoLikedUsers(holder, receivedPhoto);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: 3");
                    }
                });
            }
        });

    }


    private void getAllPhotoLikedUsers(final ViewHolder holder, Photo receivedPhoto) {
        Query query = myRef.child(mContext.getString(R.string.photos_node))
                .child(receivedPhoto.getPhoto_id())
                .child(mContext.getString(R.string.likes_field));
        //query to get user_ids of all liked users
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    //Log.d(TAG, "onDataChange: no user liked this photo");
                    mLikeString = "No likes yet!";
                    holder.mImageLikes.setText(mLikeString);
                    //setupFragmentWidgets();
                }

                final StringBuilder mLikedUsers = new StringBuilder();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //Log.d(TAG, "onDataChange: all liked users ids ds " + ds);
                    final LikedUser likedUser = ds.getValue(LikedUser.class);

                    //here we have user_id of individual photo liked user,
                    //query 'users' to get user_name of each liked users, using user_id
                    Query query = myRef.child(mContext.getString(R.string.users_node))
                            .child(likedUser.getUser_id());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            holder.mProgressBar.setVisibility(View.GONE);
                            User user = dataSnapshot.getValue(User.class);
                            mLikedUsers.append(user.getUser_name() + ",");

                            String[] splitUsers = mLikedUsers.toString().split(",");
                            int length = splitUsers.length;
                            if (length == 1) {
                                mLikeString = "Liked by " + splitUsers[0];
                            } else if (length == 2) {
                                mLikeString = "Liked by " + splitUsers[0] + " and " + splitUsers[1];
                            } else if (length == 3) {
                                mLikeString = "Liked by " + splitUsers[0] + ", "
                                        + splitUsers[1] + " and "
                                        + splitUsers[2];
                            } else if (length > 3) {
                                mLikeString = "Liked by " + splitUsers[0] + ", "
                                        + splitUsers[1] + ", "
                                        + splitUsers[2] + " and "
                                        + (length - 3) + " others";
                            }

                            holder.mImageLikes.setText(mLikeString);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, "onCancelled: 4");
                        }

                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: 5");
            }
        });
    }

}
