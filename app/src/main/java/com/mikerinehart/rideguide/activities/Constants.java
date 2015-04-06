package com.mikerinehart.rideguide.activities;

/**
 * Created by Mike on 3/13/2015.
 */
public class Constants {

    private static String googleApiProjectNumber = "690520067451";
    private static String googleServerAppApiKey = "AIzaSyBF2GCIBBUyJShxmc2n6KqwyHRBVAeVjts";
    private static String venmoApiId = "2449";
    private static String venmoAppName = "RideGuide";
    private static String venmoSecret = "BWxNqRRQ6k8xURUSkfKJVZeCVrFmqdW6";
    public static final String PREFERENCES = "com.mikerinehart.RideGuide.preferences";
    public static final String NOTIFICATIONS = "com.mikerinehart.RideGuide.notifications";
    public static final String SHOWDRAWERSHOWCASE = "drawerShowcase";
    public static final String SHOWPROFILESHOWCASE = "profileShowcase";
    public static final String SHOWSHIFTSSHOWCASE = "shiftsShowcase";
    public static final String SHOWRIDESSHOWCASE = "ridesShowcase";
    public static final String SHOWRESERVATIONSSHOWCASE = "reservationsShowcase";
    public static final String SHOWDRAWERHANDLESHOWCASE = "drawerHandleShowcase";
    public static final String SHOWNOTIFICATIONSHOWCASE = "notificationShowcase";


    static String getGoogleApiProjectNumber() {
        return googleApiProjectNumber;
    }

    public static String getGoogleServerAppApiKey() {
        return googleServerAppApiKey;
    }

    public static String getVenmoApiId() {
        return venmoApiId;
    }

    public static String getVenmoAppName() {
        return venmoAppName;
    }

    public static String getVenmoSecret() {
        return venmoSecret;
    }
}
