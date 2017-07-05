package ru.yarteleservice.mayachek.mayachek;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pavel on 28.06.17.
 */

public class MayachekService extends Service {
    String userid;
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    public MayachekService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i("Info", "--сервис чтото поделал");
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Info", "--я сервис,я стартовал");
        //оформляю подписку на изменение координат GPS
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                        LocationManager locationManager;
                        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
                        if (Build.VERSION.SDK_INT > 16) {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
                        };
        };
        SendNotif("Маячек","Сервис в фоновом режиме",1979);
        return Service.START_STICKY;
    }
    @Override
    public void onDestroy() {
        Log.i("Info", "--сервис уничтожен");
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("Info", "Service: onTaskRemoved");

    }
    //подписка на обновление местоположения
    public LocationListener locationListener = new LocationListener() {
        public void UpdateMeLocation(Location location) {
            if (isOnline() == true){
                    SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    userid = mSettings.getString("userid", "");
                    if (userid != "") {
                        if ((location.getProvider().equals(LocationManager.GPS_PROVIDER)) == true) {
                            Log.i("Info", "-service: изменилось местоположение по GPS");
                            new UpdateCoors().execute(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), "GPS");
                        };
                        if (Build.VERSION.SDK_INT > 16) {
                            if ((location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) == true) {
                                Log.i("Info", "-service: изменилось местоположение по Network");
                                new UpdateCoors().execute(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), "Network");
                            };
                        };
                    };
        };
        //поспим немножко для экономии энергии
            try {
                Log.i("Info", "-service: пошел поспать 15 секундочек");
                Thread.sleep(15000);
            } catch (InterruptedException e) {
            }
        //
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

    //отсылаем координаты
    class UpdateCoors extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg) {
            String res,dt;

            Date dateNow = new Date();
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("yyyy-MM-ddhh:mm:ss");
            dt=formatForDateNow.format(dateNow).toString().replace(" ","%20");

            BufferedReader reader = null;
            res=null;
            HttpURLConnection urlConnection = null;
            Log.i("Info", "--пробую отправлять координаты");
            try {
                URL url = new URL("http://xn--80akpf0d5b.xn--90acbu5aj5f.xn--p1ai/index.php?route=updatecoors&dt="+dt+"&userid="+userid+"&Longitude="+arg[0]+"&Latitude="+arg[1]+"&type="+arg[2]);
                Log.i("Info","http://xn--80akpf0d5b.xn--90acbu5aj5f.xn--p1ai/index.php?route=updatecoors&dt="+dt+"&userid="+userid+"&Longitude="+arg[0]+"&Latitude="+arg[1]+"&type="+arg[2]);
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
                Log.i("Info", "прочитали: "+res);
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i("Info", "--не смогли получить сообщения");
                res="-ERROR: Ошибка соединения!"+e.getLocalizedMessage();
            }
            return res;
        }
    }
    // запускаем не закрывающееся уведомление, чтоб службу труднее было остановить!
    public void SendNotif(CharSequence title,CharSequence mess,int NOTIFY_ID) {
        Context context = getApplicationContext(); //инициатор - текущая активность

        Intent notificationIntent = new Intent(context, Form1.class);
        notificationIntent.setFlags(notificationIntent.FLAG_ACTIVITY_CLEAR_TOP | notificationIntent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setTicker(mess)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(mess); // Текст уведомления

        Notification n = builder.getNotification();
        n.flags|= Notification.FLAG_NO_CLEAR;
        nm.notify(NOTIFY_ID, n);

    };
}

