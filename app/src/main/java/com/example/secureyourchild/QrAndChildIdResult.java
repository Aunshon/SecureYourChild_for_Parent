package com.example.secureyourchild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QrAndChildIdResult extends AppCompatActivity {

    TextView name,phone,age;
    String ChildId;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    private DatabaseReference mdatabaseref,addchildDatabase;
    String UID,CHILDID;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedEditor;
    private String user_type="user_type",sharename="name",shareage="age",cid="cid";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_and_child_id_result);

        sharedPreferences=getSharedPreferences(user_type,MODE_PRIVATE);
        sharedEditor=sharedPreferences.edit();
        ChildId=getIntent().getStringExtra("qrresult");
        name=findViewById(R.id.resultname);phone=findViewById(R.id.resultphone);age=findViewById(R.id.resultage);
        mAuth= FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        mdatabaseref= FirebaseDatabase.getInstance().getReference("Users");
        UID=firebaseUser.getUid();

        if (firebaseUser!=null){
            mdatabaseref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                        UserInfoClass userData=postSnapshot.getValue(UserInfoClass.class);
                        if (ChildId.equals(userData.getUid())){
                            name.setText(userData.getUsername());
                            phone.setText(userData.getPhone());
                            age.setText(userData.getAge());
                            CHILDID=userData.getUid();
                        }
//                        else {
//                            //Toast.makeText(QrAndChildIdResult.this, "You Entered Fake id or Qr Code", Toast.LENGTH_SHORT).show();
//                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(QrAndChildIdResult.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void ChildAddBtnClick(View view) {
        if (name.getText().toString()!="" && phone.getText().toString()!="" && age.getText().toString()!=""){
            addchildDatabase=FirebaseDatabase.getInstance().getReference().child("added_child_by_parent").child(UID).child(CHILDID);
            ChildAddClass childAddClass=new ChildAddClass(CHILDID,name.getText().toString(),phone.getText().toString(),age.getText().toString(),UID);
            addchildDatabase.setValue(childAddClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(QrAndChildIdResult.this, "Child Added 👏", Toast.LENGTH_SHORT).show();
                        sharedEditor.putString(cid,CHILDID);
                        sharedEditor.apply();
                        Intent mainint=new Intent(QrAndChildIdResult.this,ParentMapActivity.class);
                        mainint.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainint);
                    }
                    else {
                        Toast.makeText(QrAndChildIdResult.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
        }
        else {
            onBackPressed();
        }
    }
}
