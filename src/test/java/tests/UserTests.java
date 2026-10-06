package tests;

import config.Config;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import models.User;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import steps.UserSteps;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Feature("User API")
public class UserTests {

    private static final String LOGIN_ERROR_MESSAGE =
            "The email and password combination is not correct, please fill a correct email and password";
    private static final String EMAIL_NOT_FOUND_MESSAGE = "We could not find the email in the database";

    private UserSteps userSteps;

    @BeforeMethod
    public void setUp() {
        if (!Config.isBaseUrlConfigured()) {
            throw new SkipException("TODO: Configure base.url or base.url.<env> in config.properties before running API tests.");
        }
        userSteps = new UserSteps();
    }

    @Test
    @Story("Register")
    @Description("Positive register flow using a generated user.")
    public void shouldRegisterUserSuccessfully() {
        Response response = userSteps.registerUser(userSteps.generateRandomUser());
        logResponse("shouldRegisterUserSuccessfully", response);
        assertEquals(response.statusCode(), 201);
    }

    @Test
    @Story("Login")
    @Description("Positive login flow after registering a generated user.")
    public void shouldLoginUserSuccessfully() {
        User user = createRegisteredUser();
        Response response = userSteps.loginUser(user);
        logResponse("shouldLoginUserSuccessfully", response);
        assertEquals(response.statusCode(), 200);
        assertNotNull(userSteps.extractToken(response));
    }

    @Test
    @Story("Login")
    @Description("Login with a valid email and wrong password.")
    public void shouldNotLoginWithWrongPassword() {
        User user = createRegisteredUser();
        Response response = userSteps.loginUser(new User(user.getEmail(), "WrongPassword123!"));
        assertConfirmedLoginUnauthorized(response);
    }

    @Test
    @Story("Login")
    @Description("Login with a wrong email and correct password.")
    public void shouldNotLoginWithWrongEmail() {
        User user = createRegisteredUser();
        Response response = userSteps.loginUser(new User("wrong_" + user.getEmail(), user.getPassword()));
        assertEmailNotFound(response);
    }

    @Test
    @Story("Login")
    @Description("Login with missing email.")
    public void shouldNotLoginWithMissingEmail() {
        User user = createRegisteredUser();
        assertUnsuccessful("shouldNotLoginWithMissingEmail", userSteps.loginUser(new User(null, user.getPassword())));
    }

    @Test
    @Story("Login")
    @Description("Login with empty email.")
    public void shouldNotLoginWithEmptyEmail() {
        User user = createRegisteredUser();
        assertUnsuccessful("shouldNotLoginWithEmptyEmail", userSteps.loginUser(new User("", user.getPassword())));
    }

    @Test
    @Story("Login")
    @Description("Login with missing password.")
    public void shouldNotLoginWithMissingPassword() {
        User user = createRegisteredUser();
        assertUnsuccessful("shouldNotLoginWithMissingPassword", userSteps.loginUser(new User(user.getEmail(), null)));
    }

    @Test
    @Story("Login")
    @Description("Login with empty password.")
    public void shouldNotLoginWithEmptyPassword() {
        User user = createRegisteredUser();
        assertUnsuccessful("shouldNotLoginWithEmptyPassword", userSteps.loginUser(new User(user.getEmail(), "")));
    }

    @Test
    @Story("Login")
    @Description("Login with wrong email and wrong password.")
    public void shouldNotLoginWithWrongEmailAndWrongPassword() {
        Response response = userSteps.loginUser(new User("missing-user@example.com", "invalid-password"));
        assertEmailNotFound(response);
    }

    @Test
    @Story("Login")
    @Description("Login with a generated user that was not registered.")
    public void shouldNotLoginWithUnregisteredUser() {
        User user = userSteps.generateRandomUser();
        Response response = userSteps.loginUser(user);
        assertEmailNotFound(response);
    }

    @Test
    @Story("Login")
    @Description("Login with an empty JSON object.")
    public void shouldNotLoginWithEmptyBody() {
        assertUnsuccessful("shouldNotLoginWithEmptyBody", userSteps.login(Map.of()));
    }

    @Test
    @Story("Register")
    @Description("Register with missing email.")
    public void shouldNotRegisterWithMissingEmail() {
        assertUnsuccessful("shouldNotRegisterWithMissingEmail", userSteps.registerUser(userSteps.generateUserWithoutEmail()));
    }

    @Test
    @Story("Register")
    @Description("Register with empty email.")
    public void shouldNotRegisterWithEmptyEmail() {
        User user = userSteps.generateRandomUser();
        user.setEmail("");
        assertUnsuccessful("shouldNotRegisterWithEmptyEmail", userSteps.registerUser(user));
    }

    @Test
    @Story("Register")
    @Description("Register with invalid email format.")
    public void shouldNotRegisterWithInvalidEmailFormat() {
        User user = userSteps.generateRandomUser();
        user.setEmail("invalid-email");
        assertUnsuccessful("shouldNotRegisterWithInvalidEmailFormat", userSteps.registerUser(user));
    }

    @Test
    @Story("Register")
    @Description("Register with missing password.")
    public void shouldNotRegisterWithMissingPassword() {
        User user = userSteps.generateRandomUser();
        user.setPassword(null);
        assertUnsuccessful("shouldNotRegisterWithMissingPassword", userSteps.registerUser(user));
    }

    @Test
    @Story("Register")
    @Description("Register with empty password.")
    public void shouldNotRegisterWithEmptyPassword() {
        User user = userSteps.generateRandomUser();
        user.setPassword("");
        assertUnsuccessful("shouldNotRegisterWithEmptyPassword", userSteps.registerUser(user));
    }

    @Test
    @Story("Register")
    @Description("Register with missing first name.")
    public void shouldNotRegisterWithMissingFirstName() {
        User user = userSteps.generateRandomUser();
        user.setFirstName(null);
        assertUnsuccessful("shouldNotRegisterWithMissingFirstName", userSteps.registerUser(user));
    }

    @Test
    @Story("Register")
    @Description("Register with empty first name.")
    public void shouldNotRegisterWithEmptyFirstName() {
        User user = userSteps.generateRandomUser();
        user.setFirstName("");
        assertUnsuccessful("shouldNotRegisterWithEmptyFirstName", userSteps.registerUser(user));
    }

    @Test
    @Story("Register")
    @Description("Register with missing last name.")
    public void shouldNotRegisterWithMissingLastName() {
        User user = userSteps.generateRandomUser();
        user.setLastName(null);
        assertUnsuccessful("shouldNotRegisterWithMissingLastName", userSteps.registerUser(user));
    }

    @Test
    @Story("Register")
    @Description("Register with empty last name.")
    public void shouldNotRegisterWithEmptyLastName() {
        User user = userSteps.generateRandomUser();
        user.setLastName("");
        assertUnsuccessful("shouldNotRegisterWithEmptyLastName", userSteps.registerUser(user));
    }

    @Test
    @Story("Register")
    @Description("Register with an email that already exists.")
    public void shouldNotRegisterWithDuplicateEmail() {
        User user = createRegisteredUser();
        assertUnsuccessful("shouldNotRegisterWithDuplicateEmail", userSteps.registerUser(user));
    }

    @Test
    @Story("Register")
    @Description("Register with an empty JSON object.")
    public void shouldNotRegisterWithEmptyBody() {
        assertUnsuccessful("shouldNotRegisterWithEmptyBody", userSteps.register(Map.of()));
    }

    private User createRegisteredUser() {
        User user = userSteps.generateRandomUser();
        Response response = userSteps.registerUser(user);
        assertEquals(response.statusCode(), 201, "Precondition register should succeed.");
        return user;
    }

    private void assertConfirmedLoginUnauthorized(Response response) {
        logResponse("confirmedLoginUnauthorized", response);
        assertEquals(response.statusCode(), 401);
        assertEquals(response.path("message"), LOGIN_ERROR_MESSAGE);
    }

    private void assertEmailNotFound(Response response) {
        logResponse("emailNotFound", response);
        assertEquals(response.statusCode(), 400);
        assertEquals(response.path("message"), EMAIL_NOT_FOUND_MESSAGE);
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
