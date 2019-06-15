package com.example.secureyourchild;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllChild extends AppCompatActivity {

    DatabaseReference mdatabaseRef;
    RecyclerView recyclerView;
    ArrayList<ChildAddClass> list;
    MyAdapter myAdapter;

    String ChildId;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    private DatabaseReference mdatabaseref,addchildDatabase;
    String UID,CHILDID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_child);

        mAuth= FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        mdatabaseref= FirebaseDatabase.getInstance().getReference("Users");
        UID=firebaseUser.getUid();


        recyclerView=findViewById(R.id.myRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list= new ArrayList<ChildAddClass>();
        mdatabaseRef= FirebaseDatabase.getInstance().getReference().child("added_child_by_parent").child(UID);
        mdatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ChildAddClass station=dataSnapshot1.getValue(ChildAddClass.class);
                    list.add(station);
                }
                myAdapter=new MyAdapter(AllChild.this,list);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setClickable(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllChild.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
