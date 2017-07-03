package ru.yarteleservice.mayachek.mayachek;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Form1 extends AppCompatActivity {
    String userid;
    private WebView mbrowser;

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    //подписка на обновление местоположения
    public LocationListener locationListener = new LocationListener() {
        public void UpdateMeLocation(Location location){
            SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor mEdit = mSettings.edit();
            if ((location.getProvider().equals(LocationManager.GPS_PROVIDER))==true){
                Log.i("Info", "-изменилось местоположение по GPS");
                TextView coorgps = (TextView) findViewById(R.id.textView7);
                coorgps.setText(String.valueOf(location.getLatitude())+"\r\n"+String.valueOf(location.getLongitude()));
            };
            if ((location.getProvider().equals(LocationManager.NETWORK_PROVIDER))==true){
                Log.i("Info", "-изменилось местоположение по Network");
                TextView coornet = (TextView) findViewById(R.id.textView6);
                coornet.setText(String.valueOf(location.getLatitude())+"\r\n"+String.valueOf(location.getLongitude()));
            };
        }

        @Override
        public void onLocationChanged(Location location) {
            UpdateMeLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // UpdateMeLocation();
        }

        @Override
        public void onProviderEnabled(String provider) {
            // UpdateMeLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //UpdateMeLocation();
        }
    };
    ///////////////////////

    //нажманаем "стартовать сервис"
    public void ClickEnterNocButton(View view) {
        getApplicationContext().stopService(new Intent(getApplicationContext(), MayachekService.class));
        getApplicationContext().startService(new Intent(getApplicationContext(),MayachekService.class));
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form1);

        // спрашиваем про права на доступ к координатам
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

                Log.i("Info", "С правами на доступ к GPS/Network всё хороршо,стартую подписку на координаты");
                LocationManager locationManager;
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
                Log.i("Info", "SDK:"+Build.VERSION.SDK_INT);
            if (Build.VERSION.SDK_INT > 16) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
            };
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
            Log.i("Info", "userid не найден. Вероятно первый запуск. Пробуем получить!");
            new GetNewUserid().execute();
        } else {
            Log.i("Info", "userid:"+userid);
            TextView vuserid = (TextView) findViewById(R.id.textView);
            vuserid.setText(userid);
            //запускаем сервис ежели не запущен
            getApplicationContext().stopService(new Intent(getApplicationContext(),MayachekService.class));
            getApplicationContext().startService(new Intent(getApplicationContext(),MayachekService.class));
        };
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.get_new_id:
                Log.i("Info", "-запускаем процесс получения нового userid");
                if (isOnline()==true) {
                    new GetNewUserid().execute();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет доступа к сети интернет!", Toast.LENGTH_SHORT);
                    toast.show();
                };
            break;
            case R.id.mayachek_site:
                    Uri address2 = Uri.parse("http://xn--80akpf0d5b.xn--90acbu5aj5f.xn--p1ai");
                    Intent openlink2 = new Intent(Intent.ACTION_VIEW, address2);
                    startActivity(openlink2);
                break;
            case R.id.view_in_browser:
                if (userid!="") {
                    Uri address = Uri.parse("http://xn--80akpf0d5b.xn--90acbu5aj5f.xn--p1ai/index.php?action=viewmeonline&userid=" + userid);
                    Intent openlink = new Intent(Intent.ACTION_VIEW, address);
                    startActivity(openlink);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет идентификатора!", Toast.LENGTH_SHORT);
                    toast.show();
                };
            break;
            case R.id.view_in_map:
                Log.i("Info", "-нажали меню посмотреть на карте");
                if (isOnline()==true) {
                    if (userid!="") {
                        Log.i("Info", "-открываем в браузере");
                        mbrowser = (WebView) findViewById(R.id.webview);
                        mbrowser.getSettings().setJavaScriptEnabled(true);
                        CookieManager.getInstance().setAcceptCookie(true);
                        mbrowser.loadData("Нет подключения к интернет!", "text/html; charset=UTF-8", null);
                        mbrowser.loadUrl("http://xn--80akpf0d5b.xn--90acbu5aj5f.xn--p1ai/index.php?action=viewmeonline&userid=" + userid);
                        mbrowser.setWebViewClient(new MayachekWebViewClient());
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "Нет идентификатора!", Toast.LENGTH_SHORT);
                        toast.show();
                    };
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Нет доступа к сети интернет!", Toast.LENGTH_SHORT);
                    toast.show();
                };
                break;
        };
        return super.onOptionsItemSelected(item);
    }
    private class MayachekWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url){

        }

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
                URL url = new URL("http://xn--80akpf0d5b.xn--90acbu5aj5f.xn--p1ai/index.php?route=getnewid");
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
                //считаем что userid получили.. Записываем его!
                SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor mEdit = mSettings.edit();
                mEdit.putString("userid", String.valueOf(s));
                mEdit.commit();
                mEdit.apply();
                userid=s;
                TextView vuserid = (TextView) findViewById(R.id.textView);
                vuserid.setText(userid);
            } else {
                // иначе показываю тоаст ошибку
                Log.i("Info", "Показываю уведомление об ошибке");
                Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
                toast.show();
            };

        };
    }

}
