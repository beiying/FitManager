package com.beiying.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.beiying.plugincore.BaseActivity;

public class PluginActivity extends BaseActivity {
    @Override
    public void onPluginCreate(Bundle savedInstanceState) {
        super.onPluginCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);
        TextView tv = findViewById(R.id.name_text);
        tv.setBackgroundColor(Color.RED);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("liuyu", "liuyu plugin onclick");
                startActivity(new Intent(that, PluginActivity2.class));
            }
        });
    }
}
