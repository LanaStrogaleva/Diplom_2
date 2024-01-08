package user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
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
        UserClient userClient = new UserClient();
        userClient.setBaseURL();
        User user = new User()
                .withEmail(randomString(6) + "@yandex.ru")
                .withPassword(randomString(6))
                .withName(randomString(6));

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
        int statusCode = 200;

        UserClient userClient = new UserClient();

        Response loginResponse = userClient.loginUser(new User().withEmail(initialEmail).withPassword(initialPassword));

        userClient.checkStatusCode(loginResponse, statusCode);
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
        int statusCode = 401;
        String message = "email or password are incorrect";

        UserClient userClient = new UserClient();

        Response loginResponse = userClient.loginUser(new User().withEmail(randomString(5) + initialEmail).withPassword(initialPassword));

        userClient.checkStatusCode(loginResponse, statusCode);
        userClient.checkResponseBodyNotEmpty(loginResponse);
        userClient.checkIsSuccessResponse(loginResponse, success);
        userClient.checkResponseBodyMessage(loginResponse, message);
    }

    @Test
    @DisplayName("Логин пользователя.Логин c неверным полем \"password\".")
    @Description("Логин c неверным полем \"password\". Запрос возвращает код ответа - 401")
    public void loginWithIncorrectPassword() {
        Boolean success = false;
        int statusCode = 401;
        String message = "email or password are incorrect";

        UserClient userClient = new UserClient();

        Response loginResponse = userClient.loginUser(new User().withEmail(initialEmail).withPassword(randomString(5) + initialPassword));

        userClient.checkStatusCode(loginResponse, statusCode);
        userClient.checkResponseBodyNotEmpty(loginResponse);
        userClient.checkIsSuccessResponse(loginResponse, success);
        userClient.checkResponseBodyMessage(loginResponse, message);
    }

}
