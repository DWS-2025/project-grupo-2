package es.dws.aulavisual.courses;

import java.nio.file.Path;

public class Module {

    private final long id;
    private final String name;
    private final String content;


    public Module(long id, String name, String description, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public long getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public String getContent() {

        return content;
    }
}
