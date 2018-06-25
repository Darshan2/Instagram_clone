package demo.android.com.instagram_clone.Utils;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import demo.android.com.instagram_clone.Home.HomeActivity;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.model.Comment;
import demo.android.com.instagram_clone.model.LikedUser;
import demo.android.com.instagram_clone.model.Photo;

public class ViewCommentFragment extends Fragment {

    private static final String TAG = "ViewCommentFragment";

    //FireBase Auth, DataBase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DataBaseHandler dataBaseHandler;
    private String userId;

    //widgets
    private ImageView mBackArrow, mCheckMark;
    private EditText mCommentText;
    private RecyclerView mCommentsList;

    //vars
    private Photo receivedPhoto;
    private ArrayList<Comment> commentArrayList;
    private CommentListAdapter adapter;
    private int runCount = 0;
    private AppCompatActivity mCallingActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_comment, container, false);

        myRef = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        commentArrayList = new ArrayList<>();

        mBackArrow = view.findViewById(R.id.backArrow);
        mCheckMark = view.findViewById(R.id.checkMark);
        mCommentText = view.findViewById(R.id.comment_text);
        mCommentsList = view.findViewById(R.id.comments_list);

        setupFireBaseAuth();
        getReceivedArgumentValues();
        setupFragmentWidgets();
        getCommentsListFromDatabase();

        return view;
    }

    private void getReceivedArgumentValues() {
        try {
            Bundle argBundle = getArguments();
            receivedPhoto = argBundle.getParcelable(getString(R.string.photo));
            if(argBundle.containsKey(getString(R.string.calling_activity))) {
               if(argBundle.getString(getString(R.string.calling_activity)).equals(getString(R.string.home_activity))) {
                   mCallingActivity = (HomeActivity) getActivity();
               }
            }
        } catch (NullPointerException e) {
            Log.d(TAG, "getReceivedArgumentValues: "+ e.getMessage());
        }
    }


    private void setupFragmentWidgets() {
        Log.d(TAG, "setupFragmentWidgets: ");

        //This is always the FirstItem of comments list
        Comment firstComment = new Comment();
        firstComment.setUser_id(receivedPhoto.getUser_id());
        firstComment.setComment(receivedPhoto.getCaption());
        firstComment.setDate_created(receivedPhoto.getDate_created());
        commentArrayList.add(firstComment);
        
        mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: check mark clicked");
                String commentText = mCommentText.getText().toString();
                if(!commentText.equals("")) {
                    Log.d(TAG, "onClick: Attempting to add new comment");
                    //add new comment to database
                    addNewComment(commentText);
                }
            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallingActivity != null) {
                    Log.d(TAG, "onClick: Going back to HomeActivity");
                    ((HomeActivity)mCallingActivity).showLayout();

                } else {
                    Log.d(TAG, "onClick: backArrow, go back to calling activity");
                    getFragmentManager().popBackStack();
                }
            }
        });

        //setup comments list
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mCommentsList.setLayoutManager(linearLayoutManager);

        adapter = new CommentListAdapter(getActivity(), commentArrayList);
        mCommentsList.setAdapter(adapter);

    }


    private void addNewComment(String newCommentText) {
        Comment comment = new Comment();
        comment.setDate_created(MyDateHandler.getCurrentDate());
        comment.setComment(newCommentText);
        comment.setUser_id(userId);


        String newNodeKey = myRef.child(getString(R.string.photos_node)).push().getKey();
        //add this new Comment to database photos node
        myRef.child(getString(R.string.photos_node))
                .child(receivedPhoto.getPhoto_id())
                .child(getString(R.string.comments_field))
                .child(newNodeKey)
                .setValue(comment);

        //add this new Comment to database user_photos node
        myRef.child(getString(R.string.user_photos_node))
                .child(receivedPhoto.getUser_id())
                .child(receivedPhoto.getPhoto_id())
                .child(getString(R.string.comments_field))
                .child(newNodeKey)
                .setValue(comment);
    }


    private void getCommentsListFromDatabase() {
        Query query =  myRef.child(getString(R.string.photos_node))
                .child(receivedPhoto.getPhoto_id())
                .child(getString(R.string.comments_field));

        //got triggered whenever, new comment on selected photo is added to the database
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: comments " + dataSnapshot);
                if(dataSnapshot.getValue() == null) {
                    Log.d(TAG, "onDataChange: no user commented on this photo");
                }

                ArrayList<Comment> commentsList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: ds " + ds);
                    Comment comment = ds.getValue(Comment.class);
                    commentsList.add(comment);
                }

                Log.d(TAG, "onDataChange: commentArray " + commentsList);

                if(runCount == 0) {
                    //first time onDataChange triggered(Initial loading)
                    Log.d(TAG, "onDataChange: commentArrayList = first run" );
                    commentArrayList.addAll(commentsList);
                    adapter.notifyItemRangeChanged(1, commentsList.size());
                    //adapter.notifyDataSetChanged();
                    runCount ++;
                    Log.d(TAG, "onDataChange: commentArrayList = " +commentArrayList);
                } else {
                    //when onDataChange triggered, when 1 comment added to database,
                    //commentsList will contain all the old comments + 1 new comment
                    //to avoid duplicate entries add only last comment to commentArrayList ,
                    commentArrayList.add(commentsList.get(commentsList.size() - 1));
                    Log.d(TAG, "onDataChange: commentArrayList " +
                            commentsList.get(commentsList.size() - 1));
                    adapter.notifyItemInserted(commentArrayList.size());
                    //adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //--------------------------------------- fire base setup -----------------------------------------//
    private void setupFireBaseAuth() {
        Log.d(TAG, "setupFireBaseAuth: ");
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Log.d(TAG, "setupFireBaseAuth: User is signed in");

        } else {

        }
    }


}
