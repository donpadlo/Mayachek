package ru.yarteleservice.mayachek.mayachek;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by pavel on 28.06.17.
 */

public class StartOnBoot extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Info", "--сервис стартовал после перезагрузки");
        Intent service = new Intent(context, MayachekService.class);
        startWakefulService(context, service);
    }
}
