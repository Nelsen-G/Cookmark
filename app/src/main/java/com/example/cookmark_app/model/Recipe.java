package com.example.cookmark_app.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Recipe {

    private String recipeImage;
    private String recipeName;
    private int hours;
    private int minutes;
    private String foodType;
    private int servings;
    private ArrayList<Ingredient> ingredientList;
    private String cookingSteps;
    private String recipeURL;

    public Recipe(String recipeImage, String recipeName, int hours, int minutes, String foodType,
                  int servings, ArrayList<Ingredient> ingredientList, String cookingSteps, String recipeURL) {
        this.recipeImage = recipeImage;
        this.recipeName = recipeName;
        this.hours = hours;
        this.minutes = minutes;
        this.foodType = foodType;
        this.servings = servings;
        this.ingredientList = ingredientList;
        this.cookingSteps = cookingSteps;
        this.recipeURL = recipeURL;
    }


    public String getRecipeImage() {
        return recipeImage;
    }

    public void setRecipeImage(String recipeImage) {
        this.recipeImage = recipeImage;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public ArrayList<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(ArrayList<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public String getCookingSteps() {
        return cookingSteps;
    }

    public void setCookingSteps(String cookingSteps) {
        this.cookingSteps = cookingSteps;
    }

    public String getRecipeURL() {
        return recipeURL;
    }

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

}

