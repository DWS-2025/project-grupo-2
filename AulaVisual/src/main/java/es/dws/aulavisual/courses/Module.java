package es.dws.aulavisual.courses;

import java.nio.file.Path;

public class Module {

    private final long id;
    private final String name;
    private final String description;
    private final Path content;


    public Module(long id, String name, String description, Path content) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.content = content;
    }

    public long getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public String getDescription() {

        return description;
    }

    public Path getContent() {

        return content;
    }
}
