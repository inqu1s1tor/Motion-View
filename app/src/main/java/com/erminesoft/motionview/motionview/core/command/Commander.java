package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Commander {

    private final Executor mExecutor;

    public Commander() {
        mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
    }

    public void execute(Command command, ResultCallback callback) {
        command.execute(callback);
    }

    public void execute(Command command, ResultCallback callback, Bundle bundle) {
        command.execute(callback, bundle);
    }

    public void abort(Command command) {
        command.abort();
    }

    public static final class CommandFactory {

        public static SimpleCommand getCommand(CommandType type) {
            SimpleCommand command = new SimpleCommand();

            switch (type) {
                default:
                    break;
            }

            return command;
        }
    }
}
