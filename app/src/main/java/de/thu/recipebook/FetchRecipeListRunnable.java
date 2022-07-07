package de.thu.recipebook;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchRecipeListRunnable implements Runnable {
    private RecipeDatabase database;
    private Activity activity;
    private RecipeListAdapter adapter;

    FetchRecipeListRunnable(RecipeDatabase database, Activity activity, RecipeListAdapter adapter) {
        this.database = database;
        this.activity = activity;
        this.adapter = adapter;
    }


    @Override
    public void run() {
        synchronized (FetchRecipeListRunnable.this) {
            database.setRecipes(fetchRecipeList());
            if (activity != null) {
                adapter.setData(database.getRecipes());
                activity.runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                    Toast toast = Toast.makeText(activity, "Recipe List Updated!", Toast.LENGTH_SHORT);
                    toast.show();
                });
            }
        }
    }

    private List<Recipe> fetchRecipeList() {
        String url = "http://192.168.178.21:3000/api/recipes";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            return convertResponseBodyToRecipeList(body);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private List<Recipe> convertResponseBodyToRecipeList(String body) throws Exception {
        List<Recipe> recipes = new ArrayList<>();

        JSONArray recipesJsonArray = new JSONArray(body);
        for (int i = 0; i < recipesJsonArray.length(); i++) {
            JSONObject properties = recipesJsonArray.getJSONObject(i);
            Log.i("JSON", properties.toString());
            recipes.add(new Recipe(properties.getString("id"), properties.getString("name"), properties.getString("country")));
        }

        return recipes;
    }

}
