package server;

public class DatabaseError extends RuntimeException {
    public DatabaseError(String msg) {
        super(msg);
    }
}
