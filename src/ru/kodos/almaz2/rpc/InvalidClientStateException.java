package ru.kodos.almaz2.rpc;

import java.io.IOException;

/**
 * Created by Андрей on 13.04.2014.
 */
public class InvalidClientStateException extends IOException {
    private ClientState unfinishedState = null;
    public InvalidClientStateException(final String description) {
        super(description);
    }

    public InvalidClientStateException setUnfinishedState(ClientState unfinishedState) {
        this.unfinishedState = unfinishedState;
        return this;
    }

    public ClientState getUnfinishedState() {
        return unfinishedState;
    }

    static InvalidClientStateException negativeSize() {
        return new InvalidClientStateException("Channel read buffer which claimed to have negative size.");
    }

    static InvalidClientStateException invalidAuthorization() {
        return new InvalidClientStateException("Authorization is failed.");
    }

    static InvalidClientStateException invalidRegistration() {
        return new InvalidClientStateException("Registration is failed.");
    }

    static InvalidClientStateException finishedState() {
        return new InvalidClientStateException("Finished session state.");
    }
}
