package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;

interface Command {

    String TRANSPORT_KEY = "transport";

    void execute(Bundle bundle);

    void deny();

    boolean isRunning();
}
