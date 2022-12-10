package org.mnikitin.lesson6;

public enum SimpleProtocolResponseCode {
    OK("2:ok"),
    ERR("3:err");

    public final String description;
    SimpleProtocolResponseCode(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
