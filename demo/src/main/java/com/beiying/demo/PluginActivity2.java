package com.beiying.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.beiying.plugincore.BaseActivity;

public class PluginActivity2 extends BaseActivity {
    @Override
    public void onPluginCreate(Bundle savedInstanceState) {
        super.onPluginCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin1);
        findViewById(R.id.name_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(that, PluginActivity.class));
            }
        });

    }
}
