package routes;

import config.Config;

public final class Routes {

    public static final String REGISTER = Config.getRequiredRoute("route.register");
    public static final String LOGIN = Config.getRequiredRoute("route.login");
    public static final String TODOS = Config.getRequiredRoute("route.todos");

    private Routes() {
    }

    public static String todoById(String todoId) {
        return TODOS + "/" + todoId;
    }
}
