package buskuru.buskuru_app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // HTTPボタン押下
    public void onBtnHttpClicked(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet("http://192.168.0.103:8080/buskuru/");
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    String str = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                    Log.d("HTTP", str); // とりあえずログに表示
                    //strから次目的地を抽出
                    JSONObject json = new JSONObject(str);
                    String next = json.getString("next");
                    TextView textView = (TextView) findViewById(R.id.textView1);
                    textView.setText(next);

                } catch(Exception ex) {
                    System.out.println(ex);
                }
            }
        }).start();
    }


}
