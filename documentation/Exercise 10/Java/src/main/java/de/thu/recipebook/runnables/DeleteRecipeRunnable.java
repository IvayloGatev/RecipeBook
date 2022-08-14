package de.thu.recipebook.runnables;

import android.provider.Settings;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import de.thu.recipebook.activities.RecipeDetailsActivity;
import de.thu.recipebook.models.RecipeCollection;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DeleteRecipeRunnable implements Runnable {
    private RecipeDetailsActivity activity;
    private RecipeCollection recipeCollection;

    public DeleteRecipeRunnable(RecipeDetailsActivity activity) {
        this.activity = activity;
        this.recipeCollection = RecipeCollection.getInstance();
    }

    @Override
    public void run() {
        if (activity != null) {
            String toastText = "Connection to the server couldn't be established.";
            try {
                Response response = executeQuery();
                if (response.isSuccessful()) {
                    recipeCollection.removeRecipeById(activity.getIntent().getStringExtra("id"));
                    toastText = "The recipe was successfully deleted.";
                } else {
                    toastText = new JSONObject(response.body().string()).getString("message");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            String finalToastText = toastText;
            activity.runOnUiThread(() -> {
                Toast toast = Toast.makeText(activity, finalToastText, Toast.LENGTH_SHORT);
                toast.show();
            });
            activity.finish();
        }
    }

    private Response executeQuery() throws IOException {
        String id = activity.getIntent().getStringExtra("id");
        String url = "http://10.0.2.2:3000/api/recipes/" + id;
        String credentials = Credentials.basic(Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID), "");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).delete().header("Authorization", credentials).build();

        Response response = client.newCall(request).execute();
        return response;
    }
}
