package es.dws.aulavisual.modules;

import es.dws.aulavisual.courses.Course;
import jakarta.persistence.*;

@Entity
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Course course;

    private String name;

    public Module(Course course, String name) {
        this.course = course;
        this.name = name;
    }

    protected Module() {

    }

    public long getId() {

        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }
}
