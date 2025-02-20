package es.dws.aulavisual.courses;

public class Module {

    private final long id;
    private final String name;

    public Module(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {

        return id;
    }

    public String getName() {

        return name;
    }
}
