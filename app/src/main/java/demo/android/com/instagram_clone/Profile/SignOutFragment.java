package demo.android.com.instagram_clone.Profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import demo.android.com.instagram_clone.LogIn.LoginActivity;
import demo.android.com.instagram_clone.R;


public class SignOutFragment extends Fragment {
    private static final String TAG = "SignOutFragment";
    private FirebaseAuth mAuth;

    private Button mLogOut;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_out, container, false);

        mLogOut = view.findViewById(R.id.logOutBtn);
        mProgressBar = view.findViewById(R.id.progressBar);

        logOutUser();

        return view;

    }

    //--------------------- fire Base ------------------------------------//

    private void logOutUser() {
        mProgressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null) {
                    Log.d(TAG, "onClick: trying to sign out");
                    //User in Signed-in, now sign-out user
                    mAuth.signOut();
                    //Navigate to the LogInActivity
                    //Clear the Back-Stack so that user can not access the app after signing out
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            }
        });
    }


}
