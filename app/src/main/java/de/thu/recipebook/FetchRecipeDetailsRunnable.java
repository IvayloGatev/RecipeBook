package de.thu.recipebook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchRecipeDetailsRunnable implements Runnable {
    private RecipeDetailsActivity activity;

    public FetchRecipeDetailsRunnable(RecipeDetailsActivity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        synchronized (FetchRecipeDetailsRunnable.this) {
            if (activity != null) {
                Recipe recipe = fetchRecipe();
                activity.runOnUiThread(() -> {
                    activity.setRecipe(recipe);
                });
            }
        }
    }

    private Recipe fetchRecipe() {
        String id = activity.getIntent().getStringExtra("id");
        String url = "http://192.168.178.21:3000/api/recipes/" + id;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            String body = response.body().string();
            return convertResponseBodyToRecipe(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Recipe();
    }

    private Recipe convertResponseBodyToRecipe(String body) throws Exception {
        JSONObject jsonObject = new JSONObject(body);
        Recipe recipe = new Recipe(jsonObject.getString("id"), jsonObject.getString("name"),
                jsonObject.getString("country"), jsonObject.getString("ingredients"),
                jsonObject.getString("instructions"), jsonObject.getString("creatorid"));

        if (jsonObject.has("image")) {
            String imageBase64 = jsonObject.getString("image");
            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            recipe.setImage(bitmap);
        }
        return recipe;
    }
}
