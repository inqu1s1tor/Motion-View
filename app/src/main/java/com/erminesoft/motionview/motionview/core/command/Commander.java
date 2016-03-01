package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.erminesoft.motionview.motionview.net.GoogleClientFacade;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Commander {

    private final ExecutorService mExecutor;
    private Map<CommandType, Command> mCommandMap;

    public Commander(GoogleClientFacade googleClientFacade) {
        mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        mCommandMap = new EnumMap<>(CommandType.class);

        CommandFactory.setGoogleClientFacade(googleClientFacade);
    }

    public void execute(final Bundle bundle, final ResultCallback callback) {
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

    public void deny(CommandType type) {
        Command command = mCommandMap.get(type);

        if (command != null) {
            command.deny();
        }
    }

    private static final class CommandFactory {
        static GoogleClientFacade mGoogleClientFacade;

        static void setGoogleClientFacade(GoogleClientFacade googleClientFacade) {
            mGoogleClientFacade = googleClientFacade;
        }

        static GenericCommand getCommand(CommandType type) {
            GenericCommand command = new GenericCommand();

            switch (type) {
                case PROCESS_DAY_DATA:
                    command = new ProcessDayDataCommand();
                    break;
                case GENERATE_HISTORY_CHART_DATA:
                    command = new GenerateHistoryChartDataCommand();
                    break;
                case GENERATE_COMBINED_CHART_DATA:
                    command = new GenerateCombinedChartDataCommand();
                    break;
            }

            command.setGoogleClientFacade(mGoogleClientFacade);
            return command;
        }
    }
}
