package io.appium.uiautomator2.unittest.test;

import org.json.JSONException;
import org.json.JSONObject;

import io.appium.uiautomator2.common.exceptions.UiAutomator2Exception;
import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.model.By.ByName;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

import static android.os.SystemClock.elapsedRealtime;
import static io.appium.uiautomator2.unittest.test.TestHelper.get;
import static io.appium.uiautomator2.unittest.test.TestHelper.post;

public class TestUtil {
    private static final String baseUrl = "/wd/hub/session/:sessionId";

    public static String findElement(By by) {
        JSONObject json = new JSONObject();
        json = getJSon(by, json);
        String result = post(baseUrl + "/element", json.toString());
        Logger.info("findElement: " + result);
        return result;
    }

    public static boolean waitForElement(By by, int TIME) {
        JSONObject jsonBody = new JSONObject();
        jsonBody = getJSon(by, jsonBody);
        long start = elapsedRealtime();
        boolean foundStatus = false;
        JSONObject jsonResponse = new JSONObject();

        do {
            try {
                String response = post(baseUrl + "/element", jsonBody.toString());
                jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("status") == WDStatus.SUCCESS.code()) {
                    foundStatus = true;
                    break;
                }
            } catch (JSONException e) {
                // Retrying for element for given time
                Logger.error("Waiting for the element ..");
            }
        } while (!foundStatus && ((elapsedRealtime() - start) <= TIME));
        return foundStatus;
    }

    public static boolean waitForElementInvisible(By by, int TIME) {
        JSONObject jsonBody = new JSONObject();
        jsonBody = getJSon(by, jsonBody);
        long start = elapsedRealtime();
        boolean foundStatus = true;
        JSONObject jsonResponse = new JSONObject();

        do {
            try {
                String response = post(baseUrl + "/element", jsonBody.toString());
                jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("status") != WDStatus.SUCCESS.code()) {
                    foundStatus = false;
                    break;
                }
            } catch (JSONException e) {
                // Retrying for element for given time
                Logger.error("Waiting for the element ..");
            }
        } while (foundStatus && ((elapsedRealtime() - start) <= TIME));
        return foundStatus;
    }

    public static String findElements(By by) {
        JSONObject json = new JSONObject();
        json = getJSon(by, json);
        return post(baseUrl + "/elements", json.toString());
    }

    public static String click(String element) throws JSONException {
        String elementId;
        try {
            elementId = new JSONObject(element).getJSONObject("value").getString("ELEMENT");
        } catch (JSONException e) {
            throw new RuntimeException("Element not found");
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", elementId);
        return post(baseUrl + "/element/" + elementId + "/click", jsonObject.toString());
    }

    public static String sendKeys(String element, String text) throws JSONException {
        String elementId = new JSONObject(element).getJSONObject("value").getString("ELEMENT");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", elementId);
        jsonObject.put("text", text);
        jsonObject.put("replace", false);
        return post(baseUrl + "/element/" + elementId + "/value", jsonObject.toString());
    }

    public static String getStringValueInJsonObject(String element, String key) throws JSONException {
        return new JSONObject(element).getString(key);
    }

    public static String getText(String element) throws JSONException {
        String elementId = new JSONObject(element).getJSONObject("value").getString("ELEMENT");

        return get(baseUrl + "/element/" + elementId + "/text");
    }

    public static String getAttribute(String element, String attribute) throws JSONException {
        String elementId = new JSONObject(element).getJSONObject("value").getString("ELEMENT");

        return get(baseUrl + "/element/" + elementId + "/attribute/" + attribute);
    }

    public static String getName(String element) throws JSONException {
        String elementId = new JSONObject(element).getJSONObject("value").getString("ELEMENT");

        String response = get(baseUrl + "/element/" + elementId + "/name");
        Logger.info("Element name response:" + response);
        return response;
    }

    public static String getSize(String element) throws JSONException {
        String elementId = new JSONObject(element).getJSONObject("value").getString("ELEMENT");

        String response = get(baseUrl + "/element/" + elementId + "/size");
        Logger.info("Element Size response:" + response);
        return response;
    }

    public static String getDeviceSize() throws JSONException {

        String response = post(baseUrl + "/window/current/size", "");
        Logger.info("Device window Size response:" + response);
        return response;
    }

    public static String flickOnElement(String element) throws JSONException {
        String elementId = new JSONObject(element).getJSONObject("value").getString("ELEMENT");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", elementId);
        jsonObject.put("xoffset", 1);
        jsonObject.put("yoffset", 1);
        jsonObject.put("speed", 1000);
        String response = post(baseUrl + "/touch/flick", jsonObject.toString());
        Logger.info("Flick on element response:" + response);
        return response;
    }

    public static String flickOnPosition() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("xSpeed", 50);
        jsonObject.put("ySpeed", -180);
        String response = post(baseUrl + "/touch/flick", jsonObject.toString());
        Logger.info("Flick response:" + response);
        return response;
    }

    public static JSONObject getJSon(By by, JSONObject jsonObject) {
        try {
            if (by instanceof ByName) {
                jsonObject.put("using", "name");
                jsonObject.put("value", ((By.ByName) by).getElementLocator());
            } else if (by instanceof By.ByClass) {
                jsonObject.put("using", "class name");
                jsonObject.put("value", ((By.ByClass) by).getElementLocator());
            } else if (by instanceof By.ById) {
                jsonObject.put("using", "id");
                jsonObject.put("value", ((By.ById) by).getElementLocator());
            } else if (by instanceof By.ByXPath) {
                jsonObject.put("using", "xpath");
                jsonObject.put("value", ((By.ByXPath) by).getElementLocator());
            } else {
                throw new UiAutomator2Exception("Unable to create json object: " + by);
            }
        } catch (JSONException e) {
            Logger.error("Unable to form JSON Object: " + e.getMessage());
        }
        return jsonObject;
    }
}

