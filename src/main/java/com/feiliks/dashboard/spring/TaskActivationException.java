package com.feiliks.dashboard.spring;

public class TaskActivationException extends Exception {

    public static class MissingDataEntity extends TaskActivationException {}

    public static class InconsistentClass extends TaskActivationException {}

    public static class NonSupportedTask extends TaskActivationException {}

    public static class RunnableNotInstantiated extends TaskActivationException {
        public RunnableNotInstantiated(Throwable e) {
            super(e);
        }
    }

    TaskActivationException() {}

    TaskActivationException(Throwable e) {
        super(e);
    }

}
