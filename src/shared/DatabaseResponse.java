package shared;

public class DatabaseResponse {

    private final Object response;

    private final String reason;
    private final Object value;

    public DatabaseResponse(Object response, Object value, String reason) {
        this.response = response;
        this.value = value;
        this.reason = reason;
    }

    public DatabaseResponse(String response) {
        this.response = response;
        this.value = null;
        this.reason = null;
    }

    public Object getResponse() {
        return response;
    }

    public String getReason() {
        return reason;
    }

    public Object getValue() {
        return value;
    }
}
