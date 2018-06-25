package demo.android.com.instagram_clone.Profile;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import demo.android.com.instagram_clone.Dialogs.DialogConfirmPassword;
import demo.android.com.instagram_clone.Home.HomeActivity;
import demo.android.com.instagram_clone.LogIn.LoginActivity;
import demo.android.com.instagram_clone.R;
import demo.android.com.instagram_clone.Share.ShareActivity;
import demo.android.com.instagram_clone.Utils.DataBaseHandler;
import demo.android.com.instagram_clone.Utils.StringManipulation;
import demo.android.com.instagram_clone.Utils.UniversalImageLoader;
import demo.android.com.instagram_clone.model.User;
import demo.android.com.instagram_clone.model.UserAccountSettings;
import demo.android.com.instagram_clone.model.UserAndSettings;


public class EditProfileFragment extends Fragment implements DialogConfirmPassword.OnConfirmPasswordListener {
    private static final String TAG = "EditProfileFragment";

    //FireBase Auth, DataBase
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DataBaseHandler dataBaseHandler;

    //vars
    private Context mContext;
    private UserAndSettings userAndSettings;

    //widgets
    private CircleImageView mCircleProfilePhoto;
    private EditText mUserName, mDisplayName, mDescription, mWebsite, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private ProgressBar mProgressBar;
    private ImageView mCheckmark;


    /**
     *  When user changes his email in Profile settings.
     *  We are here receiving password entered by user in dialog DialogConfirmPassword.
     *  and taking further steps to add new email to FireBase Authentication
     */
    @Override
    public void onConfirmPassword(String pass_word) {
        Log.d(TAG, "onConfirmPassword: received passWord:" +pass_word);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication.
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), pass_word);

        ///////// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User re-authenticated.");
                        //Now we can be sure that the one trying to change email in profile settings
                        // is authenticated user.
                        /// 1. Now get the newly entered email,and
                        ///         Make sure it does not already exist in database
                        checkFireBaseAuthListForExistingEmail();

                    } else {
                            Log.d(TAG, "onComplete: re-authentication failed");
                    }
            }
       });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        Log.d(TAG, "onCreateView: started");
        mContext = getActivity();

        //When user click back arrow go to ProfileActivity
        ImageView backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigate back to ProfileActivity");
                getActivity().finish();
            }
        });

        initWidgets(view);
        setupFireBaseAuth();

        return view;
    }


    private void initWidgets(View view) {
        Log.d(TAG, "initWidgets: ");
        mCircleProfilePhoto = view.findViewById(R.id.profile_photo);
        mUserName = view.findViewById(R.id.user_name);
        mDisplayName = view.findViewById(R.id.display_name);
        mDescription = view.findViewById(R.id.description);
        mWebsite = view.findViewById(R.id.website);
        mEmail = view.findViewById(R.id.email);
        mPhoneNumber = view.findViewById(R.id.phone_number);
        mProgressBar = view.findViewById(R.id.progressBar);
        mCheckmark = view.findViewById(R.id.checkMark);
        mChangeProfilePhoto = view.findViewById(R.id.changeProfilePhoto);

        mCheckmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileSettings();
            }
        });
    }


    private void setupFragmentWidgets(UserAndSettings userAndSettings) {
        Log.d(TAG, "setupFragmentWidgets: ");
        mProgressBar.setVisibility(View.GONE);

        User user = userAndSettings.getUser();
        UserAccountSettings accountSettings = userAndSettings.getUserAccountSettings();
        Log.d(TAG, "setupFragmentWidgets: "+ user + accountSettings);

        mDisplayName.setText(accountSettings.getDisplay_name());
        mDescription.setText(accountSettings.getDescription());
        mWebsite.setText(accountSettings.getWebsite());
        mUserName.setText(user.getUser_name());
        mEmail.setText(user.getEmail());
        mPhoneNumber.setText(Long.toString(user.getPhone_number()));

        String imageUrl = accountSettings.getProfile_photo();
        UniversalImageLoader.setImage(imageUrl, mCircleProfilePhoto, null, "");

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navigate to ShareActivity,
                //from there it goes to GalleryFragment(since it is first tab of ShareActivity)
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                //to differentiate between root call, and call from here in ShareActivity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //to avoid back stack navigation from EditProfileFragment to EditProfileFragment
                getActivity().finish();
            }
        });

    }


    /**
     * Read data from widgets, and save it in FireBase DataBase
     */
    private void saveProfileSettings() {
        Log.d(TAG, "saveProfileSettings: ");
        final String user_name = mUserName.getText().toString();
        String display_name = mDisplayName.getText().toString();
        String description = mDescription.getText().toString();
        String website = mWebsite.getText().toString();
        String email = mEmail.getText().toString();
        String phone_number = mPhoneNumber.getText().toString();

        Long phoneNumber = new Long(phone_number);
        Log.d(TAG, "saveProfileSettings: phonenumber"+ phoneNumber);

        //check if user changes his user_name
        if(!user_name.equals(userAndSettings.getUser().getUser_name())) {
            //user changed his user_name
            Log.d(TAG, "saveProfileSettings: user changed user_name");
            checkForExistingUser(user_name);
        } else {
            //update user-info in DataBase
            Log.d(TAG, "saveProfileSettings: updating user info");
            dataBaseHandler.updateUserNameInDataBase(user_name);
        }

        //check if user changed his email
        if(!email.equals(userAndSettings.getUser().getEmail())) {
            Log.d(TAG, "saveProfileSettings: user changed email");
            /**
             * Step1) Re-authenticate user
             *       -Confirm the password and email
             */
            DialogConfirmPassword dialog = new DialogConfirmPassword();
            dialog.show(getFragmentManager(), getString(R.string.dialog_cofirm_password));
            //below line is a Must if missed dialog box will not know to where it should send the
            //retrieved user info, app will show bizarre behaviour without raising an error
            dialog.setTargetFragment(EditProfileFragment.this, 1);

            //from here control goes to @override onConfirmPassword

            //Step2) Check if the newly entered email is already registered
            //          -fetchProvidersForEmail(String email)
            //Step3) Change the email
            //          -submit the email to database and authenticate it

        }

        if(!description.equals(userAndSettings.getUserAccountSettings().getDescription())) {
            //update description in database
            dataBaseHandler.updateDataBaseAccountSettingsField(
                    getString(R.string.description_field), description, false);
        }

        if(!display_name.equals(userAndSettings.getUserAccountSettings().getDisplay_name())) {
            //update display_name in database
            dataBaseHandler.updateDataBaseAccountSettingsField(
                    getString(R.string.displayName_field), display_name, false);
        }

        if(!website.equals(userAndSettings.getUserAccountSettings().getWebsite())) {
            //update website in database
            dataBaseHandler.updateDataBaseAccountSettingsField(
                    getString(R.string.website_field), website, false);
        }

        if(!phone_number.equals(userAndSettings.getUser().getPhone_number())) {
            //update phone_number in database
            dataBaseHandler.updateDataBaseUsersField(
                    getString(R.string.phoneNumber_field), phone_number, true);
        }

        Toast.makeText(mContext, "Profile updated successfully", Toast.LENGTH_SHORT).show();

    }


    public void checkForExistingUser(final String user_name) {
        /**
         * In database user_name is stored in firstName.lastName format
         * user enter them in the form of firstName lastName
         */
        String userName = StringManipulation.condenseUserName(user_name);

        DatabaseReference usersNode = myRef.child(mContext.getString(R.string.users_node));
        Query queryForGivenUserName = usersNode
                .orderByChild(mContext.getString(R.string.user_name_field))
                .equalTo(userName);

        queryForGivenUserName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if dataSnapshot has value equals to given user_name, user_name already exist in database
                if (dataSnapshot.getValue() != null) {
                    Log.d(TAG, "onDataChange: user_name:" + user_name + " already exists");
                    Toast.makeText(mContext, "User name already exists", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onDataChange: user_name:" + user_name + " saved");
                    dataBaseHandler.updateUserNameInDataBase(user_name);

                    Toast.makeText(mContext, "User name saved", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //------------------------------ OnConfirmPassword() extended body --------------------------------//
    private void checkFireBaseAuthListForExistingEmail() {
        mAuth.fetchProvidersForEmail(mEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        if(task.isSuccessful()) {
                            try {
                                if (task.getResult().getProviders().size() == 1) {
                                    //newly entered email already exist in database
                                    Log.d(TAG, "onComplete: Email already exist");
                                    Toast.makeText(mContext, "Email already exist", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d(TAG, "onComplete: email is available, adding new email to databse");
                                    /// 2. email is available update the database with new email
                                    mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    //email successfully updated in database
                                                    Log.d(TAG, "onComplete: Email updated successfully");
                                                    Toast.makeText(mContext, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                                    dataBaseHandler.updateDataBaseUsersField(
                                                            getString(R.string.email_field),
                                                            mEmail.getText().toString(), false);

                                                }
                                            });
                                }
                            } catch (NullPointerException e) {
                                Log.d(TAG, "onComplete: "+ e.getMessage());
                            }
                        }
                    }
                });
    }


    //---------------------------------------fire base setup -----------------------------------------//
    private void setupFireBaseAuth() {
        Log.d(TAG, "setupFireBaseAuth: ");
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        dataBaseHandler = new DataBaseHandler(mContext, mProgressBar);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Log.d(TAG, "setupFireBaseAuth: User is signed in");
            handleDatabase(currentUser.getUid());
        } else {

        }
    }


    private void handleDatabase(final String userId) {
        Log.d(TAG, "handleDatabase: ");
        //Triggers whenever database content get changed
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Read new User data from Database
                Log.d(TAG, "onDataChange: received info from data base" +dataSnapshot);
                userAndSettings = dataBaseHandler.getUserDataFromFireBase(dataSnapshot, userId);
                setupFragmentWidgets(userAndSettings);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
