package user;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static user.UtilsString.randomString;

public class LoginUserTest {
    public static String BASE_URL = "https://stellarburgers.nomoreparties.site";
    String accessToken;

    @Before
    @Step("Перейти на сайт по базовому URL")
    public void SetUp() {

        RestAssured.baseURI = BASE_URL;
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
        public void loginUser () {
            Boolean success = true;
            int statusCode = 200;

            User user = new User()
                    .withEmail(randomString(6) + "@yandex.ru")
                    .withPassword(randomString(6))
                    .withName(randomString(6));
            UserClient userClient = new UserClient();
            Response createResponse = userClient.createUser(user);
            accessToken = createResponse.body().path("accessToken").toString().substring(7);

            Response loginResponse = userClient.loginUser(new User().withEmail(user.getEmail()).withPassword(user.getPassword()));

            userClient.checkStatusCode(loginResponse, statusCode);
            userClient.checkResponseBodyNotEmpty(loginResponse);
            userClient.checkIsSuccessResponse(loginResponse, success);

        }

    @Test
    @DisplayName("Логин пользователя.Логин c неверным полем \"email\".")
    @Description("Логин c неверным полем \"email\". Запрос возвращает код ответа - 401")
    public void loginWithIncorrectEmail () {
        Boolean success = false;
        int statusCode = 401;
        String message = "email or password are incorrect";

        User user = new User()
                .withEmail(randomString(6) + "@yandex.ru")
                .withPassword(randomString(6))
                .withName(randomString(6));
        UserClient userClient = new UserClient();
        Response createResponse = userClient.createUser(user);
        accessToken = createResponse.body().path("accessToken").toString().substring(7);

        Response loginResponse = userClient.loginUser(new User().withEmail(randomString(5) + user.getEmail()).withPassword(user.getPassword()));

        userClient.checkStatusCode(loginResponse, statusCode);
        userClient.checkResponseBodyNotEmpty(loginResponse);
        userClient.checkIsSuccessResponse(loginResponse, success);
        userClient.checkResponseBodyMessage(loginResponse, message);
    }

    @Test
    @DisplayName("Логин пользователя.Логин c неверным полем \"password\".")
    @Description("Логин c неверным полем \"password\". Запрос возвращает код ответа - 401")
    public void loginWithIncorrectPassword () {
        Boolean success = false;
        int statusCode = 401;
        String message = "email or password are incorrect";

        User user = new User()
                .withEmail(randomString(6) + "@yandex.ru")
                .withPassword(randomString(6))
                .withName(randomString(6));
        UserClient userClient = new UserClient();
        Response createResponse = userClient.createUser(user);
        accessToken = createResponse.body().path("accessToken").toString().substring(7);

        Response loginResponse = userClient.loginUser(new User().withEmail(user.getEmail()).withPassword(randomString(5) + user.getPassword()));

        userClient.checkStatusCode(loginResponse, statusCode);
        userClient.checkResponseBodyNotEmpty(loginResponse);
        userClient.checkIsSuccessResponse(loginResponse, success);
        userClient.checkResponseBodyMessage(loginResponse, message);
    }

    }
