package server;

import shared.DatabaseRequest;
import shared.DatabaseResponse;

public class GetOperation implements DatabaseOperation {

    private final JsonDatabase database;
    private final DatabaseRequest request;

    public GetOperation(JsonDatabase database, DatabaseRequest request) {
        this.database = database;
        this.request = request;
    }

    @Override
    public DatabaseResponse execute() {
        return database.get(request.getKey());
    }
}
