package server;

import com.google.gson.Gson;
import shared.DatabaseResponse;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
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

    public DatabaseResponse set(List keys, Object value) {
        try {
            Map<String, Object> db = loadDB();
            if (keys.size() == 1) {
                db.put((String) keys.get(0), value);
            } else {
                Map nearestParent = findNearestParent(db, keys);
                List keysToBeInserted = findKeysToBeInserted(db, keys);
                if (keysToBeInserted.size() == 1 && value instanceof String) {
                    nearestParent.put(keysToBeInserted.get(0), value);
                } else {
                    Map child = buildChildObject(keys, value);
                    nearestParent.put(keysToBeInserted.get(0), child);
                }
            }
            saveDB(db);
            return new DatabaseResponse(OK);
        } catch (FileNotFoundException e) {
            throw new DatabaseError("Cannot open database");
        } catch (IOException e) {
            throw new DatabaseError("Cannot write to database");
        }
    }

    public DatabaseResponse get(List keys) {
        try {
            Map<String, Object> db = loadDB();
            var parent = findChildsParent(db, keys);
            var response = parent.getOrDefault(keys.get(keys.size() - 1), ERROR);
            if (ERROR.equals(response)) {
                return new DatabaseResponse(response, null, NO_SUCH_KEY);
            } else {
                return new DatabaseResponse(OK, response, null);
            }
        } catch (DatabaseError e) {
            return new DatabaseResponse(ERROR, null, e.getMessage());
        }
    }

    public DatabaseResponse delete(List keys) {
        try {
            Map<String, Object> db = loadDB();
            Object deletedObj = null;
            Map childsParent = findChildsParent(db, keys);
            deletedObj = childsParent.remove(keys.get(keys.size() - 1));
            saveDB(db);
            return deletedObj == null ? new DatabaseResponse(ERROR, null, NO_SUCH_KEY) : new DatabaseResponse(OK);
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

    private Map findChildsParent(Map base, List keys) {
        if (keys.size() > 1 && base.containsKey(keys.get(0)) && base.get(keys.get(0)) instanceof Map map) {
            return findChildsParent(map, keys.subList(1, keys.size()));
        } else if (base.containsKey(keys.get(0))) {
            return base;
        } else {
            return Map.of();
        }
    }

    private Map findNearestParent(Map base, List keys) {
        if (keys.size() > 1 && base.containsKey(keys.get(0)) && base.get(keys.get(0)) instanceof Map map) {
            return findNearestParent(map, keys.subList(1, keys.size()));
        } else {
            return base;
        }
    }

    private List findKeysToBeInserted(Map base, List keys) {
        if (keys.size() > 1 && base.containsKey(keys.get(0)) && base.get(keys.get(0)) instanceof Map map) {
            return findKeysToBeInserted(map, keys.subList(1, keys.size()));
        } else {
            return keys;
        }
    }

    private Map buildChildObject(List keys, Object value) {
        Map previousChild = Map.of(keys.get(keys.size() - 1), value);
        for (int i = keys.size() - 2; i > 1; i--) {
            previousChild = Map.of(keys.get(i), previousChild);
        }
        return previousChild;
    }

}
