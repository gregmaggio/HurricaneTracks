package ca.datamagic.hurricanetracks.async;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.logging.LogFactory;

public class WorkflowStep<Params, Progress, Result> {
    private static final Logger logger = LogFactory.getLogger(WorkflowStep.class);
    private AsyncTaskBase<Params, Progress, Result> task = null;
    private AsyncTaskListener<Result> listener = null;
    public List<WorkflowStepListener> listeners = null;

    public WorkflowStep(AsyncTaskBase<Params, Progress, Result> task, AsyncTaskListener<Result> listener) {
        this.task = task;
        this.listener = listener;
        this.listeners = new ArrayList<WorkflowStepListener>();
    }

    public void addListener(WorkflowStepListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(WorkflowStepListener listener) {
        this.listeners.remove(listener);
    }

    private void firePass() {
        for (int ii = 0; ii < this.listeners.size(); ii++) {
            try {
                this.listeners.get(ii).pass(this);
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Exception passing to a workflow step listener.", t);
            }
        }
    }

    private void fireDrop() {
        for (int ii = 0; ii < this.listeners.size(); ii++) {
            try {
                this.listeners.get(ii).drop(this);
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Exception dropping to a workflow step listener.", t);
            }
        }
    }

    public void execute() {
        this.task.addListener(new AsyncTaskListener<Result>() {
            @Override
            public void completed(AsyncTaskResult<Result> result) {
                listener.completed(result);
                firePass();
            }
        });
        this.task.execute((Params[])null);
    }
}
