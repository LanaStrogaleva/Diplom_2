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

public class CreateUserTest {
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
    @DisplayName("Создание пользователя.Cоздание уникального пользователя.")
    @Description("Создание пользователя со случайными данными. Запрос возвращает код ответа - 200")
    public void createUniqUser() {
        Boolean success = true;
        int statusCode = 200;

        User user = new User()
                .withEmail(randomString(6) + "@yandex.ru")
                .withPassword(randomString(6))
                .withName(randomString(6));
        UserClient userClient = new UserClient();
        Response response = userClient.createUser(user);

        userClient.checkStatusCode(response, statusCode);
        userClient.checkResponseBodyNotEmpty(response);
        userClient.checkIsSuccessResponse(response, success);

        accessToken = response.body().path("accessToken").toString().substring(7);
    }

    // Создание пользователя.Cоздание пользователя, который уже зарегистрирован.

    @Test
    @DisplayName("Создание пользователя.Cоздание пользователя, который уже зарегистрирован.")
    @Description("Повторное создание пользователя с теми же данными. Запрос возвращает код ответа - 403")
    public void createAlreadyExistsUser() {
        Boolean success = false;
        int statusCode = 403;
        String message = "User already exists";

        User user = new User()
                .withEmail(randomString(6) + "@yandex.ru")
                .withPassword(randomString(6))
                .withName(randomString(6));
        UserClient userClient = new UserClient();
        Response response = userClient.createUser(user);
        accessToken = response.body().path("accessToken").toString().substring(7);

        Response repeateResponse = userClient.createUser(user);

        userClient.checkStatusCode(repeateResponse, statusCode);
        userClient.checkResponseBodyNotEmpty(repeateResponse);
        userClient.checkIsSuccessResponse(repeateResponse, success);
        userClient.checkResponseBodyMessage(repeateResponse, message);
    }

    @Test
    @DisplayName("Создание пользователя без поля \"email\"")
    @Description("При создании пользователя без поля \"email\" возвращается код 403 и \"message\": \"Email, password and name are required fields")
    public void createUserWithoutEmail() {
        Boolean success = false;
        int statusCode = 403;
        String message = "Email, password and name are required fields";

        User userWithoutEmail = new User()
                .withEmail(randomString(6) + "@yandex.ru")
                .withName(randomString(6));
        UserClient userClient = new UserClient();
        Response response = userClient.createUser(userWithoutEmail);

        userClient.checkStatusCode(response, statusCode);
        userClient.checkResponseBodyNotEmpty(response);
        userClient.checkIsSuccessResponse(response, success);
        userClient.checkResponseBodyMessage(response, message);
    }

    @Test
    @DisplayName("Создание пользователя без поля \"password\"")
    @Description("При создании пользователя без поля \"password\" возвращается код 403 и \"message\": \"Email, password and name are required fields")
    public void createUserWithoutPassword() {
        Boolean success = false;
        int statusCode = 403;
        String message = "Email, password and name are required fields";

        User userWithoutPassword = new User()
                .withEmail(randomString(6) + "@yandex.ru")
                .withName(randomString(6));
        UserClient userClient = new UserClient();
        Response response = userClient.createUser(userWithoutPassword);

        userClient.checkStatusCode(response, statusCode);
        userClient.checkResponseBodyNotEmpty(response);
        userClient.checkIsSuccessResponse(response, success);
        userClient.checkResponseBodyMessage(response, message);
    }

    @Test
    @DisplayName("Создание пользователя без поля \"name\"")
    @Description("При создании пользователя без поля \"name\" возвращается код 403 и \"message\": \"Email, password and name are required fields")
    public void createUserWithoutName() {
        Boolean success = false;
        int statusCode = 403;
        String message = "Email, password and name are required fields";

        User userWithoutName = new User()
                .withEmail(randomString(6) + "@yandex.ru")
                .withName(randomString(6));
        UserClient userClient = new UserClient();
        Response response = userClient.createUser(userWithoutName);

        userClient.checkStatusCode(response, statusCode);
        userClient.checkResponseBodyNotEmpty(response);
        userClient.checkIsSuccessResponse(response, success);
        userClient.checkResponseBodyMessage(response, message);
    }
}
