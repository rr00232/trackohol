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
                finish();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMeasure();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurments_page);

        final TextView cancelTextView = findViewById(R.id.cancelTextView);
        final TextView saveTextView = findViewById(R.id.saveTextView);
        final Button buttonSober = findViewById(R.id.ButtonSober);
        final Button buttonDrunk = findViewById(R.id.ButtonDrunk);
        final Button buttonTipsy = findViewById(R.id.ButtonTipsy);
        final Button buttonWasted = findViewById(R.id.ButtonWasted);
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
        dataseriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - dataseriesItem.getMinValue()) / (dataseriesItem.getMaxValue() - dataseriesItem.getMinValue()));
                textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });



        decoView.addEvent(new DecoEvent.Builder(50)
                .setIndex(backIndex)
                .setDuration(1500)
                .build());

        int measuredAlcoholLevel = getIntent().getIntExtra("ALCOHOL_LEVEL_KEY", 0) / 2;

        decoView.addEvent(new DecoEvent.Builder(measuredAlcoholLevel)
                .setIndex(series1Index)
                .setDelay(1000)
                .setDuration(3000L)
                .setEffectRotations(3)
                .build());

        buttonSober.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        buttonDrunk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        buttonTipsy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        buttonWasted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

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


    public void returnToMeasure(){
        Intent intent = new Intent(this, MeasuringPage.class);
        finish();
        startActivity(intent);

    }




}
