package demo.android.com.instagram_clone.Share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Utils.DataBaseHandler;
import demo.android.com.instagram_clone.Utils.StringManipulation;
import demo.android.com.instagram_clone.Utils.UniversalImageLoader;

public class NextActivity extends AppCompatActivity {

    private static final String TAG = "NextActivity";

    //FireBase Auth, DataBase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DataBaseHandler dataBaseHandler;

    //widgets
    private EditText mCaption;
    private ProgressBar mProgressBar;
    private TextView mInfo;

    //vars
    private Intent intent;
    private int imageCount = 0;
    private String imageURL;
    private Bitmap imageBitmap;
    private Context mContext;
    private String mAppend = "file://";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        Log.d(TAG, "onCreate: started");

        mContext = this;
        intent = getIntent();
        dataBaseHandler = new DataBaseHandler(this, null);

        mCaption = findViewById(R.id.caption);
        mProgressBar = findViewById(R.id.progressBar);
        mInfo = findViewById(R.id.infoTV);

        mProgressBar.setVisibility(View.GONE);
        mInfo.setVisibility(View.GONE);

        setupFireBaseAuth();
        setImage();

        ImageView backArrow = findViewById(R.id.ivCloseShare);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back to calling activity
                Log.d(TAG, "onClick: Navigating back to calling activity");
                finish();
            }
        });

        TextView shareTV = findViewById(R.id.tvShare);
        shareTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInfo.setVisibility(View.GONE);
                if(mProgressBar.getVisibility() != View.VISIBLE) {
                    Log.d(TAG, "onClick: adding shared Picture to fire-base storage");
                    Toast.makeText(mContext, "Attempting to upload photo", Toast.LENGTH_SHORT).show();

                    String caption = mCaption.getText().toString();
                    Log.d(TAG, "onClick: caption =" + caption + "!");
                    if (!caption.equals("")) {
                        if (!((imageURL == null || imageURL.equals("")) && imageBitmap == null)) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            if (intent.hasExtra(getString(R.string.image_url))) {
                                dataBaseHandler.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imageURL, null);
                            } else if (intent.hasExtra(getString(R.string.image_bitmap))) {
                                dataBaseHandler.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, null, imageBitmap);
                            }
                        } else {
                            Log.d(TAG, "onClick: select photo");
                            mInfo.setVisibility(View.VISIBLE);
                            mInfo.setText("Please select photo to share!");
                        }
                    } else {
                        Log.d(TAG, "onClick: write caption");
                        mInfo.setVisibility(View.VISIBLE);
                        mInfo.setText("Please write photo caption to share photo!");
                    }
                }

                Log.d(TAG, "onClick: end");
            }

        });
    }


    /**
     * Get the imageURL from incoming intent, and set that image in shareImage_IV
     */
    private void setImage() {
        ImageView shareImage_IV = findViewById(R.id.ShareImage);

        if(intent.hasExtra(getString(R.string.image_url))) {
            String imagePath = intent.getStringExtra(getString(R.string.image_url));
            imageURL = StringManipulation.getStorageCompatibleFilepath(imagePath);
            UniversalImageLoader.setImage(imageURL, shareImage_IV, null, mAppend);
            Log.d(TAG, "setImage: image url received  " + imageURL);

        } else if(intent.hasExtra(getString(R.string.image_bitmap))) {
            imageBitmap = intent.getParcelableExtra(getString(R.string.image_bitmap));
            shareImage_IV.setImageBitmap(imageBitmap);
            Log.d(TAG, "setImage: image bitmap received " + imageBitmap);

        }

    }


    //--------------------fire base setup -----------------------------//
    private void setupFireBaseAuth() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        dataBaseHandler = new DataBaseHandler(mContext, null);

        Log.d(TAG, "setupFireBaseAuth: image count" + imageCount);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Log.d(TAG, "setupFireBase: user is signed in");
            handleDatabase();
        } else {
            Toast.makeText(mContext, "User is signed out", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDatabase() {
        Log.d(TAG, "handleDatabase: ");
        //Triggers whenever database content get changed
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Get, Count of number of photos user already have in his Fire-base storage
                imageCount = dataBaseHandler.getImageCount(dataSnapshot);

                Log.d(TAG, "onDataChange: image count" + imageCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
