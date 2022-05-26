package com.engage.sourabh.attandanceSystem.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.engage.sourabh.attandanceSystem.Model.AttandanceRecord;
import com.engage.sourabh.attandanceSystem.R;
import com.engage.sourabh.attandanceSystem.global;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


public class selectrollnumber extends AppCompatActivity {
    private DatabaseReference databaseRef;
    private DatabaseReference databaseRef1;
    String icode="";
    HashSet<String> rollList=new HashSet<String>();
    private DatabaseReference databaseRef2;
    private ProgressBar pbar;
    ArrayList<Integer> list=new ArrayList<Integer>();
    long k=0;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectrollnumber);

        pbar =findViewById(R.id.setectpb);
        pbar.setVisibility(View.VISIBLE);
        final Intent intent=getIntent();
        final String course =intent.getStringExtra("course");
        final String year=intent.getStringExtra("year");
        final String division=intent.getStringExtra("division");
        final String subject=intent.getStringExtra("subject");
        final String startTime=intent.getStringExtra("starttime");
        final String EndTime=intent.getStringExtra("endtime");
        databaseRef = FirebaseDatabase.getInstance().getReference("Attandance");




        String yearsubstring=year.substring(0,2);
        icode=((global)getApplication()).getInstituteCode();
       DatabaseReference dbref= FirebaseDatabase.getInstance().getReference("institutes/"+icode+"/"+"student");
        databaseRef = dbref.child(course+"/"+yearsubstring+"/"+division);
        DatabaseReference rollcountref= databaseRef;




        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    pbar.setVisibility(View.GONE);
                    k=dataSnapshot.getChildrenCount();
                    String s1=Long.toString(k);
                    Log.d("tag","hy"+s1);

                    TableLayout tableLayout = new TableLayout(getApplicationContext());
                    tableLayout=findViewById(R.id.mainLayout);
                    TableRow tableRow;
                    CheckBox CheckBox;

                    for(int i = 1; i<= k;){
                        tableRow = new TableRow(getApplicationContext());
                        for (int j = 0; j < 3; j++) {
                            if(i<=k) {
                                CheckBox = new CheckBox(getApplicationContext());
                                CheckBox.setText("RollNo." + i);
                                CheckBox.setTextColor(Color.BLACK);
                                CheckBox.setId(i);
                                CheckBox.setTextSize(12);
                                i++;
                                CheckBox.setPadding(50, 30, 50, 30);
                                tableRow.addView(CheckBox);
                            }
                        }
                        tableLayout.addView(tableRow);
                    }


                    Button btn=new Button(getApplicationContext());
                    btn.setText("Submit");
                    btn.setTextColor(Color.WHITE);
                    btn.setId(1000);
                    btn.setBackgroundColor(Color.parseColor("#3c415e"));

                    tableLayout.addView(btn);

                    final List<String> rollNoList = new ArrayList<>();
                    for(int cb = 1; cb <= k; cb++){
                        CheckBox checkBx = findViewById(cb);
                        final int finalCb = cb;
                        checkBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                Log.d("tag", "cb- "+ finalCb +" checked- "+isChecked);
                                String rollNo = String.valueOf(finalCb);
                                if(isChecked){
                                    rollNoList.add(rollNo);
                                }else{
                                    rollNoList.remove(rollNo);
                                }
                                Log.d("tag","rollNoList - "+ Arrays.toString(rollNoList.toArray()));
                            }
                        });
                    }

                    databaseRef1 =FirebaseDatabase.getInstance().getReference("institutes/"+icode+"/"+"Attandance");
                    btn=findViewById(1000);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Date dNow = new Date( );
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMddHH:mm");
                            final String date=ft.format(dNow);

                            pbar.setVisibility(View.VISIBLE);
                            for(int roll=0;roll<rollNoList.size();roll++){
                                AttandanceRecord attandance=new AttandanceRecord(startTime,EndTime);
                                databaseRef1.child(course+"/"+year+"/"+division+"/"+ rollNoList.get(roll)+"/"+subject+"/"+date).setValue(attandance);
                                Log.d("tag","submit");
                            }
                            Date AdNow = new Date( );
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat ft1 = new SimpleDateFormat ("yyyyMMddHH:mm");
                            final String Adate=ft1.format(AdNow);

                            databaseRef2 = FirebaseDatabase.getInstance().getReference("institutes/"+icode+"/"+"Attandancedetail");
                            databaseRef2.child(course+"/"+year+"/"+division+"/"+subject+"/"+Adate).setValue("date");
                            pbar.setVisibility(View.GONE);
                            Toast.makeText(selectrollnumber.this,"Submit", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }else {
                    Log.d("tag","you don't have child");
                    Toast.makeText(selectrollnumber.this,"First Enter a Student",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("error","Error"+error);
                Toast.makeText(selectrollnumber.this,"Error--"+error,Toast.LENGTH_LONG).show();
            }
        });
    }
}
