package com.example.rizvanr.eps;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class MeasuringPage extends AppCompatActivity {
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    //  Receiving Data from the Device
    //  read function so that it will automatically trigger when any incoming data is detected.
    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                Log.d("SERIAL", "DATA RECEIVED: " + data);
                if (data != null && !data.isEmpty()){
                    returnToMesurements(data);
                }
                    // appended to the TextView
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }



        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        //Broadcast Receiver to automatically start and stop the Serial connection.
        // receive the broadcast to ask for user permission and also to start the connection
        // automatically when a device is connected and to close the connection when it is disconnected.
        @Override
        public void onReceive(Context context, Intent intent) {
            // get user permission
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    // initiate a connection for the device whose vendor ID matched our required vendor ID
                    connection = usbManager.openDevice(device);
                    // SerialPort is defined using the device as the connection as the arguments
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        // open SerialPort and set the parameters accordingly.
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            //    read data from device
                            serialPort.read(mCallback);
                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Toast.makeText(MeasuringPage.this, "PORT IS NULL", Toast.LENGTH_SHORT).show();
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Toast.makeText(MeasuringPage.this, "PERM NOT GRANTED", Toast.LENGTH_SHORT).show();
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onClickStart();
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                if (serialPort != null && serialPort.isOpen() ) {
                    serialPort.close();
                }
               try {
                     unregisterReceiver(broadcastReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(MeasuringPage.this, "Serial Connection Closed!", Toast.LENGTH_SHORT).show();
            }
        }

        ;
    };

    public void onClickStart() {
        //search for all connected devices
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            Log.d("SERIAL", "USB DEVICES FOUND");
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                // find device vendor
                if (deviceVID == 0x2341)//Arduino Vendor ID
                {
                    Log.d("SERIAL", "TRACKOHOL DEVICE FOUND");
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    Log.d("SERIAL", "PendingIntent: " + pi.toString());
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
            if (keep){
                Log.d("SERIAL", "TRACKOHOL DEVICE NOT FOUND");
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Arduino Vendor ID could not be found.",
                        Toast.LENGTH_SHORT);

                toast.show();
            }
        }
        else{
            Log.d("SERIAL", "NO DEVICES WERE FOUND");

            Toast toast = Toast.makeText(getApplicationContext(),
                    "No devices were found.",
                    Toast.LENGTH_SHORT);

            toast.show();
        }


    }


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
        myAnim.setRepeatCount(Animation.INFINITE);
        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
        onClickStart();
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMainPage();
            }
        });
    }

    @Override
    public void applyOverrideConfiguration(Configuration overrideConfiguration) {
        super.applyOverrideConfiguration(overrideConfiguration);
    }

    public void returnToMesurements(String data){
        //PACK THEM IN AN INTENT OBJECT
        if (serialPort != null && serialPort.isOpen() ) {
            serialPort.close();
        }
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, MesurementsPage.class);
        intent.putExtra("ALCOHOL_LEVEL_KEY", Float.valueOf(data));
        finish();
        startActivity(intent);

    }
    public void returnToMainPage(){
        //PACK THEM IN AN INTENT OBJECT
        if (serialPort != null && serialPort.isOpen() ) {
            serialPort.close();
        }
       try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, ShowData.class);
        finish();
        startActivity(intent);

    }


}
