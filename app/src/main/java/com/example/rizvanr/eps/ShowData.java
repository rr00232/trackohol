package com.example.rizvanr.eps;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Intent;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class ShowData extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    ListView listView;
    DB_Data db_data = new DB_Data();

    public String fakeData = "2100-00;2123-05;2130-09;2133-10;2225-20;2233-30;2236-32;2250-45;2300-60;2313-66;2332-78;" +
            "2350-72;2353-71;2354-52;";

    public String getHour(String str) {
        return str.length() < 2 ? str : str.substring(0, 2);
    }
    public String getMins(String str){
        return str.substring(2,4);
    }

    public void applyGraphOptions(BarGraphSeries<DataPoint> series, GraphView graph) {

        /*graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(1260);
        graph.getViewport().setMaxX(1440);*/
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        series.setSpacing(70);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.DKGRAY);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL); // hide Y axis & grid lines
        graph.getGridLabelRenderer().setGridColor(Color.DKGRAY);

    }

    public void getSpinner(){
        Spinner spinner = findViewById(R.id.dropdown_times);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.time_periods, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    // HEADS-UP: shouldn't return anything meaningful atm - don't worry!
    public void importData(String stringData){

        try{
            File my_file = new File("C://Users//nikol//Documents//year3//EPS//fakedata10.txt");
            Scanner input = new Scanner(my_file);
            String[] parts;
            int count = 0;

            while (input.hasNextLine()) {
                parts = input.nextLine().split("-");
                db_data.hours[count] = Integer.parseInt(getHour(parts[0]));
                System.out.print(parts[0]);
                db_data.mins[count] = Integer.parseInt(getMins(parts[0]));
                db_data.scalableTime[count] = db_data.hours[count]*60+db_data.mins[count];
                db_data.alcoLevel[count] = Double.parseDouble(parts[1]);
                count++;
            }
            db_data.numElements = count;
        }
        catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage()); }
        /*
        int count = 0;
        for (int i = 0; i < (stringData.length()/8); i++) {
            String subString = stringData.substring(i*count, i*count + 8);
            db_data.hours[count] = Integer.parseInt(getHour(subString));
            db_data.mins[count] = Integer.parseInt(getMins(subString));
            db_data.scalableTime[count] = db_data.hours[count] * 60 + db_data.mins[count];
            db_data.alcoLevel[count] = Double.parseDouble(subString.substring(5,7));
            count++;
        }
            db_data.numElements = count;
        */
    }

    // TO DO: get data from the database
    /*public void getData(){} */

    // go to the next intent (measurement button)
    public void goToMeasure(View view){
        Intent intent = new Intent(this, MeasuringPage.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdata);

        getSpinner();
        importData(fakeData);

        listView=(ListView)findViewById(R.id.list);
        ArrayList<DataPoint> arrayList = new ArrayList<>();
        // TO DO: replace with getData (database data)

        for (int i = 0; i < db_data.numElements; i++){
            arrayList.add(new DataPoint(db_data.scalableTime[i], db_data.alcoLevel[i])); }

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
