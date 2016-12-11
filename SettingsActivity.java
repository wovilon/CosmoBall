package wovilon.pingpong1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import static wovilon.pingpong1.R.id.effectsSound;
import static wovilon.pingpong1.R.id.generalSound;
import static wovilon.pingpong1.R.id.musicSound;
import static wovilon.pingpong1.R.string.GeneralSoundValue;

public class SettingsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    MusicPlayer musicPlayer;
    int generalSoundValue=50, musicSoundValue=50, effectsSoundValue=50;

    //TextView t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        SharedPreferences settings = getSharedPreferences("Settings", 0);
        generalSoundValue = settings.getInt("GenSnd", 0);
        musicSoundValue = settings.getInt("MusSnd", 0);
        effectsSoundValue = settings.getInt("EffSnd", 0);

        final SeekBar generalSnd=(SeekBar)findViewById(R.id.generalSound);
        final SeekBar musicSnd=(SeekBar)findViewById(musicSound);
        final SeekBar effectsSnd=(SeekBar)findViewById(effectsSound);
        generalSnd.setOnSeekBarChangeListener(this);
        musicSnd.setOnSeekBarChangeListener(this);
        effectsSnd.setOnSeekBarChangeListener(this);
        generalSnd.setProgress(generalSoundValue);
        musicSnd.setProgress(musicSoundValue);
        effectsSnd.setProgress(effectsSoundValue);
        //t=(TextView)findViewById(R.id.GeneralSoundView);

    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        SharedPreferences settings = getSharedPreferences("Settings",MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        switch(seekBar.getId()){
            case(generalSound):{
                generalSoundValue=seekBar.getProgress();
                editor.putInt("GenSnd", generalSoundValue); break;
            }
            case(musicSound):{
                musicSoundValue=seekBar.getProgress();
                editor.putInt("MusSnd", musicSoundValue); break;
            }
            case(effectsSound):{
                effectsSoundValue=seekBar.getProgress();
                editor.putInt("EffSnd", effectsSoundValue); break;
            }
         }
         editor.commit();



    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void MoveSound(Context context){

    }
}
