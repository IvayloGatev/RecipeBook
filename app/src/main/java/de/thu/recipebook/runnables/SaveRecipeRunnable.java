package de.thu.recipebook.runnables;

import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.thu.recipebook.models.Recipe;
import de.thu.recipebook.databases.RecipeDatabase;
import de.thu.recipebook.activities.SaveRecipeActivity;
import de.thu.recipebook.activities.RecipeDetailsActivity;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SaveRecipeRunnable implements Runnable {
    private RecipeDatabase database;
    private SaveRecipeActivity activity;

    public SaveRecipeRunnable(SaveRecipeActivity activity) {
        this.database = RecipeDatabase.getInstance();
        this.activity = activity;
    }

    @Override
    public void run() {
        synchronized (SaveRecipeRunnable.this) {
            if (activity != null) {
                Recipe recipe = activity.getRecipe();
                String toastText = null;
                try {
                    Response response = executeQuery(recipe);
                    if (response.isSuccessful()) {
                        String id = recipe.getId();
                        String body = response.body().string();
                        if (body != "") {
                            id = new JSONObject(body).getString("id");
                            database.getRecipes().add(new Recipe(id, recipe.getName(), recipe.getCountry()));
                        }

                        Intent recipeDetailsIntent = new Intent(activity, RecipeDetailsActivity.class);
                        recipeDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        recipeDetailsIntent.putExtra("id", id);
                        activity.startActivity(recipeDetailsIntent);
                    } else {
                        toastText = new JSONObject(response.body().string()).getString("message");
                    }
                } catch (Exception e) {
                    toastText = "Connection to the server couldn't be established.";
                    e.printStackTrace();
                }

                String finalToastText = toastText;
                activity.runOnUiThread(() -> {
                    if (finalToastText != null) {
                        Toast toast = Toast.makeText(activity, finalToastText, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    activity.finish();
                });
            }
        }
    }

    private Response executeQuery(Recipe recipe) throws IOException {
        String url = "http://192.168.178.21:3000/api/recipes";
        String credentials = Credentials.basic(Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID), "");

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).readTimeout(180, TimeUnit.SECONDS).build();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("name", recipe.getName())
                .addFormDataPart("country", recipe.getCountry())
                .addFormDataPart("ingredients", recipe.getIngredients())
                .addFormDataPart("instructions", recipe.getInstructions());
        if (recipe.getImage() != null) {
            builder.addFormDataPart("image", recipe.getName() + ".webp",
                    RequestBody.create(recipe.getImage(), MediaType.parse("image/*webp")));
        }
        RequestBody body = builder.build();
        Request request;
        if (recipe.getId() != null) {
            url = url + "/" + recipe.getId();
            request = new Request.Builder().url(url).header("Authorization", credentials).put(body).build();
        } else {
            request = new Request.Builder().url(url).header("Authorization", credentials).post(body).build();
        }
        Response response = client.newCall(request).execute();
        return response;
    }
}
