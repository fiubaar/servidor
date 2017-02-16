package controllers;

import models.User;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;
import utils.QRCode;
import views.html.markers.index;

import java.io.File;

@Security.Authenticated(Secured.class)
public class Markers extends Controller {

    private static String rootPath = Play.application().path().getPath();

    public static Result index() {
        return ok(index.render(User.findByEmail(request().username())));
    }

    //XXX: es necesario exponer esto como api??
    public static Result generate(String data) {
        // generate a marker which contains a QR code inside
        try {
            String full_path = QRCode.generateImage(data, User.findByEmail(request().username()));
            if (full_path != null) {
                File f = new File(full_path);
                //response().setContentType("application/x-download");
                response().setHeader("Content-disposition","attachment; filename=" + f.getName());
                return Results.ok(f);
            }

        } catch (Exception e) {
            return Results.internalServerError("There was an error generating the marker\n" + e.toString());
        }
        return ok();
    }

    public static Result getFile(String filename) {
        User user = User.findByEmail(request().username());
        File f = new File(rootPath + "/data/qr_codes/" + user.id + "/" + filename);
        return ok(f);
    }
}
