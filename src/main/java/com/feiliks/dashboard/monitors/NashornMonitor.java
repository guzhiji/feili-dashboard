package com.feiliks.dashboard.monitors;

import com.feiliks.dashboard.AbstractMonitor;

import javax.script.*;

public class NashornMonitor extends AbstractMonitor {

    public final class Task extends AbstractMonitor.Task {

        @Override
        public void run() {

            try {
                ScriptEngineManager sem = new ScriptEngineManager();
                ScriptEngine se = sem.getEngineByName("nashorn");
                Bindings b = se.getBindings(ScriptContext.ENGINE_SCOPE);
                b.put("task", this);
                se.eval("print(task.getMonitorInfo().getName())");
            } catch (ScriptException e) {
                e.printStackTrace();
            }

        }

    }

    public NashornMonitor() {
        super(NashornMonitor.class, Task.class, true);
    }

}

