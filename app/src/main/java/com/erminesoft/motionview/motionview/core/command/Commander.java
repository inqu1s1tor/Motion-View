package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Commander {

    private final ExecutorService mExecutor;
    private Map<CommandType, Command> mCommandMap;

    public Commander() {
        mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        mCommandMap = new EnumMap<>(CommandType.class);
    }

    public void execute(final ResultCallback callback, final Bundle bundle) {
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                CommandType type = (CommandType) bundle.getSerializable(Command.TRANSPORT_KEY);
                Command command = mCommandMap.get(type);

                if (command == null) {
                    command = CommandFactory.getCommand(type);
                    mCommandMap.put(type, command);
                }

                command.execute(callback, bundle);
            }
        });
    }

    public void abort(CommandType type) {
        Command command = mCommandMap.get(type);

        if (command != null) {
            command.deny();
        }
    }

    public static final class CommandFactory {

        public static GenericCommand getCommand(CommandType type) {
            GenericCommand command = new GenericCommand();

            switch (type) {
                case PROCESS_DAY_DATA:
                    command = new ProcessDayDataCommand();
                    break;
            }

            return command;
        }
    }
}
