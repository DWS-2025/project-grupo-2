package es.dws.aulavisual;
import java.nio.file.Path;

public class Paths {

    public static final String CURRENTUSERIDPATH = "files/users/currentUserId.txt";
    public static final String USERSMAPPATH = "files/users/userMap.txt";
    public static final Path USERIMGS = java.nio.file.Paths.get("files/users/images");
    public static final Path USERDEFAULTIMGFOLDER = java.nio.file.Paths.get("files/users/");
    public static final Path USERDEFAULTIMGPATH = java.nio.file.Paths.get("user.png");
    public static final Path COURSEMODULESPATH = java.nio.file.Paths.get("files/courses/");
}
