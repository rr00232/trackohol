package com.example.rizvanr.eps;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Intent;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

public class ShowData extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static TextView avgValue;
    private static TextView timePeriod;
    ListView listView;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    List<FirebaseData> fbDataList;
    List<Double> fbValueList;
    List<Long> fbDateTimeList;
    static int fbLength;
    Spinner mySpinner;
    String spinnerValue;

    public class childDataPoint extends DataPoint{

        public Integer childX;
        public Double childY;
        public Double getChildX;
        public childDataPoint(double x, double y) {
            super(x, y);
            this.getChildX= x;
            this.childX = getChildX.intValue();
            this.childY=y;
        }

        public String toString() {
            super.toString();
            return "time: "+childX/60+":"+childX%60+" - value: "+childY+"";
        }

    }

    public void goToMeasure(View view){
        Intent intent = new Intent(this, MeasuringPage.class);
        startActivity(intent);
    }

    public void goToDay(View view){
        Intent intent = new Intent(this, ShowData_day.class);
        startActivity(intent);
    }
    public void goToWeek(View view){
        Intent intent = new Intent(this, ShowData_week.class);
        startActivity(intent);
    }
    public void goToMonth(View view){
        Intent intent = new Intent(this, ShowData_month.class);
        startActivity(intent);
    }
    public void goToAllData(View view){
        Intent intent = new Intent(this, ShowData.class);
        startActivity(intent);
    }

    public void showAvgValue(String value){
        TextView textView = findViewById(R.id.avgValue);
        textView.setText("average value: "+value+"");
    }
    public void showTimePeriod(){
        TextView textView = findViewById(R.id.timePeriod);
        textView.setText("All data");
    }

    // init ListView with data
    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fbDataList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    FirebaseData fbData = data.getValue(FirebaseData.class);
                    fbDataList.add(fbData);
                }

                FirebaseDataList adapter = new FirebaseDataList(ShowData.this, fbDataList);
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdata);

        // initialise database stuff
        databaseReference = FirebaseDatabase.getInstance().getReference("user1");
        mAuth = FirebaseAuth.getInstance();
        fbDataList = new ArrayList<>();
        fbValueList = new ArrayList<>();
        fbDateTimeList = new ArrayList<>();

        listView=(ListView)findViewById(R.id.list);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //fbDataList.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Double fbValue = Double.parseDouble(data.getValue(FirebaseData.class).getLevel());
                    fbValueList.add(fbValue);
                    Long fbDateTime = Long.parseLong(data.getValue(FirebaseData.class).getDate_time());
                    fbDateTimeList.add(fbDateTime);
                }
                fbLength = fbValueList.size();
                //FirebaseDataList adapter = new FirebaseDataList(ShowData.this, fbDataList);
                //listView.setAdapter(adapter);

                ArrayList<childDataPoint> arrayList = new ArrayList<>();
                double avgValue = 0;
                for (int i = 0; i < fbLength; i++){
                    arrayList.add(new childDataPoint(fbDateTimeList.get(i), fbValueList.get(i)));
                    avgValue += (fbValueList.get(i)/(double)fbLength);
                }

                String stringValue = String.format("%.2f",avgValue);
                showAvgValue(stringValue);
                //TODO: get time period from spinner+data combo
                showTimePeriod();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    @Override
    // int i = position;
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT).show();
        spinnerValue = text;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
