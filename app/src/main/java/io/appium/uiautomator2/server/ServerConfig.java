package io.appium.uiautomator2.server;


public class ServerConfig {
    private final static int PORT = 6790;

    private static boolean allowInvisibleElements = false;
    private static boolean compressedLayoutHierarchy = false;

    public static boolean isAllowInvisibleElements() {
        return allowInvisibleElements;
    }

    public static void setAllowInvisibleElements(boolean allowInvisibleElements) {
        ServerConfig.allowInvisibleElements = allowInvisibleElements;
    }

    public static boolean isCompressedLayoutHierarchy() {
        return compressedLayoutHierarchy;
    }

    public static void setCompressedLayoutHierarchy(boolean compressedLayoutHierarchy) {
        ServerConfig.compressedLayoutHierarchy = compressedLayoutHierarchy;
    }

    public static int getServerPort(){
        return PORT;
    }
}
