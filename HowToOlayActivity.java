package wovilon.pingpong1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HowToOlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_olay);
        try {getActionBar().setTitle(R.string.How_to_play);}catch (NullPointerException e){}
    }
}
