package com.erminesoft.motionview.motionview.core.bridge;

import com.erminesoft.motionview.motionview.core.command.CommandType;

public interface Receiver {
    void notify(Object data, CommandType type);
}
