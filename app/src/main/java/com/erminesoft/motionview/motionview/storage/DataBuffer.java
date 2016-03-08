package com.erminesoft.motionview.motionview.storage;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.erminesoft.motionview.motionview.core.bridge.Receiver;
import com.erminesoft.motionview.motionview.core.command.CommandType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class DataBuffer {
    private static volatile DataBuffer mInstance;
    private Map<CommandType, List<Receiver>> mReceiversMap;
    private Handler mHandler;

    private DataBuffer(){
        mReceiversMap = new EnumMap<>(CommandType.class);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static DataBuffer getInstance() {
        DataBuffer localInstance = mInstance;
        if (localInstance == null) {
            localInstance = mInstance;
            synchronized (DataBuffer.class) {
                if (localInstance == null) {
                    localInstance = mInstance = new DataBuffer();
                }
            }
        }

        return localInstance;
    }

    public void register(CommandType type, Receiver receiver) {
        List<Receiver> receivers = mReceiversMap.get(type);

        if (receivers == null) {
            receivers = new ArrayList<>();
        }

        receivers.add(receiver);
        mReceiversMap.put(type, receivers);
    }

    public void unregister(Receiver receiver) {
        for (List<Receiver> receivers : mReceiversMap.values()) {
            receivers.remove(receiver);
        }
    }

    public void putData(final Object data, final CommandType type) {
        List<Receiver> receivers = mReceiversMap.get(type);

        if (receivers == null || receivers.isEmpty()) {
            Log.e(DataBuffer.class.getSimpleName(), "No receivers for this class type - " + type);
            return;
        }

        for (final Receiver receiver : receivers) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    receiver.notify(data, type);
                }
            });
        }
    }
}