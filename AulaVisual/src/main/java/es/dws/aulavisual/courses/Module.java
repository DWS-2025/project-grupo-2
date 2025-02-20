package es.dws.aulavisual.courses;

public class Module {

    private final long id;
    private final String name;
    private final String path;


    public Module(long id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public long getId() {

        return id;
    }

    public String getName() {

        return name;
    }

    public String getPath() {

        return path;
    }
}
