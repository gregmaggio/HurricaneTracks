package ca.datamagic.hurricanetracks.async;

public interface WorkflowStepListener {
    public void pass(Object sender);
    public void drop(Object sender);
}
