package server;

import com.google.gson.Gson;
import shared.DatabaseResponse;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonDatabase {

    private final String dbLocation;
    private static final String OK = "OK";
    private static final String ERROR = "ERROR";
    private static final String NO_SUCH_KEY = "No such key";

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    public JsonDatabase(String dbLocation) {
        this.dbLocation = dbLocation;
    }

    public DatabaseResponse set(String key, Object value) {
        try {
            Map<String, Object> db = loadDB();
            db.put(key, value);
            saveDB(db);
            return new DatabaseResponse(OK);
        } catch (FileNotFoundException e) {
            throw new DatabaseError("Cannot open database");
        } catch (IOException e) {
            throw new DatabaseError("Cannot write to database");
        }
    }

    public DatabaseResponse get(String key) {
        try {
            Map<String, Object> db = loadDB();
            var response = db.getOrDefault(key, ERROR);
            if (ERROR.equals(response)) {
                return new DatabaseResponse(response, null, NO_SUCH_KEY);
            } else {
                return new DatabaseResponse(OK, response, null);
            }
        } catch (DatabaseError e) {
            return new DatabaseResponse(ERROR, null, e.getMessage());
        }
    }

    public DatabaseResponse delete(String key) {
        try {
            Map<String, Object> db = loadDB();
            var value = db.remove(key);
            saveDB(db);
            return value == null ? new DatabaseResponse(ERROR, null, NO_SUCH_KEY) : new DatabaseResponse(OK);
        } catch (FileNotFoundException e) {
            throw new DatabaseError("Cannot open database");
        } catch (IOException e) {
            throw new DatabaseError("Cannot write to database");
        }
    }

    private Map<String, Object> loadDB() {
        readLock.lock();
        try (var reader = new FileReader(dbLocation);) {
            return new Gson().fromJson(reader, Map.class);
        } catch (IOException e) {
            throw new DatabaseError("Could not open database.");
        } finally {
            readLock.unlock();
        }
    }

    private void saveDB(Map<String, Object> db) throws IOException {
        writeLock.lock();
        try (var writer = new FileWriter(dbLocation);) {
            new Gson().toJson(db, writer);
        } catch (IOException e) {
            throw new DatabaseError("Could not write to database.");
        } finally {
            writeLock.unlock();
        }
    }

}
