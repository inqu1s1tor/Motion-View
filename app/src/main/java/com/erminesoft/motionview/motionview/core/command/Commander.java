package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;

import com.erminesoft.motionview.motionview.net.fitness.GoogleFitnessFacade;
import com.erminesoft.motionview.motionview.net.plus.GooglePlusFacade;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Commander {

    private final ExecutorService mExecutor;
    private final CommandFactory commandFactory;
    private Map<CommandType, Command> mCommandMap;

    public Commander(GoogleFitnessFacade googleFitnessFacade, GooglePlusFacade googlePlusFacade) {
        mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        mCommandMap = new EnumMap<>(CommandType.class);

        commandFactory = new CommandFactory(googleFitnessFacade, googlePlusFacade);
    }

    public void execute(final Bundle bundle) {
        CommandType type = (CommandType) bundle.getSerializable(Command.TRANSPORT_KEY);
        Command command = mCommandMap.get(type);

        if (command == null) {
            command = commandFactory.getCommand(type);
            mCommandMap.put(type, command);
        }

        if (command.isRunning()) {
            return;
        }

        final Command finalCommand = command;
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                finalCommand.execute(bundle);
            }
        });
    }

    public void denyAll() {
        for (Command command : mCommandMap.values()) {
            command.deny();
        }
    }

    private static final class CommandFactory {
        private GoogleFitnessFacade mGoogleFitnessFacade;
        private GooglePlusFacade mGooglePlusFacade;

        CommandFactory(GoogleFitnessFacade googleFitnessFacade, GooglePlusFacade googlePlusFacade) {
            mGoogleFitnessFacade = googleFitnessFacade;
            mGooglePlusFacade = googlePlusFacade;
        }

         GenericCommand getCommand(CommandType type) {
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
                case GET_PERSON:
                    command = new GetPersonCommand(mGooglePlusFacade);
                    break;
            }

            command.setGoogleFitnessFacade(mGoogleFitnessFacade);
            return command;
        }
    }
}
