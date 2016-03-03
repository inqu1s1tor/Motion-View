package com.erminesoft.motionview.motionview.storage;

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

    private DataBuffer(){
        mReceiversMap = new EnumMap<>(CommandType.class);
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

    public void putData(Object data, CommandType type) {
        List<Receiver> receivers = mReceiversMap.get(type);

        if (receivers == null) {
            Log.e(DataBuffer.class.getSimpleName(), "No receivers for this class type - " + type);
            return;
        }

        for (Receiver receiver : receivers) {
            receiver.notify(data, type);
        }
    }
}