package wovilon.pingpong1;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicPlayer extends MediaPlayer {
    MediaPlayer sound;

    public void oncreate(Context context){
        sound=MediaPlayer.create(context,R.raw.menusound);
        }
    public void setVolume(float left, float right) {sound.setVolume(left,right);}
    public void onstart(){sound.start();}
    public void onstop(){sound.stop();}
    public void onpause(){sound.pause();}

}
