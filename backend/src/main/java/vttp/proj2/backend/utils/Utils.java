package vttp.proj2.backend.utils;

import vttp.proj2.backend.models.Platform;

public class Utils {
     public static Platform stringToPlatform(String platformStr) {
        for (Platform platform : Platform.values()) {
            if (platform.name().equalsIgnoreCase(platformStr)) {
                return platform;
            }
        }
        return Platform.OTHER; 
    }
}
