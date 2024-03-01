package user;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static user.Utils.randomString;

public class LoginUserTest {
    String initialEmail;
    String initialPassword;
    String initialName;
    String accessToken;

    @Before
    public void SetUp() {
        Faker faker = new Faker();
        UserClient userClient = new UserClient();
        userClient.setBaseURL();
        User user = new User()
                .withEmail(faker.internet().emailAddress())
                .withPassword(faker.internet().password(6,7))
                .withName(faker.name().name());

        initialName = user.getName();
        initialPassword = user.getPassword();
        initialEmail = user.getEmail();

        accessToken = userClient.createUser(user).body().path("accessToken").toString().substring(7);
    }

    @After
    public void TearDown() {
        if (accessToken != null) {
            UserClient userClient = new UserClient();
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Логин пользователя.Логин под существующим пользователем.")
    @Description("Логин под существующим пользователем. Запрос возвращает код ответа - 200")
    public void loginUser() {
        Boolean success = true;

        UserClient userClient = new UserClient();

        Response loginResponse = userClient.loginUser(new User().withEmail(initialEmail).withPassword(initialPassword));

        userClient.checkStatusCode(loginResponse, HttpStatus.SC_OK);
        userClient.checkResponseBodyNotEmpty(loginResponse);
        userClient.checkIsSuccessResponse(loginResponse, success);
        userClient.checkEmailResponseBody(loginResponse, initialEmail);
        userClient.checkNameResponseBody(loginResponse, initialName);

    }

    @Test
    @DisplayName("Логин пользователя.Логин c неверным полем \"email\".")
    @Description("Логин c неверным полем \"email\". Запрос возвращает код ответа - 401")
    public void loginWithIncorrectEmail() {
        Boolean success = false;
        String message = "email or password are incorrect";

        UserClient userClient = new UserClient();

        Response loginResponse = userClient.loginUser(new User().withEmail("fjdhjd" + initialEmail).withPassword(initialPassword));

        userClient.checkStatusCode(loginResponse, HttpStatus.SC_UNAUTHORIZED);
        userClient.checkResponseBodyNotEmpty(loginResponse);
        userClient.checkIsSuccessResponse(loginResponse, success);
        userClient.checkResponseBodyMessage(loginResponse, message);
    }

    @Test
    @DisplayName("Логин пользователя.Логин c неверным полем \"password\".")
    @Description("Логин c неверным полем \"password\". Запрос возвращает код ответа - 401")
    public void loginWithIncorrectPassword() {
        Boolean success = false;
        String message = "email or password are incorrect";

        UserClient userClient = new UserClient();

        Response loginResponse = userClient.loginUser(new User().withEmail(initialEmail).withPassword(randomString(5) + initialPassword));

        userClient.checkStatusCode(loginResponse, HttpStatus.SC_UNAUTHORIZED);
        userClient.checkResponseBodyNotEmpty(loginResponse);
        userClient.checkIsSuccessResponse(loginResponse, success);
        userClient.checkResponseBodyMessage(loginResponse, message);
    }

}
