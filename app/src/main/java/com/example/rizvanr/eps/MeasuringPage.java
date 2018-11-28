package com.example.rizvanr.eps;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;


import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class MeasuringPage extends AppCompatActivity {
    CircularProgressButton mCircularProgressButton;
    class MyBounceInterpolator implements android.view.animation.Interpolator {
        private double mAmplitude = 1;
        private double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                    Math.cos(mFrequency * time) + 1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measuring_page);
        final TextView cancelTextView = findViewById(R.id.cancelTextView);
        mCircularProgressButton = (CircularProgressButton)findViewById(R.id.buttonMeasuring);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        mCircularProgressButton.startAnimation(myAnim);
        mCircularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<String,String,String> demoDownload = new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                            try{
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return "done";
                        }

                    @Override
                    protected void onPostExecute(String s) {
                        if(s.equals("done"))
                        {
                            Toast.makeText(MeasuringPage.this, "Download Done", Toast.LENGTH_SHORT).show();
                            mCircularProgressButton.doneLoadingAnimation(Color.parseColor("#172321"), BitmapFactory.decodeResource(getResources(),R.drawable.ic_done_white_48dp));
                            returnToMesurements();
                        }
                    }
                };
                mCircularProgressButton.startAnimation();
                demoDownload.execute();
                }
        });

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMesurements();
            }
        });
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    public void returnToMesurements(){
        //PACK THEM IN AN INTENT OBJECT
        Intent intent = new Intent(this, MesurementsPage.class);
        int alcoholLevel = 90;
        intent.putExtra("ALCOHOL_LEVEL_KEY", alcoholLevel);
        finish();
        startActivity(intent);

    }


}
