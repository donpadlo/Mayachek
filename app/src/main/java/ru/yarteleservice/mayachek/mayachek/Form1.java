package ru.yarteleservice.mayachek.mayachek;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Form1 extends AppCompatActivity {
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form1);

        // спрашиваем про права на доступ к координатам
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            Log.i("Info", "С правами на доступ к GPS всё хороршо");

        } else {
            Log.i("Info", "Вывожу запрос на доступ к GPS");
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    12313);
        };
        ////////////
        Log.i("Info", "Читаю userid из сохраненных настроек");
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userid=mSettings.getString("userid", "");

        if (userid==""){
            Log.i("Info", "userid не найдет. Вероятно первый запуск. Пробуем получить!");
            new GetNewUserid().execute();
        } else {
            Log.i("Info", "userid:"+userid);
            TextView vuserid = (TextView) findViewById(R.id.textView);
            vuserid.setText(userid);
            //запускаем сервис ежели не запущен
        };



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    class GetNewUserid extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            String res;
            BufferedReader reader = null;
            res=null;
            HttpURLConnection urlConnection = null;
            Log.i("Info", "Запустил http соединение для получения нового ID");
            try {
                URL url = new URL("http://маячек.грибовы.рф?route=getnewid");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                res = buffer.toString();
                Log.i("Info", "Прочитали:"+res);
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i("Info", "Соедениться не удалось..");
                res="-ERROR: "+getString(R.string.error_connections);
            }
            return res;
        };
        @Override
        protected void onPostExecute(String s) {
            if (s.contains("-ERROR")!=true) {

            } else {
                Log.i("Info", "Показываю уведомление об ошибке");
                Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
                toast.show();
            };

        };
    }

}
