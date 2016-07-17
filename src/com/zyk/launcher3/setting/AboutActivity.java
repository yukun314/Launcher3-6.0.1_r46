package com.zyk.launcher3.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.zyk.launcher3.R;

/**
 * Created by zyk on 2016/7/17.
 */
public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init();
        initNavigation();
    }

    private void init(){

    }

    private void initNavigation(){
        View view = findViewById(R.id.activity_about_navigation);
        ImageView back = (ImageView) view.findViewById(R.id.activity_navigation_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView message = (TextView) view.findViewById(R.id.activity_navigation_message);
        message.setText(R.string.setting_about);
    }


}
