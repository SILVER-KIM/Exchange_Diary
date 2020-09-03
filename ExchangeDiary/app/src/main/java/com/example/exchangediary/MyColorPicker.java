package com.example.exchangediary;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

public class MyColorPicker extends Activity {
    Intent intent ;
    ColorPicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_color_picker);
        this.setFinishOnTouchOutside(false);

        intent = getIntent();
        int oldColor = intent.getIntExtra("oldColor", 0);

        picker = (ColorPicker) findViewById(R.id.picker);
        SVBar svBar = (SVBar) findViewById(R.id.svbar);
        OpacityBar opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
        SaturationBar saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
        ValueBar valueBar = (ValueBar) findViewById(R.id.valuebar);

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);

        picker.getColor();

        if(oldColor != 0){
            picker.setOldCenterColor(oldColor);
        }else{
            picker.setOldCenterColor(picker.getColor());
        }
    }

    public void getColor(View view){
        intent.putExtra("color", picker.getColor());
        setResult(RESULT_OK, intent);
        finish();
    }

}


