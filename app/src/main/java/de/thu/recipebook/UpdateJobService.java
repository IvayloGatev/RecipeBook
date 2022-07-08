package de.thu.recipebook;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

public class UpdateJobService extends JobService {
    UpdateAsyncTask updateAsyncTask = new UpdateAsyncTask(this);

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        updateAsyncTask.execute(jobParameters);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private static class UpdateAsyncTask extends AsyncTask<JobParameters, Void, JobParameters> {
        private final JobService jobService;
        private RecipeDatabase database;
        private FetchRecipeListRunnable runnable;

        public UpdateAsyncTask(JobService service) {
            this.jobService = service;
            database = RecipeDatabase.getInstance();
            runnable = new FetchRecipeListRunnable(database, null, null);
        }

        @Override
        protected JobParameters doInBackground(JobParameters... jobParameters) {
            new Thread(runnable).start();
            return jobParameters[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            jobService.jobFinished(jobParameters, false);
        }
    }
}
