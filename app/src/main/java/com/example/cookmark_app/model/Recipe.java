package com.example.cookmark_app.model;

import com.google.firebase.firestore.PropertyName;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Recipe implements Serializable {
    private String recipeId;
    private String userId;
    private String recipeImage;
    private String recipeName;
    private int hours;
    private int minutes;
    private String foodType;
    private int servings;
    private ArrayList<Ingredient> ingredientList;
    private String cookingSteps;
    private String recipeURL;
    private int cookmarkCount;
    private int totalMinutes;
    private String userName;


    public Recipe(String recipeId, String userId, String recipeImage, String recipeName, int hours, int minutes,
                  String foodType, int servings, ArrayList<Ingredient> ingredientList, String cookingSteps,
                  String recipeURL, int cookmarkCount, String userName) {
        this.recipeId = recipeId;
        this.userId = userId;
        this.recipeImage = recipeImage;
        this.recipeName = recipeName;
        this.hours = hours;
        this.minutes = minutes;
        this.foodType = foodType;
        this.servings = servings;
        this.ingredientList = ingredientList;
        this.cookingSteps = cookingSteps;
        this.recipeURL = recipeURL;
        this.cookmarkCount = cookmarkCount;
        this.totalMinutes = (hours * 60) + minutes;
        this.userName = userName;
    }

    public Recipe() {
    }

    // constructor for deep copy
    public Recipe(Recipe other) {
        this.recipeId = other.getRecipeId();
        this.userId = other.getUserId();
        this.recipeImage = other.getRecipeImage();
        this.recipeName = other.getRecipeName();
        this.hours = other.getHours();
        this.minutes = other.getMinutes();
        this.foodType = other.getFoodType();
        this.servings = other.getServings();
        this.ingredientList = other.ingredientList;
        this.cookingSteps = other.getCookingSteps();
        this.recipeURL = other.getRecipeURL();
        this.cookmarkCount = other.getCookmarkCount();
        this.totalMinutes = (hours * 60) + minutes;
        this.userName = other.getUserName();
    }

    @PropertyName("id")
    public String getRecipeId() {
        return recipeId;
    }

    @PropertyName("id")
    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    @PropertyName("userId")
    public String getUserId() {
        return userId;
    }

    @PropertyName("userId")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @PropertyName("image")
    public String getRecipeImage() {
        return recipeImage;
    }

    @PropertyName("image")
    public void setRecipeImage(String recipeImage) {
        this.recipeImage = recipeImage;
    }

    @PropertyName("recipeName")
    public String getRecipeName() {
        return recipeName;
    }

    @PropertyName("recipeName")
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    @PropertyName("hours")
    public int getHours() {
        return hours;
    }
    @PropertyName("hours")
    public void setHours(int hours) {
        this.hours = hours;
    }

    @PropertyName("minutes")
    public int getMinutes() {
        return minutes;
    }

    @PropertyName("minutes")
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @PropertyName("selectedSpinnerItem")
    public String getFoodType() {
        return foodType;
    }

    @PropertyName("selectedSpinnerItem")
    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    @PropertyName("servings")
    public int getServings() {
        return servings;
    }

    @PropertyName("servings")
    public void setServings(int servings) {
        this.servings = servings;
    }

//    public ArrayList<Ingredient> getIngredientList() {
//        return ingredientList;
//    }
//    public void setIngredientList(ArrayList<Ingredient> ingredientList) {
//        this.ingredientList = ingredientList;
//    }
    @PropertyName("cookingSteps")
    public String getCookingSteps() {
        return cookingSteps;
    }

    @PropertyName("cookingSteps")
    public void setCookingSteps(String cookingSteps) {
        this.cookingSteps = cookingSteps;
    }

    @PropertyName("recipeURL")
    public String getRecipeURL() {
        return recipeURL;
    }

    @PropertyName("recipeURL")
    public void setRecipeURL(String recipeURL) {
        this.recipeURL = recipeURL;
    }

    public void setIngredientListFromString(String ingredientListString) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Ingredient>>() {}.getType();
        ingredientList = gson.fromJson(ingredientListString, type);
    }

    public String getIngredientListAsString() {
        Gson gson = new Gson();
        return gson.toJson(ingredientList);
    }

    public ArrayList<Ingredient> getIngredientListFromString(String ingredientListString) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Ingredient>>() {}.getType();
        return gson.fromJson(ingredientListString, type);
    }

    @PropertyName("cookmarkCount")
    public int getCookmarkCount() {
        return cookmarkCount;
    }
    @PropertyName("cookmarkCount")
    public void setCookmarkCount(int cookmarkCount) {
        this.cookmarkCount = cookmarkCount;
    }

    public void setIngredientListAsString(String ingredientsAsString) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Ingredient>>() {}.getType();
        this.ingredientList = gson.fromJson(ingredientsAsString, type);
    }

    public int getTotalMinutes() {
        return (hours * 60) + minutes;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

