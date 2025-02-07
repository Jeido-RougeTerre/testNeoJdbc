package com.jeido.test.util;

public enum CrudActions {
    CREATE("create"),
    READ_BY_ID("readById"),
    READ_ALL("readAll"),
    UPDATE("update"),
    DELETE("delete");

    private final String displayName;

    CrudActions(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
