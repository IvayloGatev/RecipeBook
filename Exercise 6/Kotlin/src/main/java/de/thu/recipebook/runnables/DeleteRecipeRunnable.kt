package de.thu.recipebook.runnables

import android.provider.Settings
import android.widget.Toast
import de.thu.recipebook.activities.RecipeDetailsActivity
import de.thu.recipebook.models.RecipeCollection
import okhttp3.Credentials.basic
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class DeleteRecipeRunnable(private val activity: RecipeDetailsActivity?) : Runnable {
    private val recipeCollection: RecipeCollection = RecipeCollection.instance!!

    override fun run() {
        if (activity != null) {
            var toastText = "Connection to the server couldn't be established."
            try {
                val response = executeQuery()
                toastText = if (response.isSuccessful) {
                    recipeCollection.removeRecipeById(activity.intent.getStringExtra("id"))
                    "The recipe was successfully deleted."
                } else {
                    JSONObject(response.body!!.string()).getString("message")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val finalToastText = toastText
            activity.runOnUiThread(Runnable {
                val toast = Toast.makeText(activity, finalToastText, Toast.LENGTH_SHORT)
                toast.show()
            })
            activity.finish()
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
            Request.Builder().url(url).delete().header("Authorization", credentials).build()

        return client.newCall(request).execute()
    }
}