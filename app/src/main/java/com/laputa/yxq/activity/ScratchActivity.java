package com.laputa.yxq.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.laputa.yxq.R;

import view.ScratchView;

public class ScratchActivity extends AppCompatActivity {

    private ScratchView scratchView;
    private TextView tvPrecent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch);
        ScratchView c ;
        
        initView();
    }

    private void initView() {
        tvPrecent = (TextView) findViewById(R.id.tv_precent );
        scratchView = (ScratchView) findViewById(R.id.sv);
        scratchView.setOnEraseListener(new ScratchView.OnEraseListener() {
            @Override
            public void onProgress(int precent) {
                tvPrecent.setText(precent + "");
            }


            @Override
            public void onCompleted(View view) {


                tvPrecent.setText("完成 !!!");
            }
        });

        CompoundButton.OnCheckedChangeListener l = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()){
                    case R.id.rb_blue:
                        if (isChecked){
                            Log.e("ScratchActivity"  ,"BLUE");
                            scratchView.setMaskColor(Color.BLUE);
                        }
                        break;
                    case R.id.rb_red:
                        if (isChecked){
                            Log.e("ScratchActivity"  ,"RED");
                            scratchView.setMaskColor(Color.RED);

                        }
                        break;
                    case R.id.rb_yellow:
                        if (isChecked){
                            Log.e("ScratchActivity"  ,"YELLOW");
                            scratchView.setMaskColor(Color.YELLOW);
                        }
                        break;
                    case R.id.rb_water_on:
                        if (isChecked){
                            Log.e("ScratchActivity"  ,"ON");
                            scratchView.setWaterMark(R.drawable.ic_water);
                            scratchView.reset();
                        }
                        break;
                    case R.id.rb_water_off:
                        if (isChecked){
                            Log.e("ScratchActivity"  ,"OFF");
                            scratchView.setWaterMark(-1);
                            scratchView.reset();
                        }
                        break;
                }
                scratchView.reset();
            }
        };
        RadioButton rbRed = (RadioButton) findViewById(R.id.rb_red);
        RadioButton rbBlue = (RadioButton) findViewById(R.id.rb_blue);
        RadioButton rbYellow = (RadioButton) findViewById(R.id.rb_yellow);
        RadioButton rbWaterOn = (RadioButton) findViewById(R.id.rb_water_on);
        RadioButton rbWaterOff = (RadioButton) findViewById(R.id.rb_water_off);
        rbRed.setOnCheckedChangeListener(l);
        rbBlue.setOnCheckedChangeListener(l);
        rbYellow.setOnCheckedChangeListener(l);
        rbWaterOn.setOnCheckedChangeListener(l);
        rbWaterOff.setOnCheckedChangeListener(l);

        SeekBar.OnSeekBarChangeListener sbl = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                switch (seekBar.getId()){
                    case R.id.sb_erase:
                        scratchView.setEraseWidth(progress);
                        break;
                    case R.id.sb_precent:
                        scratchView.setMaxPrecent(progress);
                        break;
                }
            }
        };
        SeekBar sbErase  = (SeekBar) findViewById(R.id.sb_erase);
        SeekBar sbPrecent  = (SeekBar) findViewById(R.id.sb_precent);
        sbErase.setOnSeekBarChangeListener(sbl);
        sbPrecent.setOnSeekBarChangeListener(sbl);

    }



}
