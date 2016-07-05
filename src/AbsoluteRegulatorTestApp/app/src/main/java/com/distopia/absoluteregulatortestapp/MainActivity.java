package com.distopia.absoluteregulatortestapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.distopia.absoluteregulatortestapp.AbsoluteRegulatorView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    AbsoluteRegulatorView view;
    Timer timer;

    final long INTERVAL = 100; // 0,1sec

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (AbsoluteRegulatorView) findViewById(R.id.myView);

        final MainActivity a = this;
        // timer which simulates linear regulator to increase current value
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        float cur = view.getCurrentValue();
                        float tar = view.getTargetValue();
                        if (cur < tar) {
                            view.setCurrentValue(cur += 1);
                        }
                        if (cur > tar) {
                            view.setCurrentValue(cur -= 1);
                        }
                    }
                });
            }
        };

        timer.schedule(task, INTERVAL, INTERVAL);
    }


}
