package server;

import shared.DatabaseRequest;
import shared.DatabaseResponse;

public class SetOperation implements DatabaseOperation {

    private final JsonDatabase database;
    private final DatabaseRequest request;

    public SetOperation(JsonDatabase database, DatabaseRequest request) {
        this.database = database;
        this.request = request;
    }

    @Override
    public DatabaseResponse execute() {
        return database.set(request.getKeys(), request.getValue());
    }
}
