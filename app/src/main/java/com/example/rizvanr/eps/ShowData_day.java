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

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

public class ShowData_day extends AppCompatActivity{

    private static TextView avgValue;
    private static TextView timePeriod;
    ListView listView;
    DB_Data db_data = new DB_Data();
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    List<FirebaseData> fbDataList;
    List<Integer> fbValueList;
    List<Integer> fbDateTimeList;
    static int fbLength;
    int currentYear, currentDay, currentMonth, currentHour, currentMinute;
    Date currentTime;
    Calendar cal;

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

    public void applyGraphOptions(BarGraphSeries<childDataPoint> series, GraphView graph) {

        /*graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1260);
        graph.getViewport().setMaxX(1440);*/
        //graph.getViewport().setYAxisBoundsManual(true);
        //graph.getViewport().setMinY(0);
        //graph.getViewport().setMaxY(100);
        series.setSpacing(20);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.DKGRAY);
        /*series.setValueDependentColor(new ValueDependentColor<childDataPoint>() {
            @Override
            public int get(childDataPoint data) {
                if (data.getY() < 300)
                    return Color.rgb(0, 155, 0);  //green
                else if ((data.getY() >= 300) && (data.getY() < 500))
                    return Color.rgb(255,210,0);  // yellow
                else if (data.getY() >= 500 && data.getY() < 700 )
                    return Color.rgb(255,150,0); // orange
                else
                    return Color.rgb(200,0,0);  //red
            }
        });*/
        series.setColor(Color.rgb(250,100,90)); // orange bars
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE); // hide Y axis & grid lines
        graph.getGridLabelRenderer().setGridColor(Color.DKGRAY);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return super.formatLabel((int)(value/60), isValueX) + ":" + super.formatLabel(value%60, isValueX);
                }
                else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });
        graph.getGridLabelRenderer().setHumanRounding(false);

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
        textView.setText("Last 24h");
    }

    public void getTime(){
        cal = new GregorianCalendar();
        currentYear = cal.get(Calendar.YEAR);
        currentMonth = cal.get(Calendar.MONTH)+1;
        currentDay = cal.get(Calendar.DAY_OF_MONTH);
        currentHour = cal.get(Calendar.HOUR_OF_DAY);
        currentMinute = cal.get(Calendar.MINUTE);
        cal.set(currentYear, currentMonth-1, currentDay);
        currentTime = cal.getTime();
    }
    public int daysBetween(Date d1, Date d2) {
        return (int)( (d1.getTime() - d2.getTime()) / (1000 * 60 * 60 * 24));
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
                    getTime();
                    int month = Integer.parseInt(fbData.getDate_time().substring(4,6));
                    int day = Integer.parseInt(fbData.getDate_time().substring(6,8));
                    int year = Integer.parseInt(fbData.getDate_time().substring(0,4));
                    int hour = Integer.parseInt(fbData.getDate_time().substring(8,10));
                    int minute = Integer.parseInt(fbData.getDate_time().substring(10,12));
                    Calendar fbCal = new GregorianCalendar();
                    fbCal.set(year, month-1, day);
                    if ((daysBetween(cal.getTime(), fbCal.getTime())==0) || (daysBetween(cal.getTime(),fbCal.getTime())==1 && currentHour<hour) || (daysBetween(cal.getTime(),fbCal.getTime())==1 && currentHour==hour && currentMinute<minute))
                        fbDataList.add(fbData);
                }

                FirebaseDataList adapter = new FirebaseDataList(ShowData_day.this, fbDataList);
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdata_day);

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
                    getTime();
                    int month = Integer.parseInt(data.getValue(FirebaseData.class).getDate_time().substring(4,6));
                    int day = Integer.parseInt(data.getValue(FirebaseData.class).getDate_time().substring(6,8));
                    int year = Integer.parseInt(data.getValue(FirebaseData.class).getDate_time().substring(0,4));
                    int hour = Integer.parseInt(data.getValue(FirebaseData.class).getDate_time().substring(8,10));
                    int minute = Integer.parseInt(data.getValue(FirebaseData.class).getDate_time().substring(10,12));
                    Calendar fbCal = new GregorianCalendar();
                    fbCal.set(year, month-1, day);
                    if ((daysBetween(cal.getTime(), fbCal.getTime())==0) || (daysBetween(cal.getTime(),fbCal.getTime())==1 && currentHour<hour) || (daysBetween(cal.getTime(),fbCal.getTime())==1 && currentHour==hour && currentMinute<minute)){
                        Integer fbValue = Integer.parseInt(data.getValue(FirebaseData.class).getLevel());
                        fbValueList.add(fbValue);
                        //Long fbDateTime = Long.parseLong(data.getValue(FirebaseData.class).getDate_time());
                        String tempTime = data.getValue(FirebaseData.class).getDate_time();
                        Integer time = Integer.parseInt(tempTime.substring(8,10))*60 + Integer.parseInt(tempTime.substring(10,12));
                        //fbDateTimeList.add(fbDateTime);
                        fbDateTimeList.add(time);
                    }
                }
                fbLength = fbValueList.size();
                //FirebaseDataList adapter = new FirebaseDataList(ShowData.this, fbDataList);
                //listView.setAdapter(adapter);

                ArrayList<childDataPoint> arrayList = new ArrayList<>();
                double avgValue = 0;
                for (int i = 0; i < fbLength; i++){
                    arrayList.add(new childDataPoint(fbDateTimeList.get(i), fbValueList.get(i)));
                    avgValue += (fbValueList.get(i)/fbLength);
                }

                String stringValue = String.format("%.2f",avgValue);
                showAvgValue(stringValue);
                showTimePeriod();

                // TODO: display real time and not scalable time
                GraphView graph = (GraphView) findViewById(R.id.graph);
                BarGraphSeries<childDataPoint> series = new BarGraphSeries<>();
                for (int i = 0; i < fbLength; i++) {
                    childDataPoint point = arrayList.get(i);
                    series.appendData(point, true, fbLength);
                }
                graph.addSeries(series);
                applyGraphOptions(series, graph);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }
}
