package apis;

import io.restassured.response.Response;
import models.User;
import routes.Routes;
import specs.RequestSpec;

import static io.restassured.RestAssured.given;

public class UserApi {

    public Response register(User user) {
        return register((Object) user);
    }

    public Response register(Object body) {
        return given()
                .spec(RequestSpec.getRequestSpec())
                .body(body)
                .when()
                .post(Routes.REGISTER)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    public Response login(User user) {
        return login((Object) user);
    }

    public Response login(Object body) {
        return given()
                .spec(RequestSpec.getRequestSpec())
                .body(body)
                .when()
                .post(Routes.LOGIN)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }
}
