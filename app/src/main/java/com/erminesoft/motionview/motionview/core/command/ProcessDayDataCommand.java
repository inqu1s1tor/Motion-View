package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.erminesoft.motionview.motionview.core.bridge.EventBridge;
import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.erminesoft.motionview.motionview.util.TimeWorker;

public class ProcessDayDataCommand extends GenericCommand {
    private static final String EVENT_BRIDGE_KEY = "eventView_KEY";
    private static final String TIMESTAMP_KEY = "timestamp";

    private final Handler mHandler;
    private EventBridge mEventView;

    ProcessDayDataCommand() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static Bundle generateBundle(EventBridge eventView,
                                        long timestamp) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(Command.TRANSPORT_KEY, CommandType.PROCESS_DAY_DATA);
        bundle.putSerializable(EVENT_BRIDGE_KEY, eventView);
        bundle.putLong(TIMESTAMP_KEY, timestamp);

        return bundle;
    }

    @Override
    public void execute(final ResultCallback callback, Bundle bundle) {
        super.execute(callback, bundle);

        if (bundle.equals(Bundle.EMPTY)) {
            callback.onError("EMPTY BUNDLE");
            return;
        }

        mEventView = (EventBridge) bundle.getSerializable(EVENT_BRIDGE_KEY);

        if (mGoogleClientFacade == null) {
            callback.onError("EMPTY GOOGLE CLIENT FACADE");
            return;
        }

        long timestamp = bundle.getLong(TIMESTAMP_KEY);

        mGoogleClientFacade.getDataPerDay(
                TimeWorker.getDay(timestamp),
                TimeWorker.getMonth(timestamp),
                TimeWorker.getYear(timestamp),
                new ResultCallback() {
                    @Override
                    public void onSuccess(final Object result) {
                        if (!isDenied()) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess(result);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
    }
}