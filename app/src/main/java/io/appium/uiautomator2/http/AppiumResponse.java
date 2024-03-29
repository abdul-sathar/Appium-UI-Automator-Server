/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appium.uiautomator2.http;

import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.utils.Logger;
import io.netty.handler.codec.http.HttpResponseStatus;

import static io.appium.uiautomator2.utils.JSONUtils.formatNull;

public class AppiumResponse {
    private final Object value;
    private final String sessionId;
    private final HttpResponseStatus httpStatus;

    public AppiumResponse(String sessionId, @Nullable Object value) {
        this.sessionId = sessionId;
        this.value = value;
        if (value instanceof Throwable) {
            this.httpStatus = (value instanceof UiAutomator2Exception)
                    ? ((UiAutomator2Exception) value).getHttpStatus()
                    : UiAutomator2Exception.DEFAULT_ERROR_STATUS;
        } else {
            this.httpStatus = HttpResponseStatus.OK;
        }
    }

    public AppiumResponse(String sessionId) {
        this(sessionId, null);
    }

    private static JSONObject formatException(Throwable error) throws JSONException {
        UiAutomator2Exception err = (error instanceof UiAutomator2Exception)
                ? (UiAutomator2Exception) error
                : new UiAutomator2Exception(error);
        JSONObject result = new JSONObject();
        result.put("error", err.getError());
        result.put("message", err.getMessage());
        result.put("stacktrace", Log.getStackTraceString(error));
        return result;
    }

    public void renderTo(IHttpResponse response) {
        response.setContentType("application/json");
        response.setEncoding(StandardCharsets.UTF_8);
        response.setStatus(getHttpStatus().code());
        JSONObject o = new JSONObject();
        try {
            o.put("sessionId", formatNull(sessionId));
            o.put("value",
                    (value instanceof Throwable) ? formatException((Throwable) value) : formatNull(value));
            final String responseString = o.toString();
            Logger.info(String.format("AppiumResponse: %s", responseString));
            response.setContent(responseString);
        } catch (JSONException e) {
            Logger.error("Unable to create JSON Object", e);
            response.setContent("{}");
            response.setStatus(UiAutomator2Exception.DEFAULT_ERROR_STATUS.code());
        }
    }

    public HttpResponseStatus getHttpStatus() {
        return httpStatus;
    }

    @Nullable
    public Object getValue() {
        return value;
    }
}

