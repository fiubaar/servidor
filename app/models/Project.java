package models;

import models.utils.AppException;
import models.utils.Hash;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import utils.Commons;
import utils.QRCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.List;

// basado en https://www.playframework.com/documentation/2.1.x/JavaEbean

// Cada vez que actualicemos objetos de tipo Model Evolutions va a actualizar los scripts
// para generar las tablas necesarias de las base de datos
// https://www.playframework.com/documentation/2.1.x/Evolutions
// Es importante saber que cada vez que aplica un script hace un drop de las tablas y se pierde todo

@Entity
public class Project extends Model {

    @Id
    public Long id;

    //TODO: revisar este constraint de unique porque si otro usuario ya creo un proyecto con ese nombre da errores
    // quizas hay que hacer que la clave unica sea otra
    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String name;

    @Constraints.Required
    @Formats.NonEmpty
    @ManyToOne
    public User user;

    public String description;

    public String qr_fileName;

    //TODO: define a default 3D model for the projects in case the user does not upload one when creating the project
    public String model_name = "DEFAULT_MODEL";

    public String logo_filename = "DEFAULT_LOGO";

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date dateCreation;

    public void setQRFilename(String filename) throws AppException {
        this.qr_fileName = filename;
        this.save();
    }

    public void set3DModel(String modelname) throws AppException {
        this.model_name = modelname;
        this.save();
    }

    public static Finder<Long, Project> find = new Finder<Long, Project>(Long.class, Project.class);

    public static Project findById(Long id) {
        return find.where().eq("id", id).findUnique();
    }

    public static Project findByName(String name) {
        return find.where().eq("name", name).findUnique();
    }

    public static List<Project> findByUser(User user) {
        return find.where().eq("user", user).findList();
    }

    public static Project createProject(User user, String name, String description, String model_name, String logo_filename) {
        try {
            Project p = new Project();
            p.user = user;
            p.name = name;
            p.description = description;
            if (model_name != null)
                p.model_name = model_name;
            if (logo_filename != null)
                p.logo_filename = logo_filename;
            // salvamos para que asigne un id
            p.save();
            // generamos el codigo QR con el ID de este proyecto
            String qr_fullpath = QRCode.generateImage(p.id.toString(), user);
            p.qr_fileName = Commons.getRelativePathToRoot(qr_fullpath, "data");
            p.save(); // salvamos de nuevo para actualizar el qr_filename
            user.projects.add(p);
            user.save();
            return p;
        } catch (Exception e) {
            System.out.println("Error al crear project");
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Authenticate a User, from a email and clear password.
     *
     * @param email         email
     * @param clearPassword clear password
     * @return User if authenticated, null otherwise
     * @throws models.utils.AppException App Exception
     */
    /*
    public static Project authenticate(String email, String clearPassword) throws AppException {

        // get the user with email only to keep the salt password
        Project user = find.where().eq("email", email).findUnique();
        if (user != null) {
            // get the hash password from the salt + clear password
            if (Hash.checkPassword(clearPassword, user.passwordHash)) {
                return user;
            }
        }
        return null;
    }

    public void changePassword(String password) throws AppException {
        this.passwordHash = Hash.createPassword(password);
        this.save();
    }
*/

}
