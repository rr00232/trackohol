package com.example.rizvanr.eps;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;




public class MainActivity extends AppCompatActivity {
    private static FragmentManager fragmentManager;

    // Firebase
    FirebaseDatabase database;
    DatabaseReference users;

    EditText edtEmailid, edtPassword, edtUsername;
    Button btnSignUp, btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
// If you save the state of the application in a bundle (typically non-persistent,
// dynamic data in onSaveInstanceState), it can be passed back to onCreate if the activity needs
// to be recreated (e.g., orientation change) so that you don't lose this prior information.
// If no data was supplied, savedInstanceState is null.
// you will get the Bundle null when activity get starts first time and it will get in use when activity orientation get changed
// TODO SHOULD-HAVE check if the user has logged in already if so then go straight to ShowData activity look at Login_Fragmet intent to see how to go there so savedInstanceState != null not sure how this will be implemented remember to finish this activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();


        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        edtEmailid = (EditText) findViewById(R.id.userEmailId);
        edtPassword =(EditText) findViewById(R.id.password);
        edtUsername = (EditText) findViewById(R.id.fullName);

        btnSignUp = (Button)  findViewById(R.id.signUpBtn);

       // btnSignUp.setOnClickListener(new View.OnClickListener(){
         //   @Override
         //   public void onClick(View view){
         //       if (v == )

          //  }
       // });

        //btnLogin = (Button) findViewById(R.id.loginBtn);
       // btnLogin.setOnClickListener(new View.OnClickListener(){
        //  @Override
        //  public void  onClick(View view){
        //      Intent s = new Intent(getApplicationContext(), Login_Fragment.class);
        //     startActivity(s);

        //    }
        //});


        // If savedinstnacestate is null then replace login fragment
        if (savedInstanceState == null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.frameContainer, new Login_Fragment(),
                            Utils.Login_Fragment).commit();
        }

        // On close icon click finish activity
        findViewById(R.id.close_activity).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        finish();

                    }
                });

    }

    // Replace Login Fragment with animation
    protected void replaceLoginFragment() {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.left_enter, R.anim.right_out)
                .replace(R.id.frameContainer, new Login_Fragment(),
                        Utils.Login_Fragment).commit();
    }

    @Override
    public void onBackPressed() {

        // Find the tag of signup
        Fragment SignUp_Fragment = fragmentManager
                .findFragmentByTag(Utils.SignUp_Fragment);

        // Check if both are null or not
        // If both are not null then replace login fragment else do backpressed
        // task

        if (SignUp_Fragment != null)
            replaceLoginFragment();
        else
            super.onBackPressed();
    }
}
