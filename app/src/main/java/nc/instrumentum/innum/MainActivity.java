package nc.instrumentum.innum;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    protected TextView title_main;
    protected ImageView logo_main;

    protected Intent nextScreen;
    protected Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        title_main = (TextView) findViewById(R.id.title_main);
        logo_main = (ImageView) findViewById(R.id.logo_main);

        TimerTask myTimerTask = new TimerTask() {
            @Override public void run() {

            nextScreen= new Intent(MainActivity.this, SecondActivity.class);
            finish();
            startActivity(nextScreen);
            }
        };

        timer = new Timer();
        timer.schedule(myTimerTask, 1300);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}