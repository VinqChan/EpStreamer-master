package com.easiio.epstreamer.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.blankj.utilcode.util.NetworkUtils;
import com.easiio.epstreamer.event.NetWorkStateEvent;

import org.greenrobot.eventbus.EventBus;

public class NetworkStateReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkStateReceiver";

    private static class SingletonHolder {
        private static final NetworkStateReceiver sSingleton = new NetworkStateReceiver();
    }

    private NetworkStateReceiver() {
    }

    private static boolean sIsRegistered = false;

    public static void register(@Nullable final Context context) {
        synchronized (SingletonHolder.sSingleton) {
            Log.d(TAG, "register");
            if (context == null || sIsRegistered) {
                return;
            }
            context.registerReceiver(SingletonHolder.sSingleton, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            sIsRegistered = true;
        }
    }

    public static void unregister(@Nullable final Context context) {
        synchronized (SingletonHolder.sSingleton) {
            Log.d(TAG, "unregister");
            if (context == null || !sIsRegistered) {
                return;
            }
            sIsRegistered = false;
            context.unregisterReceiver(SingletonHolder.sSingleton);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final boolean isConnected = NetworkUtils.isConnected();
        Log.i(TAG, "onReceive,getNetworkType: " + NetworkUtils.getNetworkType()+",isConnected: " + isConnected);
        EventBus.getDefault().post(new NetWorkStateEvent(isConnected));
    }

}