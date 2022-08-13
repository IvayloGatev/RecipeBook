package de.thu.recipebook.runnables

import android.provider.Settings
import android.util.Base64
import de.thu.recipebook.activities.RecipeDetailsActivity
import org.json.JSONObject
import android.widget.Toast
import de.thu.recipebook.models.Recipe
import okhttp3.Credentials.basic
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Exception

class FetchRecipeDetailsRunnable(private val activity: RecipeDetailsActivity?) : Runnable {
    override fun run() {
        synchronized(this@FetchRecipeDetailsRunnable) {
            if (activity != null) {
                var recipe: Recipe? = null
                var toastText = "Connection to the server couldn't be established."
                try {
                    val response = executeQuery()
                    if (response.isSuccessful) {
                        val body = response.body!!.string()
                        recipe = convertResponseBodyToRecipe(body)
                    } else {
                        toastText = JSONObject(response.body!!.string()).getString("message")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val finalRecipe = recipe
                val finalToastText = toastText
                activity.runOnUiThread(Runnable {
                    if (finalRecipe != null) {
                        activity.setRecipe(finalRecipe)
                    } else {
                        val toast = Toast.makeText(activity, finalToastText, Toast.LENGTH_SHORT)
                        toast.show()
                    }
                })
            }
        }
    }

    @Throws(IOException::class)
    private fun executeQuery(): Response {
        val id = activity!!.intent.getStringExtra("id")
        val url = "http://10.0.2.2:3000/api/recipes/$id"
        val credentials = basic(
            Settings.Secure.getString(
                activity.contentResolver,
                Settings.Secure.ANDROID_ID
            ), ""
        )
        val client = OkHttpClient()
        val request: Request =
            Request.Builder().url(url).header("Authorization", credentials).build()

        return client.newCall(request).execute()
    }

    @Throws(Exception::class)
    private fun convertResponseBodyToRecipe(body: String): Recipe {
        val jsonObject = JSONObject(body)
        val recipe = Recipe(
            jsonObject.getString("id"), jsonObject.getString("name"),
            jsonObject.getString("country"), jsonObject.getString("ingredients"),
            jsonObject.getString("instructions"), jsonObject.getBoolean("isCreator")
        )

        if (jsonObject.has("image")) {
            val imageBase64 = jsonObject.getString("image")
            val decodedString = Base64.decode(imageBase64, Base64.DEFAULT)
            recipe.image = decodedString
        }
        return recipe
    }
}