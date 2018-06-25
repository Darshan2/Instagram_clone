package demo.android.com.instagram_clone.LogIn;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Utils.DataBaseHandler;
import demo.android.com.instagram_clone.Utils.StringManipulation;
import demo.android.com.instagram_clone.model.User;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private AppCompatActivity mContext;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userID;
    private String userEmail;
    private DataBaseHandler dataBaseHandler;

    private EditText mEmail, mPassword, mUserName;
    private AppCompatButton mRegisterBtn;
    private ProgressBar mProgressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: ");

        mContext = this;

        initWidgets();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        dataBaseHandler = new DataBaseHandler(mContext , null);

       // initWidgets();
        getUserData();

    }


    private void initWidgets() {
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mUserName = findViewById(R.id.input_userName);
        mProgressBar = findViewById(R.id.loginProgressbar);
        mRegisterBtn = findViewById(R.id.register_btn);
    }


    private void getUserData() {
        mProgressBar.setVisibility(View.GONE);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = mEmail.getText().toString();
                String userName = mUserName.getText().toString();
                String passWord = mPassword.getText().toString();

                //If user left One or more fields empty while filling info
                if(userEmail.equals("") || userName.equals("") || passWord.equals("")) {
                    Toast.makeText(mContext, "All fields must be filled", Toast.LENGTH_SHORT).show();

                } else {
                    signUpUser(userEmail, userName, passWord);
                }
            }
        });
    }


    private void signUpUser(String email, final String userName, String password) {
        mProgressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success,
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userID = user.getUid();

                            Log.d(TAG, "createUserWithEmail: " + userID);
                            handleDataBaseTransaction(userName);

                            sendVerificationEmail(user);
                            //mAuth.signOut();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "sendVerificationEmail: success");
                Toast.makeText(RegisterActivity.this, "Verify your email in Inbox to proceed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * When new user signed in for first time, we have to add his info to database
     * Our database primarily has 2 nodes 1.users  2.user_account_settings
     * We need to add child down this nodes representing signed-up user
     */
    private void handleDataBaseTransaction(final String userName) {
        Log.d(TAG, "handleDataBaseTransaction: ");

        // Read from the database only once when the user signed up
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                String newUserName = userName;
//                String userID = myRef.push().getKey(); //returns for ex:8qrem7MTdVg3hcWOsSoPqDKrlvd2

                //check if the UserName is already in Database, and take necessary steps to add new user
                // to database
                checkForExistingUser(userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    /**
     *  check if the UserName is already in Database, and take necessary steps to add new user
     *   to database
     */
    public void checkForExistingUser(final String user_name) {
        /**
         * In database user_name is stored in firstName.lastName format
         * user enter them in the form of firstName lastName
         */
        final String userName = StringManipulation.condenseUserName(user_name);

        DatabaseReference usersNode = myRef.child(mContext.getString(R.string.users_node));
        Query queryForGivenUserName = usersNode
                .orderByChild(mContext.getString(R.string.user_name_field))
                .equalTo(userName);

        queryForGivenUserName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String newUserName = user_name;
                String rand = myRef.push().getKey();

                Log.d(TAG, "checkForExistingUser onDataChange: " + dataSnapshot);

                //if dataSnapshot has value equals to given user_name, user_name already exist in database
                if (dataSnapshot.getValue() != null) {
                    Log.d(TAG, "onDataChange: userName already exist appending some random string");
                    String append = rand.substring(3,10);
                    newUserName = userName + append;
                } else {
                    Log.d(TAG, "checkForExistingUser onDataChange: user_name:" + user_name + " saved");

                    Toast.makeText(mContext, "User name saved", Toast.LENGTH_SHORT).show();
                }
                addNewUserInfoToDatabase(newUserName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addNewUserInfoToDatabase(String userName) {
        Log.d(TAG, "addNewUserInfoToDatabase: email" + userEmail);
        dataBaseHandler.writeFireBase_UsersNode(userEmail, 0, userID, userName);

        mAuth.signOut();
        //Navigate back to LoginActivity
        finish();
    }


}
