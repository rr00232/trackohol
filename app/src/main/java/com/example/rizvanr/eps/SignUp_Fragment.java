package com.example.rizvanr.eps;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//Firebase
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp_Fragment extends Fragment implements OnClickListener {
    private static View view;
    private static EditText fullName, emailId, password, confirmPassword;
    private static TextView login;
    private static Button signUpButton, verifyEmailButton;
    private static CheckBox terms_conditions;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private FirebaseAuth mAuth;

    public SignUp_Fragment() {
        mAuth = FirebaseAuth.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.signup_layout, container, false);
        initViews();
        setListeners();
        return view;


    }

    // Initialize all views
    public void initViews() {
        fullName = (EditText) view.findViewById(R.id.fullName);
        emailId = (EditText) view.findViewById(R.id.userEmailId);
        password = (EditText) view.findViewById(R.id.password);
        confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
        signUpButton = (Button) view.findViewById(R.id.signUpBtn);
        login = (TextView) view.findViewById(R.id.already_user);
        terms_conditions = (CheckBox) view.findViewById(R.id.terms_conditions);
        // check_send = (Button) view.findViewById(R.id.email_veriBtn);
       verifyEmailButton = (Button) view.findViewById(R.id.verifyEmailBtn);
        verifyEmailButton.setEnabled(false);
        verifyEmailButton.setVisibility(View.GONE);

    }

    // Set Listeners
    private void setListeners() {
        signUpButton.setOnClickListener(this);
        verifyEmailButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verifyEmailBtn:
                sendEmail();
                break;

            case R.id.signUpBtn:
                // Call checkValidation method
                signUp();
                break;

            case R.id.already_user:

                // Replace login fragment
                new MainActivity().replaceLoginFragment();
                break;
        }

    }

    // Check Validation Method
    private boolean checkValidation() {
        boolean isValid = true;
        // Get all edittext texts
        String getFullName = fullName.getText().toString();
        String getEmailId = emailId.getText().toString();
        String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();

        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);
        Matcher m = p.matcher(getEmailId);
        // Check if all strings are null or not

        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0) {

            new CustomToast().Show_Toast(getActivity(), view,
                    "All fields are required.");
             isValid = false;
        }

            // Check if email id valid or not
        else if (!m.find()) {
            new CustomToast().Show_Toast(getActivity(), view,
                    "Your Email Id is Invalid.");
            isValid = false;
        }
            // Check if both password should be equal
        else if (!getConfirmPassword.equals(getPassword)) {
            new CustomToast().Show_Toast(getActivity(), view,
                    "Both password doesn't match.");
             isValid = false;
        }
            // Make sure user should check Terms and Conditions checkbox
        else if (!terms_conditions.isChecked()) {
            new CustomToast().Show_Toast(getActivity(), view,
                    "Please select Terms and Conditions.");
             isValid = false;
        }
         return isValid;
    }

    private void signUp() {
        final String getEmailId3 = emailId.getText().toString();
        final String getPassword3 = password.getText().toString();
       if (!checkValidation()){
           return;
       }

        else {
            mAuth.fetchSignInMethodsForEmail(getEmailId3)
                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            boolean check = task.getResult().getSignInMethods().isEmpty();
                            if (!check)
                                Toast.makeText(getActivity(), "email already in use", Toast.LENGTH_SHORT).show();
                            else {
                                mAuth.createUserWithEmailAndPassword(getEmailId3, getPassword3);
                                Toast.makeText(getActivity(), "Thank you for signing up.", Toast.LENGTH_SHORT)
                                        .show();
                                verifyEmailButton.setEnabled(true);
                                verifyEmailButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
    }
    private void sendEmail () {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //   check_send.setEnabled(true);
                if (task.isSuccessful()) {
                    Toast.makeText(SignUp_Fragment.this.getActivity(), "email verification sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    verifyEmailButton.setEnabled(true);
                }
                else
                    Toast.makeText(SignUp_Fragment.this.getActivity(), "fail to sent email", Toast.LENGTH_SHORT).show();
                Log.d("abcccc", user.getEmail());
            }
        });
    }
}