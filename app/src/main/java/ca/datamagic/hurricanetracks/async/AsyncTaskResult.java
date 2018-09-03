package ca.datamagic.hurricanetracks.async;

public class AsyncTaskResult<T> {
    private T _result = null;
    private Throwable _throwable = null;

    public AsyncTaskResult() {
        super();
    }

    public AsyncTaskResult(T result) {
        super();
        _result = result;
    }

    public AsyncTaskResult(Throwable throwable) {
        super();
        _throwable = throwable;
    }

    public T getResult() {
        return _result;
    }

    public Throwable getThrowable() {
        return _throwable;
    }
}
