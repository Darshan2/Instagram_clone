package demo.android.com.instagram_clone.Dialogs;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import demo.android.com.instagram_clone.Profile.EditProfileFragment;
import demo.android.com.instagram_clone.R;

/**
 * Created by Admin on 02-06-2018.
 */

public class DialogConfirmPassword extends DialogFragment {

    //To pass value back to the fragment, that inflate this dialog
    public interface OnConfirmPasswordListener {
        public void onConfirmPassword(String pass_word);
    }

    private static final String TAG = "DailogConfirmPassword";
    private EditText mPassword;
    private TextView mConfirm, mCancel;

    private OnConfirmPasswordListener onConfirmPasswordListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_password, container, false);
        Log.d(TAG, "onCreateView: dialog started");

        mPassword = view.findViewById(R.id.input_password);
        mConfirm = view.findViewById(R.id.confirm_dialog);
        mCancel = view.findViewById(R.id.cancel_dialog);

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passWord = mPassword.getText().toString();
                Log.d(TAG, "onClick: dialog confirmed retrieving password entered" +passWord);
                //Pass passWord to fragment
                onConfirmPasswordListener.onConfirmPassword(passWord);
                getDialog().dismiss();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the dialog");
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            onConfirmPasswordListener = (OnConfirmPasswordListener) getTargetFragment();
        } catch (ClassCastException e) {
            Log.d(TAG, "onAttach: "+e.getMessage());
        }
    }
}
