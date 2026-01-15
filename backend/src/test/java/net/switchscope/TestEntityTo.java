package net.switchscope;

import net.switchscope.to.BaseTo;

import java.util.UUID;

public class TestEntityTo extends BaseTo {
    private String name;

    public TestEntityTo() {
        super();
    }

    public TestEntityTo(UUID id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}