package io.appium.uiautomator2.handler;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;

import static android.net.wifi.WifiManager.WIFI_STATE_DISABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLING;

public class GetWifiState extends SafeRequestHandler {

    public GetWifiState(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        WifiManager wfm = (WifiManager) InstrumentationRegistry
                .getInstrumentation().getContext().getSystemService(Context.WIFI_SERVICE);
        int wifiState = wfm.getWifiState();
        // If the WIFI state change is in progress,
        // wait until the TIMEOUT has expired
        final int TIMEOUT = 2000;
        final long then = System.currentTimeMillis();
        long now = then;
        while (wifiState == WIFI_STATE_DISABLING || wifiState == WIFI_STATE_ENABLING
                && now - then < TIMEOUT) {

            //WIFI State change is in progress, wait for completion
            SystemClock.sleep(100);
            now = System.currentTimeMillis();
            wifiState = wfm.getWifiState();
        }
        int response = (wifiState == WIFI_STATE_ENABLED) ? 1 : 0;
        return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, response);
    }
}
