package de.thu.recipebook.runnables;

import android.provider.Settings;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import de.thu.recipebook.activities.RecipeDetailsActivity;
import de.thu.recipebook.models.Recipe;
import okhttp3.Credentials;
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
                Recipe recipe = null;
                String toastText = "Connection to the server couldn't be established.";
                try {
                    Response response = executeQuery();
                    if (response.isSuccessful()) {
                        String body = response.body().string();
                        recipe = convertResponseBodyToRecipe(body);
                    } else {
                        toastText = new JSONObject(response.body().string()).getString("message");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Recipe finalRecipe = recipe;
                String finalToastText = toastText;
                activity.runOnUiThread(() -> {
                    if (finalRecipe != null) {
                        activity.setRecipe(finalRecipe);
                    } else {
                        Toast toast = Toast.makeText(activity, finalToastText, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        }
    }

    private Response executeQuery() throws IOException {
        String id = activity.getIntent().getStringExtra("id");
        String url = "http://10.0.2.2:3000/api/recipes/" + id;
        String credentials = Credentials.basic(Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID), "");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).header("Authorization", credentials).build();

        Response response = client.newCall(request).execute();
        return response;
    }


    private Recipe convertResponseBodyToRecipe(String body) throws Exception {
        JSONObject jsonObject = new JSONObject(body);
        Recipe recipe = new Recipe(jsonObject.getString("id"), jsonObject.getString("name"),
                jsonObject.getString("country"), jsonObject.getString("ingredients"),
                jsonObject.getString("instructions"), jsonObject.getBoolean("isCreator"));

        if (jsonObject.has("image")) {
            String imageBase64 = jsonObject.getString("image");
            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            recipe.setImage(decodedString);
        }
        return recipe;
    }
}
