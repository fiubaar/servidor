package controllers.project;

import controllers.Secured;
import models.Project;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.project.display;

@Security.Authenticated(Secured.class)
public class Display extends Controller {

    public static Result index(Long id) {
        User user = User.findByEmail(request().username());
        Project project = Project.findById(id);
        if (project == null)
            return badRequest("Project not found");
        if (user.id == project.user.id)
            return ok(display.render(user, project));
        else
            return badRequest("User " + user.fullname + " does not have permission to view Project ID " + id);
    }
}
