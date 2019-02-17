package com.example.secureyourchild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class Child_Register_Input_Activity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private String UserPhoneNumber,UID;
    SharedPreferences sharedPreferences;
    private String user_type="user_type";

    EditText nameText,ageText;
    private String usernameText,userAgeText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child__register__input_);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        nameText=findViewById(R.id.NameEt);
        ageText=findViewById(R.id.AgeEt);
        sharedPreferences=getSharedPreferences(user_type,MODE_PRIVATE);
//        usernameText=nameText.getText().toString();
//        userAgeText=ageText.getText().toString();

        if (firebaseUser==null){
            Toast.makeText(this, "You are not Signed In", Toast.LENGTH_SHORT).show();
            Intent mainint=new Intent(Child_Register_Input_Activity.this,PhoneAuth.class);
            mainint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainint);
        }
    }
    public void GoBtnClicked(View view) {
        UserPhoneNumber=firebaseUser.getPhoneNumber();
        UID=firebaseUser.getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        String device_token= FirebaseInstanceId.getInstance().getToken();
        // complex data storing
        HashMap<String, String> usermap=new HashMap<>();
        usermap.put("phone",UserPhoneNumber);
        usermap.put("name",nameText.getText().toString());
        usermap.put("age",ageText.getText().toString());
        usermap.put("uid",UID);
        usermap.put("device_token",device_token);

        usernameText=nameText.getText().toString();
        userAgeText=ageText.getText().toString();
        UserInfoClass userInfoClass=new UserInfoClass(UserPhoneNumber,usernameText,userAgeText,UID,device_token,sharedPreferences.getString(user_type,"null"));
        //Toast.makeText(this, ""+userInfoClass.getUID(), Toast.LENGTH_SHORT).show();

        mDatabase.setValue(userInfoClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Child_Register_Input_Activity.this, "Registration Successful 👏", Toast.LENGTH_SHORT).show();
                    Intent mainint=new Intent(Child_Register_Input_Activity.this,ChildMapActivity.class);
                    mainint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainint);
                }
                else {
                    Toast.makeText(Child_Register_Input_Activity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

//        firebaseAuth.signOut();
    }
}