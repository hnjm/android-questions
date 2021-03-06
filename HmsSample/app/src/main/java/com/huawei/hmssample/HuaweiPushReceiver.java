/*
 * Copyright (C) Huawei Technologies Co., Ltd. 2016. All rights reserved.
 * See LICENSE.txt for this sample's licensing information.
 */

package com.huawei.hmssample;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.huawei.hms.support.api.push.PushReceiver;
import com.huawei.logger.Log;

/**
 * 应用需要创建一个子类继承com.huawei.hms.support.api.push.PushReceiver，
 * 实现onToken，onPushState ，onPushMsg，onEvent，这几个抽象方法，用来接收token返回，push连接状态，透传消息和通知栏点击事件处理。
 * onToken 调用getToken方法后，获取服务端返回的token结果，返回token以及belongId
 * onPushState 调用getPushState方法后，获取push连接状态的查询结果
 * onPushMsg 推送消息下来时会自动回调onPushMsg方法实现应用透传消息处理。本接口必须被实现。 在开发者网站上发送push消息分为通知和透传消息
 *           通知为直接在通知栏收到通知，通过点击可以打开网页，应用 或者富媒体，不会收到onPushMsg消息
 *           透传消息不会展示在通知栏，应用会收到onPushMsg
 * onEvent 该方法会在设置标签、点击打开通知栏消息、点击通知栏上的按钮之后被调用。由业务决定是否调用该函数。
 *
 * Application needs to create a subclass to inherit Com.huawei.hms.support.api.push.PushReceiver,
 * implement onToken，onPushState ，onPushMsg，onEvent, these several abstract methods,
 * Used to receive token, push connection status, pass through message and notification bar click event.
 * onToken calls the GetToken method, gets the token and belongid returned by the server
 * onPushState Get query results for push connection status
 * onPushMsg When a push message comes down, it will automatically callback the onPushMsg method to implement the application of the message processing.
 *           This interface must be implemented. You can send a notification bar message or a transmission message via the developer website.
 *           Notification bar message :  direct notification in the Notification column, by clicking can open the Web page, application or rich media, will not receive ONPUSHMSG message
 *           Transmission message will not be displayed in the notification bar, application will receive onpushmsg
 * onEvent This method is invoked after you set up the label, click to open the Notification bar message, and then click the button on the notification bar.
 */
public class HuaweiPushReceiver extends PushReceiver {

	public static final String TAG = "HuaweiPushReceiver";

	public static final String ACTION_UPDATEUI = "action.updateUI"; 
    @Override
    public void onToken(Context context, String token, Bundle extras) {
    	String belongId = extras.getString("belongId");
        Log.i(TAG, "belongId:" + belongId);
        Log.i(TAG, "Token:" + token);
        
        Intent intent = new Intent();  
        intent.setAction(ACTION_UPDATEUI);
        intent.putExtra("type", 1);  
        intent.putExtra("token", token);  
        context.sendBroadcast(intent);  
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
        	//开发者可以自己解析消息内容，然后做相应的处理
            //Developers can parse the content of the message themselves and then do the appropriate processing.
            String content = new String(msg, "UTF-8");
            Log.i(TAG, "Receive a push pass message with the message:" + content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
        	Log.i(TAG, "Receive notification bar message Click event, Notifyid:" + notifyId);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
        }
        
        String message = extras.getString(BOUND_KEY.pushMsgKey);
        Log.i(TAG, "message:" + message);
        super.onEvent(context, event, extras);
    }

    @Override
    public void onPushState(Context context, boolean pushState) {
    	Log.i("TAG", "Push Connection Status is:" + pushState);
    	
        Intent intent = new Intent();  
        intent.setAction(ACTION_UPDATEUI);
        intent.putExtra("type", 2); 
        intent.putExtra("pushState", pushState);  
        context.sendBroadcast(intent);  
    }
}
