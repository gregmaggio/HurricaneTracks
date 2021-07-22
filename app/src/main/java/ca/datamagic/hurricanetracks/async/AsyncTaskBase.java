package ca.datamagic.hurricanetracks.async;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public abstract class AsyncTaskBase<Params, Progress, Result> extends AsyncTask<Params, Progress, AsyncTaskResult<Result>> {
    private List<AsyncTaskListener<Result>> listeners = new ArrayList<AsyncTaskListener<Result>>();

    public void addListener(AsyncTaskListener<Result> listener) {
        this.listeners.add(listener);
    }

    public void removeListener(AsyncTaskListener<Result> listener) {
        this.listeners.remove(listener);
    }

    protected void fireCompleted(AsyncTaskResult<Result> result) {
        for (AsyncTaskListener<Result> listener : this.listeners) {
            try {
                listener.completed(result);
            } catch (Throwable t) {
                // TODO
            }
        }
    }
}
