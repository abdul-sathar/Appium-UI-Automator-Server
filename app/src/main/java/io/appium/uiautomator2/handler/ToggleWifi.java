package io.appium.uiautomator2.handler;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;

import org.json.JSONException;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;

import static android.net.wifi.WifiManager.WIFI_STATE_DISABLING;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;
import static android.net.wifi.WifiManager.WIFI_STATE_ENABLING;

public class ToggleWifi extends SafeRequestHandler {

    private WifiManager wfm;

    public ToggleWifi(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        wfm = (WifiManager) InstrumentationRegistry
                .getInstrumentation().getContext().getSystemService(Context.WIFI_SERVICE);

        try {
            final Boolean setTo = getPayload(request).getInt("wifi") != 0 ? true : false;

            boolean status = wfm.setWifiEnabled(setTo);
            if (!status) {
                String errorMsg = "Unable to " + (setTo ? "ENABLE" : "DISABLE") + "WIFI";
                return new AppiumResponse(getSessionId(request), WDStatus.UNKNOWN_ERROR, errorMsg);
            }
            int wifiState = wfm.getWifiState();
            // If the WIFI state change is in progress,
            // wait until the TIMEOUT has expired
            final int TIMEOUT = 2000;
            final long then = System.currentTimeMillis();
            long now = then;
            while (isInProgress() || !isSuccessful(setTo) && now - then < TIMEOUT) {
                //WIFI State change is in progress, wait for completion
                SystemClock.sleep(100);
                now = System.currentTimeMillis();
                wifiState = wfm.getWifiState();
            }
            if (isInProgress() || !isSuccessful(setTo)) {
                return new AppiumResponse(getSessionId(request), WDStatus.TIMEOUT
                        , String.format("Changing WIFI State not completed in %s ms", TIMEOUT));
            }
            int response = (wifiState == WIFI_STATE_ENABLED) ? 1 : 0;
            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, response);

        } catch (JSONException e) {
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        }
    }

    private boolean isInProgress() {
        return wfm.getWifiState() == WIFI_STATE_DISABLING || wfm.getWifiState() == WIFI_STATE_ENABLING;
    }

    private boolean isSuccessful(boolean desired) {
        if (isInProgress()) {
            return false;
        } else if (desired == false && wfm.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
            return true;
        } else if (desired == true && wfm.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            return true;
        }
        return false;
    }
}
