package com.mstar004.utills.multisimsms;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;


/**
 * Created by Aamir on 25-01-2017.
 */

public class ImplSendSms {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static void sendTextMessage() {
        String SENT = "SMS_SENT";

        PendingIntent sentPI = PendingIntent.getBroadcast(Model.getContext(), 0,
                new Intent(SENT), 0);
        if (Model.getListener() != null) {
            new UtilImpl().registerReceiver(SENT, Model.getListener());
        }
        new UtilImpl().checkCondition(sentPI);
    }
}
