package me.toptas.fancyshowcasesample;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class TestConstraintLayoutActivity extends AppCompatActivity {

    View view;
    View view3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_constraint_layout);


        view = findViewById(R.id.view);
        view3 = findViewById(R.id.view3);

        view.postDelayed(new Runnable() {
            @Override
            public void run() {

                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = 60;
                params.width = 60;
                view.setLayoutParams(params);

                Log.d("foo", "foo");

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("foo", "foo");
                    }
                });

            }
        }, 1000);
    }
}
