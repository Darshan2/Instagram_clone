package demo.android.com.instagram_clone.LogIn;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import demo.android.com.instagram_clone.Home.HomeActivity;
import demo.android.com.instagram_clone.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    private Context mContext;
    private EditText mEmail, mPassword;
    private ProgressBar mProgressBar;
    private Button mLogin_Btn;
    private TextView mSignup, mVerifyInfo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mProgressBar = findViewById(R.id.progressBar);
        mSignup = findViewById(R.id.signUpLink);
        mLogin_Btn = findViewById(R.id.login_btn);
        mVerifyInfo = findViewById(R.id.verifyInfo);

        mVerifyInfo.setVisibility(View.GONE);

        getUserData();
        signUpUser();

    }


    private void getUserData() {
        Log.d(TAG, "getUserData: ");
        mProgressBar.setVisibility(View.GONE);

        mLogin_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVerifyInfo.setVisibility(View.GONE);
                String email = mEmail.getText().toString();
                String passWord = mPassword.getText().toString();

                if(email.equals("") || passWord.equals("")) {
                    mVerifyInfo.setVisibility(View.VISIBLE);
                    mVerifyInfo.setText("All fields must be filled");
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                   signInUser(email, passWord);
                }
            }
        });

    }


    private void signInUser(String email, String password) {
        Log.d(TAG, "signInUser: email password " + email +"  "+ password);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success,
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(user.isEmailVerified()) {
                                Log.d(TAG, "onComplete: user is Email verified");
                                //Navigate to HomeActivity
                                //LoginActivity.this.finish();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);

                            } else {
                                mProgressBar.setVisibility(View.GONE);
                                mVerifyInfo.setVisibility(View.VISIBLE);
                                mVerifyInfo.setText("Email is not verified, Check your inbox to verify");
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            mProgressBar.setVisibility(View.GONE);
                            mVerifyInfo.setVisibility(View.VISIBLE);
                            mVerifyInfo.setText("Email or Password error");

                        }
                    }
                });
    }


    private void signUpUser() {
        //If user clicks the SignUp link go to RegisterActivity
        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


}
