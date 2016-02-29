package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Commander {

    private final Executor mExecutor;
    private Map<CommandType, Command> commandMap;

    public Commander() {
        mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        commandMap = new EnumMap<CommandType, Command>(CommandType.class);
    }

    public void execute(ResultCallback callback, Bundle bundle) {
        CommandType type = (CommandType) bundle.getSerializable(Command.TRANSPORT_KEY);

        Command command = commandMap.get(type);
        if(command == null){
            command = CommandFactory.getCommand(type);
            commandMap.put(type, command);
        }
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
