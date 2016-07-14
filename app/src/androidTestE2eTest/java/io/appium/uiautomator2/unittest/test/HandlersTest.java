package io.appium.uiautomator2.unittest.test;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.Configurator;

import com.jayway.jsonpath.JsonPath;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.server.ServerInstrumentation;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.unittest.test.TestHelper.getJsonObjectCountInJsonArray;
import static io.appium.uiautomator2.unittest.test.TestUtil.appStrings;
import static io.appium.uiautomator2.unittest.test.TestUtil.click;
import static io.appium.uiautomator2.unittest.test.TestUtil.findElement;
import static io.appium.uiautomator2.unittest.test.TestUtil.findElements;
import static io.appium.uiautomator2.unittest.test.TestUtil.flickOnElement;
import static io.appium.uiautomator2.unittest.test.TestUtil.flickOnPosition;
import static io.appium.uiautomator2.unittest.test.TestUtil.getAttribute;
import static io.appium.uiautomator2.unittest.test.TestUtil.getDeviceSize;
import static io.appium.uiautomator2.unittest.test.TestUtil.getLocation;
import static io.appium.uiautomator2.unittest.test.TestUtil.getName;
import static io.appium.uiautomator2.unittest.test.TestUtil.getScreenOrientation;
import static io.appium.uiautomator2.unittest.test.TestUtil.getSize;
import static io.appium.uiautomator2.unittest.test.TestUtil.getStringValueInJsonObject;
import static io.appium.uiautomator2.unittest.test.TestUtil.getText;
import static io.appium.uiautomator2.unittest.test.TestUtil.longClick;
import static io.appium.uiautomator2.unittest.test.TestUtil.multiPointerGesture;
import static io.appium.uiautomator2.unittest.test.TestUtil.rotateScreen;
import static io.appium.uiautomator2.unittest.test.TestUtil.scrollTo;
import static io.appium.uiautomator2.unittest.test.TestUtil.sendKeys;
import static io.appium.uiautomator2.unittest.test.TestUtil.startActivity;
import static io.appium.uiautomator2.unittest.test.TestUtil.swipe;
import static io.appium.uiautomator2.unittest.test.TestUtil.waitForElement;
import static io.appium.uiautomator2.unittest.test.TestUtil.waitForElementInvisible;
import static io.appium.uiautomator2.unittest.test.TestUtil.waitForSeconds;
import static io.appium.uiautomator2.utils.Device.getUiDevice;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//TODO: need to remove explicit usage of waitForElement, once after configuring setWaitForSelectorTimeout() to driver instance
//reference link: https://developer.android.com/intl/es/reference/android/support/test/uiautomator/Configurator.html#setWaitForSelectorTimeout%28long%29

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class HandlersTest {
    public static final MediaType JSON = MediaType.parse("application/json; " + "charset=utf-8");
    private static final int PORT = 8080;
    private static final String testAppPkg = "io.appium.android.apis";
    private static final int SECOND = 1000;
    private static ServerInstrumentation serverInstrumentation;
    private static Context ctx;
    private String response;
    private String element;
    private String result;


    /**
     * start io.appium.uiautomator2.server and launch the application main activity
     *
     * @throws InterruptedException
     */
    @BeforeClass
    public static void beforeStartServer() throws InterruptedException {
        if (serverInstrumentation == null) {
            assertNotNull(getUiDevice());
            ctx = InstrumentationRegistry.getInstrumentation().getContext();
            serverInstrumentation = ServerInstrumentation.getInstance(ctx, PORT);
            Logger.info("[AppiumUiAutomator2Server]", " Starting Server ");
            serverInstrumentation.startServer();
            TestHelper.waitForNetty();
            Configurator.getInstance().setWaitForSelectorTimeout(50000);
            Configurator.getInstance().setWaitForIdleTimeout(50000);
        }

    }

    @AfterClass
    public static void stopSever() throws InterruptedException {
        if (serverInstrumentation != null) {
            serverInstrumentation.stopServer();
        }
    }

    @Before
    public void launchAUT() throws InterruptedException, JSONException {
        Intent intent = new Intent().setClassName(testAppPkg, testAppPkg + ".ApiDemos").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.stopService(intent);
        ctx.startActivity(intent);
        Logger.info("[AppiumUiAutomator2Server]", " waiting for app to launch ");

        TestHelper.waitForAppToLaunch(testAppPkg, 15 * SECOND);
        waitForElement(By.name("Accessibility"), 5 * SECOND);
        getUiDevice().waitForIdle();
        Logger.info("Configurator.getInstance().getWaitForSelectorTimeout:" + Configurator.getInstance().getWaitForSelectorTimeout());
        element = findElement(By.name("Accessibility"));
        Logger.info("[AppiumUiAutomator2Server]", " click element:" + element);
        result = getStringValueInJsonObject(element, "status");
        assertEquals(WDStatus.SUCCESS.code(), Integer.parseInt(result));
    }

    /**
     * Test for click on element
     */
    @Test
    public void clickElementTest() throws JSONException {

        waitForElement(By.name("Accessibility"), 5 * SECOND);
        element = findElement(By.name("Accessibility"));
        Logger.info("[AppiumUiAutomator2Server]", " click element:" + element);
        result = getStringValueInJsonObject(element, "status");
        assertEquals(WDStatus.SUCCESS.code(), Integer.parseInt(result));
        click(element);
        getUiDevice().waitForIdle();
        waitForElementInvisible(By.name("Accessibility"), 5 * SECOND);
        element = findElement(By.name("Accessibility"));
        result = getStringValueInJsonObject(element, "status");
        assertEquals(WDStatus.NO_SUCH_ELEMENT.code(), Integer.parseInt(result));
    }

    /**
     * Test for findElement
     */
    @Test
    public void findElementTest() throws JSONException, InterruptedException {
        waitForElement(By.name("API Demos"), 5 * SECOND);
        response = findElement(By.name("API Demos"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.name: " + response);
        result = getStringValueInJsonObject(response, "status");
        assertEquals(WDStatus.SUCCESS.code(), Integer.parseInt(result));

        response = findElement(By.xpath("//*[@resource-id='android:id/action_bar']"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.id: " + response);
        result = getStringValueInJsonObject(response, "status");
        assertEquals(WDStatus.SUCCESS.code(), Integer.parseInt(result));

        response = findElement(By.xpath("(//*[@class='android.widget.TextView'][1])[2]"));
        Logger.info("[AppiumUiAutomator2Server]", "By.xpath:" + response);
        result = getAttribute(response, "text");
        assertEquals("Accessibility", getStringValueInJsonObject(result, "value"));

        response = findElement(By.xpath("//*[@resource-id='android:id/content']//*[@resource-id='android:id/text1'][4]"));
        Logger.info("[AppiumUiAutomator2Server]", "By.xpath:" + response);
        result = getAttribute(response, "text");
        assertEquals("Content", getStringValueInJsonObject(result, "value"));
    }

    /**
     * test to find element using "-android automator" property
     */
    @Test
    public void findElementUsingUiAutomatorTest() throws JSONException {
        waitForElement(By.name("API Demos"), 5 * SECOND);
        scrollTo("Views"); // Due to 'Views' option not visible on small screen
        click(findElement(By.name("Views")));

        element = findElement(By.androidUiAutomator("new UiScrollable(new UiSelector()"
                + ".resourceId(\"android:id/list\")).scrollIntoView("
                + "new UiSelector().text(\"Radio Group\"));"));

        Logger.info("[AppiumUiAutomator2Server]", " findElement By.androidUiAutomator: " + element);
        result = getStringValueInJsonObject(element, "status");
        assertEquals(WDStatus.SUCCESS.code(), Integer.parseInt(result));

        click(element);

        element = findElement(By.name("Radio Group"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.androidUiAutomator: " + element);
        result = getStringValueInJsonObject(element, "status");
        assertEquals(WDStatus.NO_SUCH_ELEMENT.code(), Integer.parseInt(result));
    }


    /**
     * test to find elements using "-android automator" property
     */
    @Test
    public void findElementsUsingUiAutomatorTest() throws JSONException {
        waitForElement(By.name("API Demos"), 5 * SECOND);
        scrollTo("Views"); // Due to 'Views' option not visible on small screen
        click(findElement(By.name("Views")));

        response = findElements(By.androidUiAutomator("resourceId(\"android:id/text1\")"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.androidUiAutomator: " + response);
        result = getStringValueInJsonObject(response, "status");
        assertEquals(WDStatus.SUCCESS.code(), Integer.parseInt(result));

        JSONArray elements = new JSONArray(getStringValueInJsonObject(response, "value"));
        int elementCount = getJsonObjectCountInJsonArray(elements);
        assertTrue("Elements Count in views screen should at least > 5, " +
                "in all variants of screen sizes, but actual: " + elementCount, elementCount > 5);

    }

    /**
     * Test for findElements
     */
    @Test
    public void findElementsTest() throws JSONException, ClassNotFoundException {

        response = findElements(By.className("android.widget.TextView"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.className: " + response);
        result = getStringValueInJsonObject(response, "status");
        assertEquals(WDStatus.SUCCESS.code(), Integer.parseInt(result));

        JSONArray elements = new JSONArray(getStringValueInJsonObject(response, "value"));
        int elementCount = getJsonObjectCountInJsonArray(elements);
        assertTrue("Elements Count in Home launch screen should at least > 5, " +
                "in all variants of screen sizes, but actual: " + elementCount, elementCount > 5);
    }

    /**
     * Test for get Attributes
     */
    @Test
    public void getAttributeTest() throws JSONException {
        element = findElement(By.name("App"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.name: " + element);

        result = getAttribute(element, "resourceId");
        Logger.info("[AppiumUiAutomator2Server]", " getAttribute: resourceId - " + result);
        assertEquals("android:id/text1", getStringValueInJsonObject(result, "value"));

        result = getAttribute(element, "contentDescription");
        assertEquals("App", getStringValueInJsonObject(result, "value"));

        result = getAttribute(element, "text");
        assertEquals("App", getStringValueInJsonObject(result, "value"));

        result = getAttribute(element, "className");
        assertEquals("android.widget.TextView", getStringValueInJsonObject(result, "value"));
    }

    /**
     * Test for getElement Text
     */
    @Test
    public void getTextTest() throws JSONException {
        element = findElement(By.id("android:id/text1"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.className: " + element);
        String elementTxt = getText(element);
        assertEquals("Accessibility", getStringValueInJsonObject(elementTxt, "value"));
    }

    /**
     * Test for send keys to element
     */

    @Test
    public void sendKeysTest() throws JSONException, InterruptedException {
        getUiDevice().waitForIdle();
        scrollTo("Views"); // Due to 'Views' option not visible on small screen

        waitForElement(By.name("Views"), 10 * SECOND);
        click(findElement(By.name("Views")));

        waitForElement(By.name("Controls"), 10 * SECOND);
        click(findElement(By.name("Controls")));

        waitForElement(By.name("1. Light Theme"), 10 * SECOND);
        click(findElement(By.name("1. Light Theme")));

        waitForElement(By.id("io.appium.android.apis:id/edit"), 5 * SECOND);
        sendKeys(findElement(By.id("io.appium.android.apis:id/edit")), "Dummy Theme");
        String enteredText = getStringValueInJsonObject(getText(findElement(By.id("io.appium.android.apis:id/edit"))), "value");
        assertEquals("Dummy Theme", enteredText);
    }

    /**
     * Test for element name
     *
     * @throws JSONException
     */
    @Test
    public void getNameTest() throws JSONException {
        getUiDevice().waitForIdle();
        waitForElement(By.id("android:id/text1"), 5 * SECOND);
        String response = getName(findElement(By.id("android:id/text1")));
        assertEquals("Accessibility", getStringValueInJsonObject(response, "value"));
    }

    /**
     * Test for element size
     *
     * @throws JSONException
     */
    @Test
    public void getElementSizeTest() throws JSONException {
        getUiDevice().waitForIdle();
        waitForElement(By.id("android:id/text1"), 5 * SECOND);
        response = getSize(findElement(By.id("android:id/text1")));
        Integer height = JsonPath.compile("$.value.height").read(response);
        Integer width = JsonPath.compile("$.value.width").read(response);
        assertTrue("Element height is zero(0), which is not expected", height > 0);
        assertTrue("Element width is zero(0), which is not expected", width > 0);
    }

    /**
     * Test for Device size
     *
     * @throws JSONException
     */
    @Test
    public void getDeviceSizeTest() throws JSONException {
        getUiDevice().waitForIdle();
        response = getDeviceSize();
        Integer height = JsonPath.compile("$.value.height").read(response);
        Integer width = JsonPath.compile("$.value.width").read(response);
        assertTrue("device window height is zero(0), which is not expected", height > 479);
        assertTrue("device window width is zero(0), which is not expected", width > 319);
    }

    /**
     * Test for flick on element
     *
     * @throws JSONException
     */
    @Test
    public void flickOnElementTest() throws JSONException {
        getUiDevice().waitForIdle();
        waitForElement(By.id("android:id/text1"), 5 * SECOND);
        response = flickOnElement(findElement(By.id("android:id/text1")));
        assertTrue(JsonPath.compile("$.value").<Boolean>read(response));
    }

    /**
     * Test for flick on device screen
     *
     * @throws JSONException
     */
    @Test
    public void flickTest() throws JSONException {
        getUiDevice().waitForIdle();
        response = flickOnPosition();
        assertTrue(JsonPath.compile("$.value").<Boolean>read(response));
    }

    /**
     * getLocation will get the location of the element on the screen
     *
     * @throws JSONException
     * @throws InterruptedException
     */

    @Test
    public void getLocationTest() throws JSONException, InterruptedException {
        startActivity(ctx, "io.appium.android.apis", ".view.ChronometerDemo");
        waitForElement(By.id("io.appium.android.apis:id/start"), 10 * SECOND);
        element = findElement(By.id("io.appium.android.apis:id/start"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.id: " + element);
        String response = getLocation(element);
        JSONObject json = new JSONObject(new JSONObject(response).get("value").toString());

        int x = JsonPath.compile("$.x").read(json.toString());
        int y = JsonPath.compile("$.y").read(json.toString());
        assertTrue("element location x coordinate is zero(0), which is not expected", x > 0);
        assertTrue("element location y coordinate is zero(0), which is not expected", y > 0);
    }

    /**
     * Performs multi pointer touch actions
     *
     * @throws InterruptedException
     * @throws JSONException
     */
    @Test
    public void multiPointerGestureTest() throws InterruptedException, JSONException {
        JSONArray actions = new JSONArray();
        startActivity(ctx, "io.appium.android.apis", ".view.ChronometerDemo");
        waitForElement(By.id("io.appium.android.apis:id/start"), 10 * SECOND);
        click(findElement(By.id("io.appium.android.apis:id/start")));
        waitForSeconds(2 * SECOND);
        String elementTxt = getText(findElement(By.id("io.appium.android.apis:id/chronometer")));
        assertNotEquals("Initial format: 00:00", getStringValueInJsonObject(elementTxt, "value"));

        String stop = findElement(By.id("io.appium.android.apis:id/stop"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.id: " + stop);
        String response = getLocation(stop);
        JSONObject json = new JSONObject(new JSONObject(response).get("value").toString());
        int x = JsonPath.compile("$.x").read(json.toString());
        int y = JsonPath.compile("$.y").read(json.toString());
        JSONObject touch1 = new JSONObject().put("x", x).put("y", y);
        JSONArray action1 = new JSONArray();
        action1.put(new JSONObject().put("time", 0.05).put("touch", touch1));


        String reset = findElement(By.id("io.appium.android.apis:id/reset"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.id: " + reset);
        response = getLocation(reset);
        json = new JSONObject(new JSONObject(response).get("value").toString());
        x = JsonPath.compile("$.x").read(json.toString());
        y = JsonPath.compile("$.y").read(json.toString());
        JSONObject touch2 = new JSONObject().put("x", x).put("y", y);
        JSONArray action2 = new JSONArray();
        action2.put(new JSONObject().put("time", 0.05).put("touch", touch2));

        /**
         * actions, e.g.:
         * [
         * [{"time": 0.005, "touch": {"y": 705, "x": 540 }}],
         * [{"time": 0.005, "touch": {"y": 561, "x": 540 }}]
         * ]
         */
        actions.put(action1).put(action2);

        response = multiPointerGesture((new JSONObject().put("actions", actions)).toString());
        Logger.info("multi touch response: " + response);
        assertEquals("OK", getStringValueInJsonObject(response, "value"));

        elementTxt = getText(findElement(By.id("io.appium.android.apis:id/chronometer")));
        assertEquals( "Initial format: 00:00", getStringValueInJsonObject(elementTxt, "value"));

    }

    /**
     * Swipes on the screen from Focus to Buttons
     *
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void swipeTest() throws JSONException, InterruptedException {
        getUiDevice().waitForIdle();
        scrollTo("Views"); // Due to 'Views' option not visible on small screen
        waitForElement(By.name("Views"), 10 * SECOND);
        click(findElement(By.name("Views")));
        waitForElement(By.name("Custom"), 10 * SECOND);
        String startElement = findElement(By.name("Custom"));
        String endElement = findElement(By.name("Buttons"));

        //Before Swipe
        String startResponse = getLocation(startElement);
        JSONObject json = new JSONObject(new JSONObject(startResponse).get("value").toString());
        int x1 = JsonPath.compile("$.x").read(json.toString());
        int y1 = JsonPath.compile("$.y").read(json.toString());

        String endResponse = getLocation(endElement);
        json = new JSONObject(new JSONObject(endResponse).get("value").toString());
        int x2 = JsonPath.compile("$.x").read(json.toString());
        int y2 = JsonPath.compile("$.y").read(json.toString());

        swipe(x1, y1, x2, y2, 1 * SECOND);

        //After Swipe
        startElement = findElement(By.name("Buttons"));
        String afterStatus = getStringValueInJsonObject(startElement, "status");

        // swipe performed hence the 'Buttons' element was not found on the screen
        assertEquals(WDStatus.NO_SUCH_ELEMENT.code(), Integer.parseInt(afterStatus));
    }

    /**
     * Performs long click action on the element
     *
     * @throws JSONException
     */
    @Test
    public void touchLongClickTest() throws JSONException {
        waitForElement(By.name("Accessibility"), 5 * SECOND);
        element = findElement(By.name("Accessibility"));
        Logger.info("[AppiumUiAutomator2Server]", "long click element:" + element);
        result = getStringValueInJsonObject(element, "status");
        assertEquals(WDStatus.SUCCESS.code(), Integer.parseInt(result));
        longClick(element);
        getUiDevice().waitForIdle();
        waitForElementInvisible(By.name("Accessibility"), 5 * SECOND);
        element = findElement(By.name("Accessibility"));
        result = getStringValueInJsonObject(element, "status");
        assertEquals(WDStatus.NO_SUCH_ELEMENT.code(), Integer.parseInt(result));
    }

    /**
     * Performs Scroll to specified element
     *
     * @throws JSONException
     * @throws InterruptedException
     */
    @Test
    public void scrollTest() throws JSONException, InterruptedException {
        getUiDevice().waitForIdle();
        scrollTo("Views"); // Due to 'Views' option not visible on small screen
        waitForElement(By.name("Views"), 10 * SECOND);
        click(findElement(By.name("Views")));
        String scrollToText = "Radio Group";
        element = findElement(By.name(scrollToText));
        String status = getStringValueInJsonObject(element, "status");
        // Before Scroll 'Radio Group' Element was not found
        assertEquals(WDStatus.NO_SUCH_ELEMENT.code(), Integer.parseInt(status));
        scrollTo(scrollToText);
        element = findElement(By.name(scrollToText));
        status = getStringValueInJsonObject(element, "status");
        // After Scroll Element was found
        assertEquals(WDStatus.SUCCESS.code(), Integer.parseInt(status));
    }

    /**
     * gets the length of the AppStrings
     *
     * @throws JSONException
     */
    @Test
    public void appStringsTest() throws JSONException {
        assertNotEquals(0, appStrings().length());
    }

    /**
     * performs screen rotation
     *
     * @throws JSONException
     */
    @Test
    public void screenRotationTest() throws JSONException {
        getUiDevice().waitForIdle();

        rotateScreen("LANDSCAPE");
        assertEquals("LANDSCAPE", getScreenOrientation());

        rotateScreen("PORTRAIT");
        assertEquals("PORTRAIT", getScreenOrientation());
    }

    /**
     * Test to verify 500 HTTP Status code for unsuccessful request
     *
     * @throws JSONException
     * @throws IOException
     */
    @Test
    public void verify_500_HTTPStatusCode() throws JSONException, IOException {
        Response response = null;
        String responseBody = null;
        int responseCode;
        response = findElement(By.id("invalid_ID"), response);

        responseBody = response.body().string();
        responseCode = response.code();
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.id: responseBody " + responseBody);
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.id: responseCode " + responseCode);

        assertEquals("HTTP Status code for unsuccessful request should be '500'.", 500, responseCode);
        assertEquals("AppiumResponse status code for element not found should be '7'.", WDStatus.NO_SUCH_ELEMENT.code(), Integer.parseInt(getStringValueInJsonObject(responseBody, "status")));
    }
}
