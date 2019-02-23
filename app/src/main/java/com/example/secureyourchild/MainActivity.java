package com.example.secureyourchild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private int timeOut=2000;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedEditor;
    private DatabaseReference mdatabaseref;
    Spinner userSpinner;
    private String user_type="user_type",sharephonekey="phone",sharename="name",shareage="age";
    String userPhoneOnFirebase,userNameOnFirebase,userAgeOnfirebase;;
    private String signed_in_user;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    private String user,PhoneNumber,userage,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        sharedPreferences=getSharedPreferences(user_type,MODE_PRIVATE);
        user=sharedPreferences.getString(user_type,"null");
        PhoneNumber=sharedPreferences.getString(sharephonekey,"null");
        userage=sharedPreferences.getString(shareage,"null");
        name=sharedPreferences.getString(sharename,"null");

        mdatabaseref= FirebaseDatabase.getInstance().getReference("Users");
//        if (firebaseUser!=null){
//            mdatabaseref.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
//                        UserInfoClass userData=postSnapshot.getValue(UserInfoClass.class);
//                        if (firebaseUser.getPhoneNumber().equals(userData.getPhone())){
//                            userPhoneOnFirebase=userData.getPhone();
//                            userNameOnFirebase=userData.getUsername();
//                            userAgeOnfirebase=userData.getAge();
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user!="null"){
                    if (firebaseUser!=null){
                        if (userage!="null" && PhoneNumber!="null" && name!="null"){
                            if (sharedPreferences.getString(user_type,null)=="Child"){
                                Intent mainint = new Intent(MainActivity.this, ChildMapActivity.class);
                                mainint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainint);
                            }
                            else {
                                //Toast.makeText(MainActivity.this, "User DEV at MainActivity", Toast.LENGTH_SHORT).show();
                                Intent mainint = new Intent(MainActivity.this, ParentMapActivity.class);
                                mainint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainint);
                            }
                        }
                        else {
                            Intent mainint = new Intent(MainActivity.this, Child_Register_Input_Activity.class);
                            mainint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainint);
                        }
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
