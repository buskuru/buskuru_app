package buskuru.buskuru_app;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;
import android.app.Service;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ek003024 on 2015/10/24.
 */
public class SimpleService extends Service {

    private final String TAG = "SimpleService";
    private Timer timer;

    private Handler mHandler;

    private int count = 0;

    private class DisplayToast implements Runnable {
        String mText;

        public DisplayToast(String text) {
            mText = text;
        }

        public void run() {
            Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_SHORT).show();
        }
    }


    /*
  * サービス初回起動時のみ実行
  *
  * @see android.app.Service#onCreate()
  */
    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();

        Log.i(TAG, "onCreate");
    }


    /*
  * サービス起動時の度に呼び出される
  *
  * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
  */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        mHandler.post(new DisplayToast("サービスを開始しました。"));

//        Intent broadcastIntent = new Intent();
//        broadcastIntent.putExtra(
//               "message", "Hello, BroadCast!");
//        broadcastIntent.setAction("MY_ACTION");
//        getBaseContext().sendBroadcast(broadcastIntent);


        // 非同期（別スレッド）で定期的に処理を実行させるためにTimerを利用する

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "onStartCommand");


                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet("http://192.168.0.103:8080/buskuru/");
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    String str = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                    Log.d("HTTP", str); // とりあえずログに表示
                    //strから次目的地を抽出
                    JSONObject json = new JSONObject(str);
                    String next = json.getString("next");
                    String wait = json.getString("wait");

//                    mHandler.post(new DisplayToast("もうすぐバスが来ますよ！" + count));

                    mHandler.post(new DisplayToast("今バスは " + next + " にいます！" +
                            "あと " + wait + " 分後に到着予定です。"));

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.putExtra(
                            "message", str);
                    broadcastIntent.setAction("MY_ACTION");
                    getBaseContext().sendBroadcast(broadcastIntent);

                } catch (Exception ex) {
                    System.out.println(ex);
                }


                count++;
            }
        }, 0, 10000);
        return START_STICKY;


    }

    /*
* サービスをバインド時に実行
  *
  * @see android.app.Service#onBind(android.content.Intent)
  */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
  * サービス停止時に実行
  *
  * @see android.app.Service#onDestroy()
  */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        // timerをキャンセル
        timer.cancel();
    }


}
