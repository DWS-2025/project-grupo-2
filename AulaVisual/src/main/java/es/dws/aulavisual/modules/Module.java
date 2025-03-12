package es.dws.aulavisual.modules;

import es.dws.aulavisual.courses.Course;
import jakarta.persistence.*;
import java.sql.Blob;

@Entity
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    private Course course;

    private String name;

    @Lob
    private Blob content;

    public Module(Course course, String name, Blob content) {
        this.course = course;
        this.name = name;
        this.content = content;
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

    public Blob getContent() {

        return content;
    }
}
