package controllers.project;

import controllers.Secured;
import models.Project;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.project.index;

import java.util.List;

@Security.Authenticated(Secured.class)
public class Index extends Controller {

    public static Result index() {
        User user = User.findByEmail(request().username());
        List<Project> projects = Project.findByUser(user);
        return ok(index.render(user, projects));
    }
}
