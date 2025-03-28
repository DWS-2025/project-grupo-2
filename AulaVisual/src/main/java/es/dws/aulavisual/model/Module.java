package es.dws.aulavisual.model;

import jakarta.persistence.*;
import java.sql.Blob;

@Entity
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Course course;

    private String name;
    private int position;

    @Lob
    private Blob content;

    public Module(Course course, String name, int position,  Blob content) {
        this.course = course;
        this.name = name;
        this.position = position;
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
    public int getPosition() {

        return position;
    }

    public Course getCourse() {

        return this.course;
    }
}
