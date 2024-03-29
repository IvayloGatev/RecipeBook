package de.thu.recipebook.runnables;

import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.thu.recipebook.activities.RecipeListActivity;
import de.thu.recipebook.models.Recipe;
import de.thu.recipebook.models.RecipeCollection;
import de.thu.recipebook.services.UpdateRecipeListNotifier;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchRecipeListRunnable implements Runnable {
    private RecipeCollection recipeCollection;
    private RecipeListActivity activity;
    private UpdateRecipeListNotifier notifier;

    public FetchRecipeListRunnable(RecipeListActivity activity) {
        this.recipeCollection = RecipeCollection.getInstance();
        this.activity = activity;
        this.notifier = new UpdateRecipeListNotifier(activity);
    }

    @Override
    public void run() {
        synchronized (FetchRecipeListRunnable.this) {
            if (activity != null) {
                String toastText = "Connection to the server couldn't be established.";
                try {
                    Response response = executeQuery(activity.getFilterName());
                    if (response.isSuccessful()) {
                        String body = response.body().string();
                        List<Recipe> recipes = convertResponseBodyToRecipeList(body);
                        recipeCollection.setRecipes(recipes);
                        toastText = "The recipes were fetched successfully.";
                        notifier.showOrUpdateNotification("Recipe list updated.");
                    } else {
                        toastText = new JSONObject(response.body().string()).getString("message");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String finalToastText = toastText;
                activity.runOnUiThread(() -> {
                    activity.notifyDataSetChanged();
                    Toast toast = Toast.makeText(activity, finalToastText, Toast.LENGTH_SHORT);
                    toast.show();
                });
            }
        }
    }

    private Response executeQuery(String nameFilter) throws IOException {
        String url = "http://10.0.2.2:3000/api/recipes";
        String credentials = Credentials.basic(Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID), "");

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
        if (nameFilter != null && !nameFilter.isEmpty()) {
            httpBuilder.addQueryParameter("name", nameFilter);
        }

        Request request = new Request.Builder().url(httpBuilder.build()).header("Authorization", credentials).build();
        Response response = client.newCall(request).execute();
        return response;

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
