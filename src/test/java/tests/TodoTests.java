package tests;

import config.Config;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import models.Todo;
import models.User;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import steps.TodoSteps;
import steps.UserSteps;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Feature("Todo API")
public class TodoTests {

    private static final String INVALID_TOKEN = "invalid-token";
    private static final String NON_EXISTING_TASK_ID = "000000000000000000000000";
    private static final String MALFORMED_TASK_ID = "invalid-id";

    @BeforeMethod
    public void setUp() {
        if (!Config.isBaseUrlConfigured()) {
            throw new SkipException("TODO: Configure base.url or base.url.<env> in config.properties before running API tests.");
        }
    }

    @Test
    @Story("Create Todo")
    @Description("Positive add todo flow using an authenticated generated user.")
    public void shouldAddTodoSuccessfully() {
        TodoSteps todoSteps = new TodoSteps();
        Response response = todoSteps.createTodo(todoSteps.generateTodo(), createToken());
        logResponse("shouldAddTodoSuccessfully", response);
        assertEquals(response.statusCode(), 201);
        assertNotNull(response.path("_id"));
    }

    @Test
    @Story("Get Todos")
    @Description("Create a todo and retrieve all todos for the authenticated user.")
    public void shouldGetAllTodosSuccessfully() {
        TodoSteps todoSteps = new TodoSteps();
        String token = createToken();
        assertEquals(todoSteps.createTodo(todoSteps.generateTodo(), token).statusCode(), 201);
        Response response = todoSteps.getTodos(token);
        logResponse("shouldGetAllTodosSuccessfully", response);
        assertEquals(response.statusCode(), 200);
        assertNotNull(response.path("tasks"));
    }

    @Test
    @Story("Update Todo")
    @Description("Create a todo and update it to completed.")
    public void shouldUpdateTodoSuccessfully() {
        TodoSteps todoSteps = new TodoSteps();
        String token = createToken();
        Todo todo = todoSteps.generateTodo();
        String taskId = createTodoAndReturnTaskId(todoSteps, todo, token);
        Response response = todoSteps.updateTodo(taskId, todoSteps.generateCompletedTodo(todo.getItem() + " updated"), token);
        logResponse("shouldUpdateTodoSuccessfully", response);
        assertEquals(response.statusCode(), 200);
        assertEquals(response.path("isCompleted"), Boolean.TRUE);
    }

    @Test
    @Story("Delete Todo")
    @Description("Create a todo and delete it by ID.")
    public void shouldDeleteTodoSuccessfully() {
        TodoSteps todoSteps = new TodoSteps();
        String token = createToken();
        String taskId = createTodoAndReturnTaskId(todoSteps, todoSteps.generateTodo(), token);
        Response response = todoSteps.deleteTodo(taskId, token);
        logResponse("shouldDeleteTodoSuccessfully", response);
        assertEquals(response.statusCode(), 200);
    }

    @Test
    @Story("Authentication")
    @Description("Create todo without Authorization header.")
    public void shouldNotCreateTodoWithoutToken() {
        TodoSteps todoSteps = new TodoSteps();
        assertUnsuccessful("shouldNotCreateTodoWithoutToken", todoSteps.createTodo(todoSteps.generateTodo(), null));
    }

    @Test
    @Story("Authentication")
    @Description("Create todo with invalid token.")
    public void shouldNotCreateTodoWithInvalidToken() {
        TodoSteps todoSteps = new TodoSteps();
        assertUnsuccessful("shouldNotCreateTodoWithInvalidToken", todoSteps.createTodo(todoSteps.generateTodo(), INVALID_TOKEN));
    }

    @Test
    @Story("Authentication")
    @Description("Get todos without Authorization header.")
    public void shouldNotGetTodosWithoutToken() {
        assertUnsuccessful("shouldNotGetTodosWithoutToken", new TodoSteps().getTodos(null));
    }

    @Test
    @Story("Authentication")
    @Description("Get todos with invalid token.")
    public void shouldNotGetTodosWithInvalidToken() {
        assertUnsuccessful("shouldNotGetTodosWithInvalidToken", new TodoSteps().getTodos(INVALID_TOKEN));
    }

    @Test
    @Story("Authentication")
    @Description("Update todo without Authorization header.")
    public void shouldNotUpdateTodoWithoutToken() {
        TodoSteps todoSteps = new TodoSteps();
        String token = createToken();
        Todo todo = todoSteps.generateTodo();
        String taskId = createTodoAndReturnTaskId(todoSteps, todo, token);
        assertUnsuccessful("shouldNotUpdateTodoWithoutToken",
                todoSteps.updateTodo(taskId, todoSteps.generateCompletedTodo(todo.getItem()), null));
    }

    @Test
    @Story("Authentication")
    @Description("Update todo with invalid token.")
    public void shouldNotUpdateTodoWithInvalidToken() {
        TodoSteps todoSteps = new TodoSteps();
        String token = createToken();
        Todo todo = todoSteps.generateTodo();
        String taskId = createTodoAndReturnTaskId(todoSteps, todo, token);
        assertUnsuccessful("shouldNotUpdateTodoWithInvalidToken",
                todoSteps.updateTodo(taskId, todoSteps.generateCompletedTodo(todo.getItem()), INVALID_TOKEN));
    }

    @Test
    @Story("Update Todo")
    @Description("Update non-existing todo ID.")
    public void shouldNotUpdateNonExistingTodo() {
        TodoSteps todoSteps = new TodoSteps();
        assertUnsuccessful("shouldNotUpdateNonExistingTodo",
                todoSteps.updateTodo(NON_EXISTING_TASK_ID, todoSteps.generateCompletedTodo("missing task"), createToken()));
    }

    @Test
    @Story("Update Todo")
    @Description("Update malformed todo ID.")
    public void shouldNotUpdateMalformedTodoId() {
        TodoSteps todoSteps = new TodoSteps();
        assertUnsuccessful("shouldNotUpdateMalformedTodoId",
                todoSteps.updateTodo(MALFORMED_TASK_ID, todoSteps.generateCompletedTodo("bad id"), createToken()));
    }

    @Test
    @Story("Update Todo")
    @Description("Update a todo after it has been deleted.")
    public void shouldNotUpdateDeletedTodo() {
        TodoSteps todoSteps = new TodoSteps();
        String token = createToken();
        Todo todo = todoSteps.generateTodo();
        String taskId = createTodoAndReturnTaskId(todoSteps, todo, token);
        assertEquals(todoSteps.deleteTodo(taskId, token).statusCode(), 200);
        assertUnsuccessful("shouldNotUpdateDeletedTodo",
                todoSteps.updateTodo(taskId, todoSteps.generateCompletedTodo(todo.getItem()), token));
    }

    @Test
    @Story("Delete Todo")
    @Description("Delete todo without Authorization header.")
    public void shouldNotDeleteTodoWithoutToken() {
        TodoSteps todoSteps = new TodoSteps();
        String token = createToken();
        String taskId = createTodoAndReturnTaskId(todoSteps, todoSteps.generateTodo(), token);
        assertUnsuccessful("shouldNotDeleteTodoWithoutToken", todoSteps.deleteTodo(taskId, null));
    }

    @Test
    @Story("Delete Todo")
    @Description("Delete todo with invalid token.")
    public void shouldNotDeleteTodoWithInvalidToken() {
        TodoSteps todoSteps = new TodoSteps();
        String token = createToken();
        String taskId = createTodoAndReturnTaskId(todoSteps, todoSteps.generateTodo(), token);
        assertUnsuccessful("shouldNotDeleteTodoWithInvalidToken", todoSteps.deleteTodo(taskId, INVALID_TOKEN));
    }

    @Test
    @Story("Delete Todo")
    @Description("Delete non-existing todo ID.")
    public void shouldNotDeleteNonExistingTodo() {
        assertUnsuccessful("shouldNotDeleteNonExistingTodo", new TodoSteps().deleteTodo(NON_EXISTING_TASK_ID, createToken()));
    }

    @Test
    @Story("Delete Todo")
    @Description("Delete malformed todo ID.")
    public void shouldNotDeleteMalformedTodoId() {
        assertUnsuccessful("shouldNotDeleteMalformedTodoId", new TodoSteps().deleteTodo(MALFORMED_TASK_ID, createToken()));
    }

    @Test
    @Story("Delete Todo")
    @Description("Delete the same todo twice.")
    public void shouldNotDeleteSameTodoTwice() {
        TodoSteps todoSteps = new TodoSteps();
        String token = createToken();
        String taskId = createTodoAndReturnTaskId(todoSteps, todoSteps.generateTodo(), token);
        assertEquals(todoSteps.deleteTodo(taskId, token).statusCode(), 200);
        assertUnsuccessful("shouldNotDeleteSameTodoTwice", todoSteps.deleteTodo(taskId, token));
    }

    @Test
    @Story("Authorization")
    @Description("User B attempts to update User A's todo.")
    public void shouldNotUpdateAnotherUsersTodo() {
        TodoSteps todoSteps = new TodoSteps();
        String userAToken = createToken();
        Todo todo = todoSteps.generateTodo();
        String taskId = createTodoAndReturnTaskId(todoSteps, todo, userAToken);
        String userBToken = createToken();
        assertUnsuccessful("shouldNotUpdateAnotherUsersTodo",
                todoSteps.updateTodo(taskId, todoSteps.generateCompletedTodo(todo.getItem()), userBToken));
    }

    @Test
    @Story("Authorization")
    @Description("User B attempts to delete User A's todo.")
    public void shouldNotDeleteAnotherUsersTodo() {
        TodoSteps todoSteps = new TodoSteps();
        String taskId = createTodoAndReturnTaskId(todoSteps, todoSteps.generateTodo(), createToken());
        assertUnsuccessful("shouldNotDeleteAnotherUsersTodo", todoSteps.deleteTodo(taskId, createToken()));
    }

    @Test
    @Story("Authorization")
    @Description("User B must not see User A's todos.")
    public void shouldNotShowAnotherUsersTodos() {
        TodoSteps todoSteps = new TodoSteps();
        String userATaskId = createTodoAndReturnTaskId(todoSteps, todoSteps.generateTodo(), createToken());
        Response response = todoSteps.getTodos(createToken());
        logResponse("shouldNotShowAnotherUsersTodos", response);
        assertEquals(response.statusCode(), 200);
        List<String> taskIds = response.path("tasks._id");
        assertFalse(taskIds.contains(userATaskId), "User B should not see User A's task ID.");
    }

    @Test
    @Story("Payload Validation")
    @Description("Create todo with missing item.")
    public void shouldNotCreateTodoWithMissingItem() {
        assertUnsuccessful("shouldNotCreateTodoWithMissingItem", new TodoSteps().createTodo(new Todo(null, false), createToken()));
    }

    @Test
    @Story("Payload Validation")
    @Description("Create todo with empty item.")
    public void shouldNotCreateTodoWithEmptyItem() {
        assertUnsuccessful("shouldNotCreateTodoWithEmptyItem", new TodoSteps().createTodo(new Todo("", false), createToken()));
    }

    @Test
    @Story("Payload Validation")
    @Description("Create todo with missing isCompleted.")
    public void shouldNotCreateTodoWithMissingIsCompleted() {
        assertUnsuccessful("shouldNotCreateTodoWithMissingIsCompleted",
                new TodoSteps().createTodo(new Todo("task without completion", null), createToken()));
    }

    private String createToken() {
        UserSteps userSteps = new UserSteps();
        User user = userSteps.generateRandomUser();
        return userSteps.registerAndGetToken(user);
    }

    private String createTodoAndReturnTaskId(TodoSteps todoSteps, Todo todo, String token) {
        Response response = todoSteps.createTodo(todo, token);
        assertEquals(response.statusCode(), 201, "Precondition create todo should succeed.");
        String taskId = response.path("_id");
        assertNotNull(taskId, "Create todo response should include _id.");
        return taskId;
    }

    private void assertUnsuccessful(String scenario, Response response) {
        logResponse(scenario, response);
        assertTrue(response.statusCode() < 200 || response.statusCode() >= 300,
                scenario + " should not return a successful 2xx response.");
    }

    private void logResponse(String scenario, Response response) {
        System.out.println("Scenario: " + scenario);
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.asPrettyString());
    }
}
