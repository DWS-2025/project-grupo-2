package es.dws.aulavisual.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SubmissionComment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    private String role;
    private String text;

    public SubmissionComment(String role, String text) {
        this.role = role;
        this.text = text;
    }
    public SubmissionComment() {
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

}
