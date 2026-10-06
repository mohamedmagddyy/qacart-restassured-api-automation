package apis;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Todo;
import routes.Routes;
import specs.RequestSpec;

import static io.restassured.RestAssured.given;

public class TodoApi {

    public Response getTodos(String token) {
        return authorizedRequest(token)
                .when()
                .get(Routes.TODOS)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    public Response addTodo(Todo todo, String token) {
        return authorizedRequest(token)
                .body(todo)
                .when()
                .post(Routes.TODOS)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    public Response getTodo(String todoId, String token) {
        return authorizedRequest(token)
                .when()
                .get(Routes.todoById(todoId))
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    public Response updateTodo(String taskId, Todo todo, String token) {
        return authorizedRequest(token)
                .body(todo)
                .when()
                .put(Routes.todoById(taskId))
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    public Response deleteTodo(String todoId, String token) {
        return authorizedRequest(token)
                .when()
                .delete(Routes.todoById(todoId))
                .then()
                .log().ifValidationFails()
                .extract()
                .response();
    }

    private RequestSpecification authorizedRequest(String token) {
        RequestSpecification request = given().spec(RequestSpec.getRequestSpec());
        if (token != null) {
            request.auth().oauth2(token);
        }
        return request;
    }
}
