package com.centroSer.app.persistent.entities.enums;

public enum UserRole {
    ADMIN(1),
    USER(2);

    private final int id;

    UserRole(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
