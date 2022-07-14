package de.thu.recipebook.runnables;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import de.thu.recipebook.models.Recipe;
import de.thu.recipebook.databases.RecipeDatabase;
import de.thu.recipebook.activities.AddRecipeActivity;
import de.thu.recipebook.activities.RecipeDetailsActivity;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddRecipeRunnable implements Runnable {
    private RecipeDatabase database;
    private AddRecipeActivity activity;

    public AddRecipeRunnable(AddRecipeActivity activity) {
        this.database = RecipeDatabase.getInstance();
        this.activity = activity;
    }

    @Override
    public void run() {
        synchronized (AddRecipeRunnable.this) {
            if (activity != null) {
                Recipe recipe = activity.getRecipe();
                String toastText = null;
                try {
                    Response response = executeQuery(recipe);
                    if (response.isSuccessful()) {
                        String id = new JSONObject(response.body().string()).getString("id");
                        activity.runOnUiThread(() -> {
                            database.getRecipes().add(new Recipe(id, recipe.getName(), recipe.getCountry()));
                            Intent recipeDetailsIntent = new Intent(activity, RecipeDetailsActivity.class);
                            recipeDetailsIntent.putExtra("id", id);
                            activity.startActivity(recipeDetailsIntent);
                            activity.finish();
                        });
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
                });
            }
        }
    }

    private Response executeQuery(Recipe recipe) throws IOException {
        String url = "http://192.168.178.21:3000/api/recipes";
        String credentials = Credentials.basic(Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID), "");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).readTimeout(180, TimeUnit.SECONDS).build();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("name", recipe.getName())
                .addFormDataPart("country", recipe.getCountry())
                .addFormDataPart("ingredients", recipe.getIngredients())
                .addFormDataPart("instructions", recipe.getInstructions());
        if (recipe.getImage() != null) {
            recipe.getImage().compress(Bitmap.CompressFormat.JPEG, 70, stream);
            builder.addFormDataPart("image", recipe.getName() + ".jpg",
                    RequestBody.create(stream.toByteArray(), MediaType.parse("image/*jpg")));

        }

        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).header("Authorization", credentials).post(body).build();
        Response response = client.newCall(request).execute();
        return response;
    }
}
