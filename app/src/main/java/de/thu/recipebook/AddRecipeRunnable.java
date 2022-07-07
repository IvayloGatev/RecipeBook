package de.thu.recipebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddRecipeRunnable implements Runnable {
    private RecipeDatabase database;
    private CreateRecipeActivity activity;

    AddRecipeRunnable(RecipeDatabase database, CreateRecipeActivity activity) {
        this.database = database;
        this.activity = activity;
    }

    @Override
    public void run() {
        synchronized (AddRecipeRunnable.this) {
            if (activity != null) {
                Recipe recipe = activity.getRecipe();
                String id = createRecipe(recipe);

                activity.runOnUiThread(() -> {
                    String toastText = "Error. The recipe couldn't be created.";
                    if (id != null) {
                        database.getRecipes().add(new Recipe(id, recipe.getName(), recipe.getCountry()));

                        toastText = "Recipe created successfully!";
                        Intent recipeDetailsIntent = new Intent(activity, RecipeDetailsActivity.class);
                        recipeDetailsIntent.putExtra("id", id);
                        activity.startActivity(recipeDetailsIntent);
                        activity.finish();
                    }

                    Toast toast = Toast.makeText(activity, toastText, Toast.LENGTH_SHORT);
                    toast.show();
                });
            }
        }
    }

    private String createRecipe(Recipe recipe) {
        String url = "http://192.168.178.21:3000/api/recipes";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).readTimeout(180, TimeUnit.SECONDS).build();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("name", recipe.getName())
                .addFormDataPart("country", recipe.getCountry())
                .addFormDataPart("ingredients", recipe.getIngredients())
                .addFormDataPart("instructions", recipe.getInstructions())
                .addFormDataPart("creatorId", recipe.getCreatorId());

        if (recipe.getImage() != null) {
            recipe.getImage().compress(Bitmap.CompressFormat.JPEG, 70, stream);
            builder.addFormDataPart("image", recipe.getName() + ".jpg",
                    RequestBody.create(stream.toByteArray(), MediaType.parse("image/*jpg")));

        }

        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        try (Response response = client.newCall(request).execute()) {
            return new JSONObject(response.body().string()).getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
