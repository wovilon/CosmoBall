package wovilon.pingpong1;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    Sensor mySensor;
    SensorManager SM;
    TextView textView1;
    double phoneRot;
    MusicPlayer musicPlayer;


    void ex(float d){this.phoneRot=d; }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //textView1.setText(""+event.values[0]);


        ex(event.values[0]); //if (this.phoneRot>2) System.exit(0);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        musicPlayer=new MusicPlayer();
        musicPlayer.oncreate(this);
        musicPlayer.setVolume(1,1);
        musicPlayer.onstart();


        SM=(SensorManager)getSystemService(SENSOR_SERVICE);
        mySensor=SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);


       // Toast toast=Toast.makeText(this,text,Toast.LENGTH_SHORT);
       // toast.show();
    }


    public void onBtNewGame(View view) {
        musicPlayer.onstop();
        Intent intent=new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);

    }

    public void onHowClick(View view) {
        Intent intent=new Intent(MainActivity.this, HowToOlayActivity.class);
        startActivity(intent);
    }

    public void onSettingsClick(View view) {
        Intent intent=new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);


    }

    public void onContactsClick(View view) {
    }

    public void onExitClick(View view) {System.exit(0); }
}
