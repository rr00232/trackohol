package com.example.rizvanr.eps;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;


public class MesurementsPage extends Activity {


    @Override
    public void onBackPressed(){
        alertWindowFunction();
    }

    public void alertWindowFunction(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(MesurementsPage.this);
        builder.setCancelable(true);

        final View mView = getLayoutInflater().inflate(R.layout.dialog_leave_page, null);
        TextView cancelButton = (TextView) mView.findViewById(R.id.cancelTextView);
        TextView discardButton = (TextView) mView.findViewById(R.id.discardTextView);
        TextView saveButton = (TextView) mView.findViewById(R.id.saveTextView);
        builder.setView(mView);
        final AlertDialog alertDialog = builder.create();

        alertDialog.show();
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();

            }
        });
        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                finish();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                returnToMainPage();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurments_page);

        final TextView cancelTextView = findViewById(R.id.cancelTextView);
        final TextView saveTextView = findViewById(R.id.saveTextView);
        final DecoView decoView = findViewById(R.id.dynamicArcView);

        final SeriesItem backseriesItem = new SeriesItem.Builder(Color.parseColor("#cac1b2"))
                .setRange(0, 50, 0)
                .build();
        int backIndex = decoView.addSeries(backseriesItem);

        final SeriesItem dataseriesItem = new SeriesItem.Builder(Color.parseColor("#FF5733"), Color.parseColor("#e9859f"))
                .setInitialVisibility(false)
                .setInterpolator(new BounceInterpolator())
                .setRange(0, 50, 0)
                .setCapRounded(true)
                .build();

        final int series1Index = decoView.addSeries(dataseriesItem);

        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);
        decoView.addEvent(new DecoEvent.Builder(50)
                .setIndex(backIndex)
                .setDuration(1500)
                .build());
        float measuredAlcoholLevelMgL = getIntent().getFloatExtra("ALCOHOL_LEVEL_KEY", 0);

        long measuredAlcoholLevel = Math.round((measuredAlcoholLevelMgL/0.75)*100);
        Log.d("DISPLAY", "DATA RECEIVED: " + Float.toString(measuredAlcoholLevelMgL));

        dataseriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - dataseriesItem.getMinValue()) / (dataseriesItem.getMaxValue() - dataseriesItem.getMinValue()));

                textPercentage.setText(String.format("%.2f mg/L", measuredAlcoholLevelMgL));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        decoView.addEvent(new DecoEvent.Builder(measuredAlcoholLevel)
                .setIndex(series1Index)
                .setDelay(1000)
                .setDuration(3000L)
                .setEffectRotations(3)
                .build());

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertWindowFunction();
            }
        });
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancelTextView.setTextColor(Color.WHITE);
                Log.d("EPS", "HELLO: The button has been pressed" + saveTextView);
            }
        });
    }



    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        super.applyOverrideConfiguration(overrideConfiguration);
    }


    public void returnToMainPage(){
        Intent intent = new Intent(this, ShowData.class);
        finish();
        startActivity(intent);

    }




}
