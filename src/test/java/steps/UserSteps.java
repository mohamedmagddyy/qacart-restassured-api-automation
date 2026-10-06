package steps;

import apis.UserApi;
import io.restassured.response.Response;
import models.User;
import net.datafaker.Faker;

public class UserSteps {

    private final Faker faker;
    private final UserApi userApi;

    public UserSteps() {
        this(new UserApi(), new Faker());
    }

    public UserSteps(UserApi userApi, Faker faker) {
        this.userApi = userApi;
        this.faker = faker;
    }

    public User generateRandomUser() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = "qa_" + faker.internet().uuid() + "@example.com";
        String password = "Aa1!" + faker.internet().uuid().replace("-", "");
        return new User(firstName, lastName, email, password);
    }

    public User generateUserWithoutEmail() {
        User user = generateRandomUser();
        user.setEmail(null);
        return user;
    }

    public Response registerUser(User user) {
        return userApi.register(user);
    }

    public Response register(Object body) {
        return userApi.register(body);
    }

    public Response loginUser(User user) {
        return userApi.login(new User(user.getEmail(), user.getPassword()));
    }

    public Response login(Object body) {
        return userApi.login(body);
    }

    public String getToken(User user) {
        Response loginResponse = loginUser(user);
        return extractToken(loginResponse);
    }

    public String registerAndGetToken(User user) {
        registerUser(user);
        return getToken(user);
    }

    public String extractToken(Response response) {
        return response.path("access_token");
    }
}
