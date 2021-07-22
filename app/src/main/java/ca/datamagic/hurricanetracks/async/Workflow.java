package ca.datamagic.hurricanetracks.async;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.datamagic.hurricanetracks.logging.LogFactory;

public class Workflow implements WorkflowStepListener {
    private static final Logger logger = LogFactory.getLogger(Workflow.class);
    private List<WorkflowStep> steps = new ArrayList<WorkflowStep>();
    private int currentStep = 0;
    private List<WorkflowListener> listeners = new ArrayList<WorkflowListener>();

    public void addListener(WorkflowListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(WorkflowListener listener) {
        this.listeners.remove(listener);
    }

    public void fireCompleted(boolean success) {
        for (int ii = 0; ii < this.listeners.size(); ii++) {
            try {
                this.listeners.get(ii).completed(success);
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Exception calling completed on workflow listener.", t);
            }
        }
    }

    public void addStep(WorkflowStep step) {
        step.addListener(this);
        this.steps.add(step);
    }

    public void start() {
        this.currentStep = 0;
        this.steps.get(this.currentStep).execute();
    }

    @Override
    public void pass(Object sender) {
        ++this.currentStep;
        if (this.currentStep < this.steps.size()) {
            this.steps.get(this.currentStep).execute();
        } else {
            this.fireCompleted(true);
        }
    }

    @Override
    public void drop(Object sender) {
        this.fireCompleted(false);
    }
}
