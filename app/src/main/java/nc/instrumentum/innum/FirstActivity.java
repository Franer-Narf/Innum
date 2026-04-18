package nc.instrumentum.innum;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.TimerTask;

public class FirstActivity extends AppCompatActivity {

    protected MenuItem info_button, list_button;
    protected WebView wV;
    protected Intent nextScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        wV = findViewById(R.id.webView_first);

        WebSettings wS = wV.getSettings();

        try {
            String html = readRawTextFile(R.raw.info);
            wV.loadDataWithBaseURL(
                    null,
                    html,
                    "text/html",
                    "utf-8",
                    null
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readRawTextFile(int resId) throws IOException {
        InputStream inputStream = getResources().openRawResource(resId);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        return byteStream.toString(StandardCharsets.UTF_8.name());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        info_button = menu.findItem(R.id.info_menu);
        list_button = menu.findItem(R.id.listoflist_menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int auxItem = item.getItemId();

        if (auxItem == R.id.listoflist_menu) {
            nextScreen = new Intent(FirstActivity.this, ThirdActivity.class);
            finish();
            startActivity(nextScreen);
            return true;
        } else if (auxItem == R.id.info_menu) {
            nextScreen = new Intent(FirstActivity.this, FirstActivity.class);
            finish();
            startActivity(nextScreen);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}