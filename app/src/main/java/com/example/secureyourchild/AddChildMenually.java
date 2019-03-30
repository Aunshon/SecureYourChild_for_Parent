package com.example.secureyourchild;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class AddChildMenually extends Fragment {

    EditText childidtext;
    Button childidnextbtn;
    String childId;
    public AddChildMenually() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_add_child_menually, container, false);
        childidtext=view.findViewById(R.id.Childidmenualy);
        childidnextbtn=view.findViewById(R.id.childidnextbtn);
        childidnextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                childId=childidtext.getText().toString();
                if (childId.isEmpty()){
                    childidtext.setError("Please enter Child id");
                }else {
                    Intent intent=new Intent(getContext(),QrAndChildIdResult.class);
                    intent.putExtra("qrresult",childId);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

}
