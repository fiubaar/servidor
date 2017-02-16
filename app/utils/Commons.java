package utils;


import play.Play;

import java.io.File;
import java.net.URI;

public class Commons {

    private static File rootPath = Play.application().path();

    public static String getRelativePathToRoot(String path, String subdir) {
        // http://stackoverflow.com/questions/204784/how-to-construct-a-relative-path-in-java-from-two-absolute-paths-or-urls
        URI baseURI = (subdir != null) ? new File(rootPath, subdir).toURI() : rootPath.toURI() ;
        return baseURI.relativize(new File(path).toURI()).getPath();

    }
}
