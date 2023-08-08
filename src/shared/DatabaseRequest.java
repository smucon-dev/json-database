package shared;

public class DatabaseRequest {

    private final String type;
    private final String key;
    private final Object value;

    public DatabaseRequest(String type, String key, Object value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public DatabaseRequest(String type, String key) {
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

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
