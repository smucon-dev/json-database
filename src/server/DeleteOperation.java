package server;

import shared.DatabaseRequest;
import shared.DatabaseResponse;

public class DeleteOperation implements DatabaseOperation {

    private final JsonDatabase database;
    private final DatabaseRequest request;

    public DeleteOperation(JsonDatabase database, DatabaseRequest request) {
        this.database = database;
        this.request = request;
    }

    @Override
    public DatabaseResponse execute() {
        return database.delete(request.getKeys());
    }
}
