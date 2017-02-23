package io.appium.uiautomator2.handler;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.view.MotionEvent;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.handler.request.SafeRequestHandler;
import io.appium.uiautomator2.http.AppiumResponse;
import io.appium.uiautomator2.http.IHttpRequest;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;
import io.appium.uiautomator2.utils.Point;

/**
 * Low-level implementation for scroll action with coordinates
 */
public class DefiniteScrolling extends SafeRequestHandler {

    public DefiniteScrolling(String mappedUri) {
        super(mappedUri);
    }

    @Override
    public AppiumResponse safeHandle(IHttpRequest request) {
        Logger.info("Definite scrolling");
        try {
            PageUpDownArguments arguments = new PageUpDownArguments(request);
            Logger.info(arguments);

            Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis();
            MotionEvent downEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, arguments.start.x.intValue(), arguments.start.y.intValue(), 0);
            instrumentation.sendPointerSync(downEvent);

            eventTime = SystemClock.uptimeMillis() + 500;
            MotionEvent moveEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, arguments.end.x.intValue(), arguments.end.y.intValue(), 0);
            instrumentation.sendPointerSync(moveEvent);

            eventTime = SystemClock.uptimeMillis() + 500;
            //After calling ACTION_UP move event can scroll a little bit. To prevent this should call ACTION_CANCEL
            MotionEvent upEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_CANCEL, arguments.end.x.intValue(), arguments.end.y.intValue(), 0);
            instrumentation.sendPointerSync(upEvent);

            return new AppiumResponse(getSessionId(request), WDStatus.SUCCESS, true);
        } catch (JSONException e) {
            Logger.error("Exception while reading JSON: ", e);
            return new AppiumResponse(getSessionId(request), WDStatus.JSON_DECODER_ERROR, e);
        }
    }

    private class PageUpDownArguments {
        final Point start;
        final Point end;

        PageUpDownArguments(final IHttpRequest request) throws JSONException {
            JSONObject payload = getPayload(request);
            start = new Point(payload.get("startX"), payload.get("startY"));
            end = new Point(payload.get("endX"), payload.get("endY"));
        }

        @Override
        public String toString() {
            return "PageUpDownArguments{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }
}
