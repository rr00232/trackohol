package com.example.rizvanr.eps;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FirebaseDataList extends ArrayAdapter {

    private Activity context;
    private List<FirebaseData> firebaseDataListl;

    public FirebaseDataList(Activity context, List<FirebaseData> firebaseDataListl) {
        super(context, R.layout.list_layout, firebaseDataListl);
        this.context = context;
        this.firebaseDataListl = firebaseDataListl;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView textViewDate = (TextView)listViewItem.findViewById(R.id.textViewDate);
        TextView textViewValue = (TextView)listViewItem.findViewById(R.id.textViewValue);

        FirebaseData fbData1 = firebaseDataListl.get(position);

        String dt = fbData1.getDate_time();
        String printDT = (" "+dt.substring(6,8)+"/"+dt.substring(4,6)+"/"+dt.substring(0,4)+"   " +
                dt.substring(8,10)+":"+dt.substring(10,12)+"");
        textViewDate.setText(printDT);

        textViewValue.setText(" "+fbData1.getLevel()+" mg/L");

        //retValue.add(Integer.parseInt(fbData1.getLevel()));
        //retDateTime.add(Long.parseLong(fbData1.getDate_time()));

        return listViewItem;
    }


}
