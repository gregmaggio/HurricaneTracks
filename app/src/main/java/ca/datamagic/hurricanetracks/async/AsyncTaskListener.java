package ca.datamagic.hurricanetracks.async;

public interface AsyncTaskListener<T> {
    public void completed(AsyncTaskResult<T> result);
}
