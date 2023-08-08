package server;

import shared.DatabaseResponse;

public interface DatabaseOperation {
    DatabaseResponse execute();
}
