package com.erminesoft.motionview.motionview.core.command;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.erminesoft.motionview.motionview.net.plus.GooglePlusFacade;
import com.erminesoft.motionview.motionview.storage.DataBuffer;
import com.google.android.gms.plus.model.people.Person;

public class GetPersonCommand extends GenericCommand {
    private static final String DATA_KEY = "intent";

    private final GooglePlusFacade mGooglePlusFacade;

    public static Bundle generateBundle(Intent data) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Command.TRANSPORT_KEY, CommandType.GET_PERSON);
        bundle.putParcelable(DATA_KEY, data);

        return bundle;
    }

    GetPersonCommand(GooglePlusFacade googlePlusFacade) {
        mGooglePlusFacade = googlePlusFacade;
    }

    @Override
    protected void execute() {
        Intent intent = getBundle().getParcelable(DATA_KEY);

        Person account = mGooglePlusFacade.getAccountInfo(intent);

        Log.i("TAG", account.getId());

        if (!isDenied()) {
            DataBuffer.getInstance().putData(account, CommandType.GET_PERSON);
        }
    }
}
