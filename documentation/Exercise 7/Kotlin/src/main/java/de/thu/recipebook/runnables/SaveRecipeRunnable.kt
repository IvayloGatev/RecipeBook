package de.thu.recipebook.runnables

import android.provider.Settings
import android.widget.Toast
import de.thu.recipebook.activities.SaveRecipeActivity
import de.thu.recipebook.models.Recipe
import de.thu.recipebook.models.RecipeCollection
import okhttp3.*
import okhttp3.Credentials.basic
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class SaveRecipeRunnable(activity: SaveRecipeActivity?) : Runnable {
    private val recipeCollection: RecipeCollection = RecipeCollection.instance!!
    private val activity: SaveRecipeActivity? = activity

    override fun run() {
        synchronized(this@SaveRecipeRunnable) {
            if (activity != null) {
                val recipe: Recipe = activity.getRecipe()!!
                var toastText: String? = null
                try {
                    val response = executeQuery(recipe)
                    if (response.isSuccessful) {
                        val body = response.body!!.string()
                        if (body.isNotEmpty()) {
                            val id = JSONObject(body).getString("id")
                            recipeCollection.getRecipes().add(
                                Recipe(
                                    id,
                                    recipe.name,
                                    recipe.country,
                                    null,
                                    null,
                                    true
                                )
                            )
                        }
                        activity.finish()
                    } else {
                        toastText = JSONObject(response.body!!.string()).getString("message")
                    }
                } catch (e: Exception) {
                    toastText = "Connection to the server couldn't be established."
                    e.printStackTrace()
                }

                val finalToastText = toastText
                activity.runOnUiThread(Runnable {
                    if (finalToastText != null) {
                        val toast =
                            Toast.makeText(activity, finalToastText, Toast.LENGTH_SHORT)
                        toast.show()
                    }
                    activity.finish()
                })
            }
        }
    }

    @Throws(IOException::class)
    private fun executeQuery(recipe: Recipe): Response {
        var url = "http://10.0.2.2:3000/api/recipes"
        val credentials = basic(
            Settings.Secure.getString(
                activity!!.contentResolver,
                Settings.Secure.ANDROID_ID
            ), ""
        )

        val client: OkHttpClient =
            OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS).build()
        val builder: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("name", recipe.name)
            .addFormDataPart("country", recipe.country)
            .addFormDataPart("ingredients", recipe.ingredients!!)
            .addFormDataPart("instructions", recipe.instructions!!)

        val body: RequestBody = builder.build()
        val request: Request
        if (recipe.id != null) {
            url = url + "/" + recipe.id
            request = Request.Builder().url(url).header("Authorization", credentials).put(body).build()
        } else {
            request = Request.Builder().url(url).header("Authorization", credentials).post(body).build()
        }

        return client.newCall(request).execute()
    }
}