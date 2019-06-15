package com.example.secureyourchild;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<ChildAddClass> stationInfos;

    public MyAdapter(Context context, ArrayList<ChildAddClass> stationInfos) {
        this.context = context;
        this.stationInfos = stationInfos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.name.setText(stationInfos.get(i).getChildName());
        myViewHolder.phone.setText(stationInfos.get(i).getChildPhone());
        myViewHolder.age.setText(stationInfos.get(i).getChildAge());
//        myViewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Intent intent=new Intent(context,AddDetails.class);
//                intent.putExtra("key",stationInfos.get(i).getUploadId());
//                intent.putExtra("lat",stationInfos.get(i).getLat());
//                intent.putExtra("lon",stationInfos.get(i).getLon());
//                intent.putExtra("address",stationInfos.get(i).getAdd());
//                context.startActivity(intent);
//                return true;
//            }
//        });
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder alert=new AlertDialog.Builder(context);
                alert.setTitle("Confirm...");
                alert.setMessage("Are You Sure , You Want To Remove This Child ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference addchildDatabase= FirebaseDatabase.getInstance().getReference().child("added_child_by_parent").child(stationInfos.get(i).getParentId()).child(stationInfos.get(i).getChildID());
                        addchildDatabase.removeValue();

                        Toast.makeText(context, "Child Deleted", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(context,ParentMapActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                });
                alert.setCancelable(true);
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Not Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return stationInfos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView name,phone,age;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            phone=itemView.findViewById(R.id.phone);
            age=itemView.findViewById(R.id.age);
            cardView=itemView.findViewById(R.id.cardView_id);
        }

    }
}
