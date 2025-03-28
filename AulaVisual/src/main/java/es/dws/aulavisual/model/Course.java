package es.dws.aulavisual.model;

import java.io.IOException;
import java.sql.Blob;
import jakarta.persistence.*;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private  String name;
    private  String description;
    private  String task;

    @OneToOne()
    private User teacher = null;

    @Lob
    private Blob imageCourse;

    @ManyToMany(fetch = FetchType.EAGER)
    private final List <User> students = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "course")
    private final List <Module> modules = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "course")
    private final List<Submission> submissions = new ArrayList<>();



    protected Course() {

    }

    public Course(String name, String description, String task, MultipartFile imageCourse) {

        this.name = name;
        this.description = description;
        this.task = task;
        this.imageCourse = transformImage(imageCourse);
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

    public String getTask() {

        return task;
    }

    public User getTeacher() {

        return teacher;
    }

    private Blob transformImage(MultipartFile imageCourse) {

        if(imageCourse == null ||imageCourse.isEmpty()){

            return null;
        }else{

            try {
                return BlobProxy.generateProxy(imageCourse.getInputStream(), imageCourse.getSize());
            }catch (IOException e) {

                throw new RuntimeException("Error processing image", e);
            }
        }
    }

    public List<User> getStudents() {

        return students;
    }

    public Blob getImage() {

        return imageCourse;
    }

    public void setTeacher(User teacher) {

        this.teacher = teacher;
    }

    public List<Module> getmodules() {

        return modules;
    }
}