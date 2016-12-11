package wovilon.pingpong1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WinLooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_loose);

        boolean win=getIntent().getExtras().getBoolean("winloose");

        TextView winLoose=(TextView)findViewById(R.id.WinLoose);
        if (win==true) {
            winLoose.setText(R.string.YouWin);
            winLoose.setTextColor(getResources().getColor(R.color.GreenCol));

        }
        else {
            winLoose.setText(R.string.YouLoose);
            winLoose.setTextColor(getResources().getColor(R.color.RedCol));
        }
    }

    public void onPlayAgain(View view) {
        Intent intent=new Intent(WinLooseActivity.this, GameActivity.class);
        startActivity(intent);

    }

    public void onMainMenu(View view) {
        Intent intent=new Intent(WinLooseActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
