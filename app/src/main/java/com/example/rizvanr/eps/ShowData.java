package com.example.rizvanr.eps;

import android.graphics.Color;
import android.os.Bundle;
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class ShowData extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static TextView avgValue;
    private static TextView timePeriod;
    ListView listView;
    DB_Data db_data = new DB_Data();

    public void applyGraphOptions(BarGraphSeries<DataPoint> series, GraphView graph) {

        /*graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1260);
        graph.getViewport().setMaxX(1440);*/
        //graph.getViewport().setYAxisBoundsManual(true);
        //graph.getViewport().setMinY(0);
        //graph.getViewport().setMaxY(100);
        series.setSpacing(20);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.DKGRAY);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                if (data.getY() < 300)
                    return Color.rgb(0, 155, 0);  //green
                else if ((data.getY() >= 300) && (data.getY() < 500))
                    return Color.rgb(255,210,0);  // yellow
                else if (data.getY() >= 500 && data.getY() < 700 )
                    return Color.rgb(255,150,0); // orange
                else
                    return Color.rgb(200,0,0);  //red
            }
        });
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE); // hide Y axis & grid lines
        graph.getGridLabelRenderer().setGridColor(Color.DKGRAY);

    }
    public void goToMeasure(View view){
        Intent intent = new Intent(this, MeasuringPage.class);
        startActivity(intent);
    }

    //TODO: get value from Spinner to display the right time period
    public void getSpinner(){
        Spinner spinner = findViewById(R.id.dropdown_times);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.time_periods, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
    }
    public void showAvgValue(){
        TextView textView = findViewById(R.id.avgValue);
        textView.setText("average value: <TODO>");
    }
    public void showTimePeriod(){
        TextView textView = findViewById(R.id.timePeriod);
        textView.setText("2nd Jun - 1st Jun <todo>");
    }

    public int fillArray(ArrayList<String> arr){
        //arr = new ArrayList<String>();
        arr.add("201812102123-181");
        arr.add("201812102133-261");
        arr.add("201812102155-298");
        arr.add("201812102159-300");
        arr.add("201812102212-324");
        arr.add("201812102216-333");
        arr.add("201812102224-356");
        arr.add("201812102245-531");
        arr.add("201812102248-582");
        arr.add("201812102258-682");
        arr.add("201812102312-531");
        arr.add("201812102316-538");
        arr.add("201812102342-424");
        arr.add("201812102355-235");
        arr.add("201812102359-194");
        return arr.size();
    }
    // HEADS-UP: currently returns hard-coded data
    public void importData(ArrayList<String> arr){
        db_data.numElements = fillArray(arr); // to be replaced by reading from the database
        for (int count = 0; count < db_data.numElements; count++) {
            db_data.hours.add(db_data.getHour(arr.get(count)));
            db_data.mins.add(db_data.getMins(arr.get(count)));
            db_data.scalableTime.add(db_data.getScalableTime(arr.get(count)));
            db_data.alcoLevel.add(db_data.getValue(arr.get(count)));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdata);

        ArrayList<String> fakeData = new ArrayList<String>();
        importData(fakeData);

        // show UI elements
        getSpinner();
        showAvgValue();
        showTimePeriod();

        listView=(ListView)findViewById(R.id.list);
        ArrayList<DataPoint> arrayList = new ArrayList<>();
        for (int i = 0; i < db_data.numElements; i++){
            arrayList.add(new DataPoint(db_data.scalableTime.get(i), db_data.alcoLevel.get(i)));
        }

        int arrayLength = arrayList.size();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        // TO DO: display real time and not scalable time
        GraphView graph = (GraphView) findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
        for (int i = 0; i < arrayLength; i++) {
            DataPoint point = arrayList.get(i);
            series.appendData(point, true, arrayLength);
        }
        graph.addSeries(series);
        applyGraphOptions(series, graph);
    }

    @Override
    // int i = position;
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

/*      arrayList.add(new DataPoint(0,2));
        arrayList.add(new DataPoint(1,12));
        arrayList.add(new DataPoint(2,24));
        arrayList.add(new DataPoint(3,22));
        arrayList.add(new DataPoint(4,55));
        arrayList.add(new DataPoint(5,71));
        arrayList.add(new DataPoint(6,52));
        arrayList.add(new DataPoint(7,10));*/
