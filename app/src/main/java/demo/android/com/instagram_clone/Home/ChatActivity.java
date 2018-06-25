package demo.android.com.instagram_clone.Home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Utils.MyDateHandler;
import demo.android.com.instagram_clone.Utils.UniversalImageLoader;
import demo.android.com.instagram_clone.model.ChatItem;
import demo.android.com.instagram_clone.model.Comment;
import demo.android.com.instagram_clone.model.UserMatched;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    //FireBase Auth, DataBase
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String currentUserId;

    //widgets
    private CircleImageView mProfilePhoto;
    private TextView mUserName;
    private ImageView mBackArrow, mCheckMark;
    private RecyclerView mChatList;
    private EditText mMessageText;
    private ProgressBar mProgressBar;

    //vars
    private ArrayList<ChatItem> mChatItemsList;
    private ChatListAdapter adapter;
    private UserMatched mChatWithUser;
    private LinearLayoutManager linearLayoutManager;
    private int runCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupFireBaseAuth();
        myRef = FirebaseDatabase.getInstance().getReference();

        mProfilePhoto = findViewById(R.id.profile_photo);
        mUserName = findViewById(R.id.userName);
        mBackArrow = findViewById(R.id.backArrow);
        mChatList = findViewById(R.id.recyclerMessageList);
        mMessageText = findViewById(R.id.message_text);
        mCheckMark = findViewById(R.id.checkMark);
        mProgressBar = findViewById(R.id.progressBar);

        mChatItemsList = new ArrayList<>();
        adapter = new ChatListAdapter(this, mChatItemsList, currentUserId);
        linearLayoutManager = new LinearLayoutManager(this);

        Intent intent = getIntent();
        if(intent != null) {
            mChatWithUser = intent.getParcelableExtra(getString(R.string.userMatched_intent));
            mProgressBar.setVisibility(View.VISIBLE);
            setupWidgets();
            getAllMessages();
        }

        // on clicks
        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void setupWidgets() {
        UniversalImageLoader.setImage(mChatWithUser.getProfile_photo(), mProfilePhoto, null, "");
        mUserName.setText(mChatWithUser.getUser_name());

        mChatList.setLayoutManager(linearLayoutManager);
        mChatList.setAdapter(adapter);

        mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = mMessageText.getText().toString();
                addMessageToDataBase(messageText);
            }
        });
    }


    /*
        messages -> currentUserId -> chat_with_user_id -> new key -> chatItem
     */
    private void addMessageToDataBase(String message) {
        ChatItem chatItem = new ChatItem(message, currentUserId, MyDateHandler.getCurrentDate());
        String newKey = myRef.child(getString(R.string.messages_node)).push().getKey();

        //add chat under currentUserId
        myRef.child(getString(R.string.messages_node))
                .child(currentUserId)
                .child(mChatWithUser.getUser_id())
                .child(newKey)
                .setValue(chatItem);

        //add chat under chatWithUser userId
        myRef.child(getString(R.string.messages_node))
                .child(mChatWithUser.getUser_id())
                .child(currentUserId)
                .child(newKey)
                .setValue(chatItem);

    }


    private void getAllMessages() {
        Query query =  myRef.child(getString(R.string.messages_node))
                .child(currentUserId)
                .child(mChatWithUser.getUser_id());

        //got triggered whenever, new message is added to the database
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: comments " + dataSnapshot);
                mProgressBar.setVisibility(View.GONE);
                if(dataSnapshot.getValue() == null) {
                    Log.d(TAG, "onDataChange: no user commented on this photo");
                }

                ArrayList<ChatItem> chatList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: ds " + ds);
                    ChatItem chatItem = ds.getValue(ChatItem.class);
                    chatList.add(chatItem);
                }


                if(runCount == 0) {
                    //first time onDataChange triggered(Initial loading)
                    Log.d(TAG, "onDataChange: commentArrayList = first run" );
                    mChatItemsList.addAll(chatList);
                    adapter.notifyItemRangeChanged(1, mChatItemsList.size());
                    //adapter.notifyDataSetChanged();
                    runCount ++;
                    Log.d(TAG, "onDataChange: commentArrayList = " +mChatItemsList);
                } else {
                    //when onDataChange triggered, when 1 comment added to database,
                    //chatList will contain all the old comments + 1 new comment
                    //to avoid duplicate entries add only last comment to commentArrayList ,
                    mChatItemsList.add(chatList.get(chatList.size() - 1));
                    adapter.notifyItemInserted(mChatItemsList.size() - 1);
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
            currentUserId = currentUser.getUid();

        } else {

        }
    }
}
