package controllers.project;

import com.google.common.io.Files;
import controllers.*;
import models.Project;
import models.User;
import org.apache.commons.io.FilenameUtils;
import play.Play;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utils.Commons;
import views.html.project.setup;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class Setup extends Controller {
    // some useful code taken from http://stackoverflow.com/questions/9452375/how-to-get-the-upload-file-with-other-inputs-in-play2

    public static class ProjectForm {

        @Constraints.Required
        public String name;

        public String description;

        /**
         * Validate the authentication.
         *
         * @return null if validation ok, string with details otherwise
         */
        public String validate() {

            if (isBlank(name)) {
                return "Name is required";
            }
            return null;
        }

        private boolean isBlank(String input) {
            return input == null || input.isEmpty() || input.trim().isEmpty();
        }

    }


    private static String rootPath = Play.application().path().getPath();

    public static Result index() {
        return ok(setup.render(User.findByEmail(request().username()), form(ProjectForm.class)));
    }

    public static Result delete(Long idProject) {
        //Primera prueba: siempre borrará proyecto Nro 3. Ok.-
        //Segunda prueba: Id del Project por parámetro. Ok.-
        Project p = Project.findById(Long.valueOf(idProject));
        if(p!=null) {
            try {
                File QR_File = new File(rootPath + "/data/" + p.qr_fileName);
                File Logo_File = new File(rootPath + "/data/" + p.logo_filename);
                File Model_File = new File(rootPath + "/data/" + p.model_name);
                QR_File.delete();
                Logo_File.delete();
                Model_File.delete();
            }
            catch(Exception e){
                e.printStackTrace();
            };
                p.delete();
                // return redirect(routes.Index.index());
                return ok("Ok, encontre el proyecto a borrar !");
            }
        else
            return badRequest("Failed to delete project!");
    }

    public static Result upload() {
        User user = User.findByEmail(request().username());
        Form<ProjectForm> filledForm = form(ProjectForm.class).bindFromRequest();

        if (filledForm.hasErrors()) {
            return badRequest(views.html.project.setup.render(user, filledForm));
            //return badRequest();
        } else {
            ProjectForm pf = filledForm.get();
            Http.MultipartFormData body = request().body().asMultipartFormData();

            File modelFile = upload(body, user, "3DModel", "models");
            if (!validate3DModelFile(modelFile))
                return badRequest("Invalid 3D Model export file. Only ZIP files are allowed");

            File logoFile = upload(body, user, "logo", "logos");
            if (!validateLogoFile(logoFile))
                return badRequest("Invalid Logo image file");

            // http://stackoverflow.com/questions/204784/how-to-construct-a-relative-path-in-java-from-two-absolute-paths-or-urls
            URI dataURI = new File(rootPath + "/data").toURI();
            String modelFile_relativePath = (modelFile != null) ? Commons.getRelativePathToRoot(modelFile.getPath(), "data") : null;
            String logoFile_relativePath = (logoFile != null) ? Commons.getRelativePathToRoot(logoFile.getPath(), "data") : null;

            Project project = Project.createProject(user,
                                                    pf.name,
                                                    pf.description,
                                                    modelFile_relativePath,
                                                    logoFile_relativePath);

            if (project != null)
                return redirect(routes.Display.index(project.id));
            else
                return badRequest("Failed to create project!");
        }
    }

    private static File upload(Http.MultipartFormData body, User user, String fieldName, String folderName) {

        Http.MultipartFormData.FilePart uploadedFilePart = body.getFile(fieldName);
        if (uploadedFilePart != null) {
            String fileName = uploadedFilePart.getFilename();
            String contentType = uploadedFilePart.getContentType();
            File file = uploadedFilePart.getFile();

            System.out.println("File uploaded to " + file.getAbsolutePath());

            // ensure the user upload directory exists
            File destination_dir = new File(rootPath + "/data/user_uploads/" + user.id + "/" + folderName);
            if (!destination_dir.exists())
                destination_dir.mkdir();
            // move from tmp to the real upload directory

            File rename_file = new File(destination_dir, fileName);
            //boolean result = file.renameTo(rename_file);
            try {
                Files.move(file, rename_file);
            } catch (IOException e) {
                System.out.println("Error renaming file");
                e.printStackTrace();
            }
            System.out.println("File renamed to " + rename_file.getAbsolutePath());

            return rename_file;
        }

        return null;
    }

    private static boolean validate3DModelFile(File model_file) {
        if (model_file != null) {
            try {
                String fullpath = model_file.getCanonicalPath();
                //String model_filename = FilenameUtils.getBaseName(fullpath);
                String ext = FilenameUtils.getExtension(fullpath);
                System.out.println("extension = " + ext);
                if (ext.equalsIgnoreCase("zip"))
                    return true;
                //TODO: tendriamos que validar que adentro tenga algun archivo con extension valida como .obj o .3ds?
            } catch (Exception e) {
                return false;
            }
        } else {
            System.out.println("validate3DModelFile - model_file is null");
        }
        return false;
    }

    private static boolean validateLogoFile(File logo_file) {
        if (logo_file != null) {
            try {
                // tomado de http://stackoverflow.com/questions/4169713/how-to-check-a-uploaded-file-whether-it-is-a-image-or-other-file
                InputStream input = new FileInputStream(logo_file);
                ImageIO.read(input).toString();
                // It's an image (only BMP, GIF, JPG and PNG are recognized).
                return true;
            } catch (Exception e) {
                // It's not an image.
                return false;
            }
        } else {
            System.out.println("validateLogoFile - model_file is null");
        }
        return false;
    }
}
