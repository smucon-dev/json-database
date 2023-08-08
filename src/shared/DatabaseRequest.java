package shared;

import java.util.List;

public class DatabaseRequest {

    private final String type;
    private final Object key;
    private final Object value;

    public DatabaseRequest(String type, Object key, Object value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public DatabaseRequest(String type, Object key) {
        this.type = type;
        this.key = key;
        this.value = null;
    }

    public DatabaseRequest(String type) {
        this.type = type;
        this.key = null;
        this.value = null;
    }

    public String getType() {
        return type;
    }

    public List getKeys() {
        if (this.key instanceof List keys) {
            return keys;
        } else {
            return List.of(this.key);
        }
    }

    public Object getValue() {
        return value;
    }
}
