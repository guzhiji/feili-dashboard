package com.feiliks.dashboard.spring;

public class TaskActivationException extends Exception {

    public static class MissingDataEntity extends TaskActivationException {}

    public static class InconsistentClass extends TaskActivationException {}

    public static class NonSupportedTask extends TaskActivationException {}

    public static class TaskAlreadyActivated extends TaskActivationException {}

    public static class MonitorNotInstantiated extends TaskActivationException {
        public MonitorNotInstantiated(Throwable e) {
            super(e);
        }
    }

    public static class TaskNotInstantiated extends TaskActivationException {
        public TaskNotInstantiated(Throwable e) {
            super(e);
        }
    }

    TaskActivationException() {}

    TaskActivationException(Throwable e) {
        super(e);
    }

}
