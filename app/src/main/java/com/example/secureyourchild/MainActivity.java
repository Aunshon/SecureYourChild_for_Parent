package com.example.secureyourchild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private int timeOut=2000;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedEditor;
    Spinner userSpinner;
    private String user_type="user_type";
    private String signed_in_user;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    private String user,UserPhoneNumber=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        sharedPreferences=getSharedPreferences(user_type,MODE_PRIVATE);
        user=sharedPreferences.getString(user_type,"null");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user!="null"){
                    if (firebaseUser!=null){
//                        if (){
//
//                        }
//                        else {
//                            Intent mainint = new Intent(MainActivity.this, PhoneAuth.class);
//                            mainint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(mainint);
//                        }
                        Intent mainint = new Intent(MainActivity.this, Child_Register_Input_Activity.class);
                        mainint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainint);
                    }
                    else {
                        Intent mainint = new Intent(MainActivity.this, PhoneAuth.class);
                        mainint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainint);
                    }
                }
                else {
                    Intent mainint = new Intent(MainActivity.this, UserTypeActivity.class);
                    mainint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainint);
                }
            }
        },timeOut);

    }
}
