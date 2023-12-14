package com.example.cookmark_app.utils;
import java.util.HashMap;
import java.util.Map;

public class CookmarkStatusManager {
    private static CookmarkStatusManager instance;
    private Map<String, Boolean> cookmarkStatusMap;

    private CookmarkStatusManager() {
        cookmarkStatusMap = new HashMap<>();
    }

    public static CookmarkStatusManager getInstance() {
        if (instance == null) {
            instance = new CookmarkStatusManager();
        }
        return instance;
    }

    public boolean getCookmarkStatus(String recipeId) {
        return cookmarkStatusMap.containsKey(recipeId) && cookmarkStatusMap.get(recipeId);
    }

    public void setCookmarkStatus(String recipeId, boolean isCookmarked) {
        cookmarkStatusMap.put(recipeId, isCookmarked);
    }
}

