package com.example.hxs15.autohongbaogetter;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.Toast;

/**
 * Created by hxs15 on 2018-6-5.
 */

public class NotificationService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification){
        Toast.makeText(this,"通知栏来红包了吗？", Toast.LENGTH_SHORT).show();
        //LuckyMoneyService.handleNotificationLuckyMoney(statusBarNotification);
    }

}
