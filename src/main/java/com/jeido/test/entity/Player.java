package com.jeido.test.entity;

import java.util.UUID;

public class Player {
    private UUID id;
    private String name;

    public Player(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public Player(String name) {
        id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return String.format("'%s' #%s", name, id);
    }
}
