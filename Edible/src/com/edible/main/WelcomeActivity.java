package com.edible.main;
import com.edible.ocr.CaptureActivity;
import com.edible.ocr.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class WelcomeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        Thread thread = new Thread() {
            @Override
            public void run() {
                int waitingTime = 3000; // ms
                try {
                    while(waitingTime > 0) {
                            sleep(100);
                            waitingTime -= 100; // 100ms per time           
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                    Intent intent = new Intent(WelcomeActivity.this, CaptureActivity.class);
                    startActivity(intent);  // enter the main activity finally
                }
            }
        };

        thread.start();
    }
}
