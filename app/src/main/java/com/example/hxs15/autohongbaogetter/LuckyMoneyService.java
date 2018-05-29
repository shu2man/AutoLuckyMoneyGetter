package com.example.hxs15.autohongbaogetter;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;


/**
 * Created by hxs15 on 2018-5-15.
 */


public class LuckyMoneyService extends AccessibilityService {

    private static String TAG = "LuckyMoneyService";
    private static String WECHAT_PACKAGE_NAME="com.tencent.mm";
    private static String LUCKY_MONEY_KEY_WORD="[微信红包]";

    private boolean isAutoGetter=false;
    public static Double money=0.0;
    public static int count=0;

    private SharedPreferences sharedPreferences;


    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences=getSharedPreferences("data",0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "中断抢红包服务", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String pkn = String.valueOf(event.getPackageName());

        Log.i("eventArrive","-------------------------------------------------------------");
        int eventType = event.getEventType();//事件类型
        Log.i("eventArrive","packageName:" + event.getPackageName() + "");//响应事件的包名，也就是哪个应用才响应了这个事件
        Log.i("eventArrive","source:" + event.getSource() + "");//事件源信息
        Log.i("eventArrive","source class:" + event.getClassName() + "");//事件源的类名，比如android.widget.TextView
        Log.i("eventArrive","event type(int):" + eventType + "");

        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// 通知栏事件
                Log.i("eventArrive","1-event type:TYPE_NOTIFICATION_STATE_CHANGED");
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://窗体状态改变
                Log.i("eventArrive","2-event type:TYPE_WINDOW_STATE_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED://View获取到焦点
                Log.i("eventArrive","3-event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                Log.i("eventArrive","4-event type:TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                Log.i("eventArrive","5-event type:TYPE_GESTURE_DETECTION_END");
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Log.i("eventArrive","6-event type:TYPE_WINDOW_CONTENT_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                Log.i("eventArrive","7-event type:TYPE_VIEW_CLICKED");
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                Log.i("eventArrive","8-event type:TYPE_VIEW_TEXT_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Log.i("eventArrive","9-event type:TYPE_VIEW_SCROLLED");
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                Log.i("eventArrive","10-event type:TYPE_VIEW_TEXT_SELECTION_CHANGED");
                break;
        }

        for (CharSequence txt : event.getText()) {
            Log.i("eventArrive","11-text:" + txt);//输出当前事件包含的文本信息
        }
        Log.i("eventArrive",event.getWindowId()+"");
        Log.i("eventArrive","=============================================================");

        //onLuckyMoneyEvent(event);

        if("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {
            //点中了红包，下一步就是去拆红包
            openLuckyMoney(event);
        }
        else if("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName())) {
            //拆完红包后看详情的纪录界面
            updateLuckyMoney(event);//if(isAutoGetter)

        }
        else if("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
            //在聊天界面,去点中红包
            clickChatLuckyMoney(event);
        }
        else if("android.widget.ListView".equals(event.getClassName()) && event.getEventType()==2048){
            //聊天界面有新消息
            clickChatLuckyMoney(event);
        }

    }

    private void onLuckyMoneyEvent(AccessibilityEvent event) {
        if("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {
            //点中了红包，下一步就是去拆红包
            openLuckyMoney(event);
        }
        else if("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName())) {
            //拆完红包后看详细的纪录界面
            updateLuckyMoney(event);//if(isAutoGetter)

        }
        else if("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
            //在聊天界面,去点中红包
            clickChatLuckyMoney(event);
        }
        else{

        }

        // type-2048-内容改变-有新消息
        // windowID-5876-群聊界面
        // android.widget.ListView-聊天界面

        //

    }

    public void findAndPerformActionButton(String key1,String key2){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if(nodeInfo==null) return;
        List<AccessibilityNodeInfo> nodes1=nodeInfo.findAccessibilityNodeInfosByText(key1);//领取红包
        if(nodes1==null) return;
        else if(nodes1.isEmpty()){
            List<AccessibilityNodeInfo> nodes2=nodeInfo.findAccessibilityNodeInfosByText(key2);//[微信红包]
            if(nodes2!=null&&nodes2.size()>0) nodes2.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        else{
            if(nodes1.size()>0){
                AccessibilityNodeInfo node=nodes1.get(nodes1.size()-1);
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

  /*      List<AccessibilityNodeInfo> nodeInfos=new ArrayList<>();
        for(int i=0;i<nodes1.size();i++){
            if(nodes2.contains(nodes1.get(i))) nodeInfos.add(nodes1.get(i));
        }

        if(nodeInfos.size()>0) Toast.makeText(this,"收到红包啦"+nodeInfos.size()+"-"+nodes1.size()+"-"+nodes2.size(),Toast.LENGTH_SHORT).show();
*/
    }


    private void bring2Front() {
        ActivityManager activtyManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activtyManager.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
            if (this.getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) {
                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }

    public void updateLuckyMoney(AccessibilityEvent event){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if(nodeInfo == null) return;

        List<AccessibilityNodeInfo> infos=nodeInfo.findAccessibilityNodeInfosByText("已存入零钱，可直接提现");
        if(infos!=null && infos.size()>0){
            AccessibilityNodeInfo node=infos.get(0).getParent();
            if(node.getClassName().equals("android.widget.LinearLayout")){
                if(node.getChildCount()>3){
                    try {
                        Double d=Double.parseDouble(node.getChild(2).getText().toString());//2-为红包金额
                        Double m=Double.parseDouble(sharedPreferences.getString("money","0"));
                        int c=sharedPreferences.getInt("count",0);
                        m+=d;
                        c++;

                        Message msg=new Message();
                        Bundle bundle=new Bundle();
                        bundle.putString("money",String.valueOf(m));
                        bundle.putInt("count",c);
                        msg.setData(bundle);
                        MainActivity.handler.sendMessage(msg);
                        Toast.makeText(this,"sent--->",Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("money",String.valueOf(m));
                        editor.putInt("count",c);
                        editor.apply();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        //this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }
    public void openLuckyMoney(AccessibilityEvent event){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if(nodeInfo == null) return;

        List<AccessibilityNodeInfo> nodes00=nodeInfo.findAccessibilityNodeInfosByText("手慢了，红包派完了");
        List<AccessibilityNodeInfo> nodes01=nodeInfo.findAccessibilityNodeInfosByText("该红包已超过24小时");
        if((nodes00!=null && nodes00.size()>0) || (nodes01!=null && nodes01.size()>0)) this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);

        List<AccessibilityNodeInfo> nodes=nodeInfo.findAccessibilityNodeInfosByText("開");
        if(nodes!=null&&nodes.size()>0) {
            AccessibilityNodeInfo targetNode = nodes.get(0);
            targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        else {
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                AccessibilityNodeInfo node = nodeInfo.getChild(i);
                if (node.getClassName().equals("android.widget.Button")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
            }
        }
    }

    public void clickChatLuckyMoney(AccessibilityEvent event){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if(nodeInfo==null) return;
        //假设为聊天界面，查找红包
        List<AccessibilityNodeInfo> nodes1 = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
        if(nodes1==null) return;
        //假设为最近联系人列表，查找红包
        if(nodes1.isEmpty()){
            List<AccessibilityNodeInfo> nodes2 = nodeInfo.findAccessibilityNodeInfosByText("[微信红包]");
            if(nodes2!=null && nodes2.size()>0){
                //在最近联系人列表中点击有红包的聊天记录
                if(nodes2.get(0).isClickable()) nodes2.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                else if(nodes2.get(0).getParent().isClickable()) nodes2.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        else{
            AccessibilityNodeInfo node=nodes1.get(nodes1.size()-1);
            if(node.isClickable()) node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            else if(node.getParent().isClickable()) node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }

    }


}
