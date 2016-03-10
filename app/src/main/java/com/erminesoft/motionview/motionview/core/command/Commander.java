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
    private final Map<ExecutorType, EnumMap<CommandType, Command>> mExexutorsMap;

    public Commander(GoogleFitnessFacade googleFitnessFacade, GooglePlusFacade googlePlusFacade) {
        mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        mExexutorsMap = new EnumMap<>(ExecutorType.class);

        commandFactory = new CommandFactory(googleFitnessFacade, googlePlusFacade);
    }

    public void execute(final Bundle bundle, ExecutorType executorType) {
        EnumMap<CommandType, Command> commandMap = mExexutorsMap.get(executorType);

        if (commandMap == null) {
            commandMap = new EnumMap<>(CommandType.class);
            mExexutorsMap.put(executorType, commandMap);
        }

        CommandType commandType = (CommandType) bundle.getSerializable(Command.TRANSPORT_KEY);
        Command command = commandMap.get(commandType);

        if (command == null) {
            command = commandFactory.getCommand(commandType);
            commandMap.put(commandType, command);
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

    public void denyAll(ExecutorType type) {
        EnumMap<CommandType, Command> commandMap = mExexutorsMap.get(type);

        if (commandMap == null) {
            return;
        }

        for (Command command : commandMap.values()) {
            command.deny();
        }
    }

    private static final class CommandFactory {
        private final GoogleFitnessFacade mGoogleFitnessFacade;
        private final GooglePlusFacade mGooglePlusFacade;

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
