package org.cordell.com.cordelldb.objects;

public class ObjectRecord {
    public ObjectRecord(String body) {
        Body = body;
    }

    private final String Body;

    public String asString() {
        return Body;
    }

    public int asInteger() {
        return Integer.parseInt(Body);
    }

    public long asLong() {
        return Long.parseLong(Body);
    }

    public float asFloat() {
        return Float.parseFloat(Body);
    }

    public double asDouble() {
        return Double.parseDouble(Body);
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(Body);
    }
}
