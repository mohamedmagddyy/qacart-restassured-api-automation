package steps;

import apis.TodoApi;
import io.restassured.response.Response;
import models.Todo;
import net.datafaker.Faker;

public class TodoSteps {

    private final Faker faker;
    private final TodoApi todoApi;

    public TodoSteps() {
        this(new TodoApi(), new Faker());
    }

    public TodoSteps(TodoApi todoApi, Faker faker) {
        this.todoApi = todoApi;
        this.faker = faker;
    }

    public Todo generateTodo() {
        return new Todo(faker.book().title(), false);
    }

    public Todo generateCompletedTodo(String item) {
        return new Todo(item, true);
    }

    public Todo generateInvalidTodo() {
        Todo todo = generateTodo();
        todo.setItem(null);
        return todo;
    }

    public Response getTodos(String token) {
        return todoApi.getTodos(token);
    }

    public Response createTodo(Todo todo, String token) {
        return todoApi.addTodo(todo, token);
    }

    public Response getTodo(String todoId, String token) {
        return todoApi.getTodo(todoId, token);
    }

    public Response updateTodo(String taskId, Todo todo, String token) {
        return todoApi.updateTodo(taskId, todo, token);
    }

    public Response deleteTodo(String todoId, String token) {
        return todoApi.deleteTodo(todoId, token);
    }
}
