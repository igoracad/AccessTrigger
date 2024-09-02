package com.example.accesstrigger;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class MyAccessibilityService extends AccessibilityService {

    private static MyAccessibilityService instance;
    private boolean isChromeOpened = false;
    private boolean backButtonClicked = false;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (!isChromeOpened) {
                Log.i("AccessibilityService", "Window state changed event detected.");
                clickAppHomeScreen();
            }
        }

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isChromeOpened = false;
    }

    public static void clickAppHomeScreen() {
        if (instance != null) {
            boolean success = instance.performGlobalAction(GLOBAL_ACTION_HOME);
            if (success) {
                Log.i("AccessibilityService", "Home button pressed successfully.");
                instance.clickChromeIcon();
            } else {
                Log.e("AccessibilityService", "Failed to press the home button.");
            }
        } else {
            Log.e("AccessibilityService", "AccessibilityService instance is null.");
        }
    }

    // Method to find and click the Chrome icon on the home screen
    private void clickChromeIcon() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            AccessibilityNodeInfo chromeNode = findChromeIcon(rootNode);
            if (chromeNode != null) {
                chromeNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.i("AccessibilityService", "Chrome icon clicked successfully.");
                isChromeOpened = true;
            } else {
                Log.e("AccessibilityService", "Chrome icon not found.");
            }
        } else {
            Log.e("AccessibilityService", "Root node is null.");
        }
    }

    // Method to recursively find the Chrome icon by its label or content description
    private AccessibilityNodeInfo findChromeIcon(AccessibilityNodeInfo node) {
        if (node == null) return null;

        // Check if the current node is the Chrome icon
        if ("Chrome".equals(node.getContentDescription()) || "Chrome".equals(node.getText())) {
            return node;
        }

        // Recursively search in child nodes
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo result = findChromeIcon(node.getChild(i));
            if (result != null) return result;
        }

        return null;
    }
}
