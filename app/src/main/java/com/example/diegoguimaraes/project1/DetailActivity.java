package com.example.diegoguimaraes.project1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_detail, new DetailFragment())
                    .commit();
    }
}
