package io.appium.uiautomator2.unittest.test;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.Configurator;

import com.jayway.jsonpath.JsonPath;
import com.squareup.okhttp.MediaType;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import io.appium.uiautomator2.model.By;
import io.appium.uiautomator2.server.ServerInstrumentation;
import io.appium.uiautomator2.server.WDStatus;
import io.appium.uiautomator2.utils.Logger;

import static io.appium.uiautomator2.unittest.test.TestUtil.click;
import static io.appium.uiautomator2.unittest.test.TestUtil.findElement;
import static io.appium.uiautomator2.unittest.test.TestUtil.findElements;
import static io.appium.uiautomator2.unittest.test.TestUtil.flickOnElement;
import static io.appium.uiautomator2.unittest.test.TestUtil.flickOnPosition;
import static io.appium.uiautomator2.unittest.test.TestUtil.getAttribute;
import static io.appium.uiautomator2.unittest.test.TestUtil.getDeviceSize;
import static io.appium.uiautomator2.unittest.test.TestUtil.getLocation;
import static io.appium.uiautomator2.unittest.test.TestUtil.getName;
import static io.appium.uiautomator2.unittest.test.TestUtil.getSize;
import static io.appium.uiautomator2.unittest.test.TestUtil.getStringValueInJsonObject;
import static io.appium.uiautomator2.unittest.test.TestUtil.getText;
import static io.appium.uiautomator2.unittest.test.TestUtil.sendKeys;
import static io.appium.uiautomator2.unittest.test.TestUtil.startActivity;
import static io.appium.uiautomator2.unittest.test.TestUtil.waitForElement;
import static io.appium.uiautomator2.unittest.test.TestUtil.waitForElementInvisible;
import static io.appium.uiautomator2.utils.Device.getUiDevice;
import static org.junit.Assert.assertEquals;
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
    private static boolean shouldStopRestOfSuite = false;
    private String response;


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
    public void launchAUT() throws InterruptedException {
        Intent intent = new Intent().setClassName(testAppPkg, testAppPkg + ".ApiDemos").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.stopService(intent);
        ctx.startActivity(intent);
        Logger.info("[AppiumUiAutomator2Server]", " waiting for app to launch ");

        TestHelper.waitForAppToLaunch(testAppPkg, 15 * SECOND);
        waitForElement(By.name("Accessibility"), 5 * SECOND);
        getUiDevice().waitForIdle();
        Logger.info("Configurator.getInstance().getWaitForSelectorTimeout:" + Configurator.getInstance().getWaitForSelectorTimeout());
    }

    /**
     * Test for click on element
     */
    @Test
    public void clickElementTest() throws JSONException {
        waitForElement(By.name("Accessibility"), 5 * SECOND);
        String element = findElement(By.name("Accessibility"));
        Logger.info("[AppiumUiAutomator2Server]", " click element:" + element);
        String result = getStringValueInJsonObject(element, "status");
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
    public void findElementTest() throws JSONException {
        waitForElement(By.name("API Demos"), 5 * SECOND);
        response = findElement(By.name("API Demos"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.name: " + response);
        String result = getStringValueInJsonObject(response, "status");
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
     * Test for findElements
     */
    @Test
    public void findElementsTest() throws JSONException, ClassNotFoundException {

        response = findElements(By.className("android.widget.TextView"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.className: " + response);
        String result = getStringValueInJsonObject(response, "status");
        assertEquals(WDStatus.SUCCESS.code(), Integer.parseInt(result));
    }

    /**
     * Test for get Attributes
     */
    @Test
    public void getAttributeTest() throws JSONException {
        String element = findElement(By.name("App"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.name: " + element);

        String result = getAttribute(element, "resourceId");
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
        String element = findElement(By.id("android:id/text1"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.className: " + element);
        String elementTxt = getText(element);
        assertEquals(getStringValueInJsonObject(elementTxt, "value"), "Accessibility");
    }

    /**
     * Test for send keys to element
     */

    @Test
    public void sendKeysTest() throws JSONException, InterruptedException {
        getUiDevice().waitForIdle();

        waitForElement(By.name("Views"), 10 * SECOND);
        click(findElement(By.name("Views")));

        waitForElement(By.name("Controls"), 10 * SECOND);
        click(findElement(By.name("Controls")));

        waitForElement(By.name("1. Light Theme"), 10 * SECOND);
        click(findElement(By.name("1. Light Theme")));

        waitForElement(By.id("io.appium.android.apis:id/edit"), 5 * SECOND);
        sendKeys(findElement(By.id("io.appium.android.apis:id/edit")), "Dummy Theme");
        assertEquals("Dummy Theme", getStringValueInJsonObject(getText(findElement(By.id("io.appium.android.apis:id/edit"))), "value"));
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
        assertEquals(getStringValueInJsonObject(response, "value"), "Accessibility");
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
        String element = findElement(By.id("io.appium.android.apis:id/start"));
        Logger.info("[AppiumUiAutomator2Server]", " findElement By.id: " + element);
        String response = getLocation(element);
        JSONObject json = new JSONObject(new JSONObject(response).get("value").toString());

        int x = JsonPath.compile("$.x").read(json.toString());
        int y = JsonPath.compile("$.y").read(json.toString());
        assertTrue("element location x coordinate is zero(0), which is not expected", x > 0);
        assertTrue("element location y coordinate is zero(0), which is not expected", y > 0);
    }
}
