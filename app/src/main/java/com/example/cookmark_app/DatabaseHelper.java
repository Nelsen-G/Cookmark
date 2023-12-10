package com.example.cookmark_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.cookmark_app.model.Recipe;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DB_Cookmark";


    //reseppp
    private static final String TABLE_RECIPE = "recipe";
    private static final String KEY_ID = "id";
    private static final String KEY_RECIPE_NAME = "recipe_name";
    private static final String KEY_RECIPE_IMAGE = "recipe_image";
    private static final String KEY_HOURS = "hours";
    private static final String KEY_MINUTES = "minutes";
    private static final String KEY_FOOD_TYPE = "food_type";
    private static final String KEY_SERVINGS = "servings";
    private static final String KEY_INGREDIENT_LIST = "ingredient_list";
    private static final String KEY_COOKING_STEPS = "cooking_steps";
    private static final String KEY_RECIPE_URL = "recipe_url";

    private static final String CREATE_TABLE_RECIPE = "CREATE TABLE " + TABLE_RECIPE + "("
            + KEY_RECIPE_IMAGE + " TEXT,"
            + KEY_RECIPE_NAME + " TEXT,"
            + KEY_HOURS + " INTEGER,"
            + KEY_MINUTES + " INTEGER,"
            + KEY_FOOD_TYPE + " TEXT,"
            + KEY_SERVINGS + " INTEGER,"
            + KEY_INGREDIENT_LIST + " TEXT,"
            + KEY_COOKING_STEPS + " TEXT,"
            + KEY_RECIPE_URL + " TEXT"
            + ")";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user(username TEXT primary key, email TEXT, password TEXT)");
        db.execSQL(CREATE_TABLE_RECIPE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);

    }

    public Boolean insertData(String username, String email, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = db.insert("user", null, contentValues);
        return result != -1;
    }

    public void insertRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_RECIPE_IMAGE, recipe.getRecipeImage());
        values.put(KEY_RECIPE_NAME, recipe.getRecipeName());
        values.put(KEY_HOURS, recipe.getHours());
        values.put(KEY_MINUTES, recipe.getMinutes());
        values.put(KEY_FOOD_TYPE, recipe.getFoodType());
        values.put(KEY_SERVINGS, recipe.getServings());
        values.put(KEY_INGREDIENT_LIST, recipe.getIngredientListAsString());
        values.put(KEY_COOKING_STEPS, recipe.getCookingSteps());
        values.put(KEY_RECIPE_URL, recipe.getRecipeURL());


        try {
            db.insert(TABLE_RECIPE, null, values);
            Log.d("DatabaseHelper", "Recipe inserted successfully");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error insert", e);
        } finally {
            db.close();
        }
    }

    public Boolean checkUsername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username = ?", new String[]{username});

        return cursor.getCount() > 0;
    }

    public Boolean checkEmail(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE email = ?", new String[]{email});

        return cursor.getCount() > 0;
    }

    public Boolean checkUsernamePassword(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user WHERE username = ? AND password = ?", new String[]{username, password});

        return cursor.getCount() > 0;
    }

}
