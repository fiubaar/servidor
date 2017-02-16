package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Project;
import models.User;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;
import views.html.project.index;

import java.io.File;

public class Models3D extends Controller {
    /*
    public static Result index() {
        return ok(index.render(User.findByEmail(request().username())));
    }
    */
    private static String rootPath = Play.application().path().getPath();

    public static Result getInfo(Long projectId) {
        Project p = Project.findById(projectId);
        if (p == null)
            return badRequest("Invalid project ID");

        ObjectNode result = Json.newObject();
        result.put("3DModelFile", new File(p.model_name).getName());
        return ok(result);
    }

    public static Result getFile(Long projectId) {
        Project p = Project.findById(projectId);
        if (p == null)
            return badRequest("Invalid project ID");

        //File dir = new File(rootPath + "/data/user_uploads/" + p.user.id + "/models" );
        //File zip_3dmodel = new File(p.model_name);
        //response().setHeader("Content-disposition","attachment; filename=" + new File(p.model_name).getName());
        //return Results.ok(zip_3dmodel);
        return Results.redirect(controllers.routes.ExternalAssets.at(p.model_name));

    }


}
