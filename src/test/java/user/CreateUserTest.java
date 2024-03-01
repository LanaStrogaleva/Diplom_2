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

public class CreateUserTest {
    String accessToken;

    @Before
    public void SetUp() {
        UserClient userClient = new UserClient();
        userClient.setBaseURL();
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
        Faker faker = new Faker();
        Boolean success = true;

        User user = new User()
                .withEmail(faker.internet().emailAddress())
                .withPassword(faker.internet().password(6,7))
                .withName(faker.name().name());
        UserClient userClient = new UserClient();
        Response response = userClient.createUser(user);

        userClient.checkStatusCode(response, HttpStatus.SC_OK);
        userClient.checkResponseBodyNotEmpty(response);
        userClient.checkIsSuccessResponse(response, success);

        accessToken = response.body().path("accessToken").toString().substring(7);
    }


    @Test
    @DisplayName("Создание пользователя.Cоздание пользователя, который уже зарегистрирован.")
    @Description("Повторное создание пользователя с теми же данными. Запрос возвращает код ответа - 403")
    public void createAlreadyExistsUser() {
        Faker faker = new Faker();
        Boolean success = false;
        String message = "User already exists";

        User user = new User()
                .withEmail(faker.internet().emailAddress())
                .withPassword(faker.internet().password(6,7))
                .withName(faker.name().name());
        UserClient userClient = new UserClient();
        Response response = userClient.createUser(user);
        accessToken = response.body().path("accessToken").toString().substring(7);

        Response repeateResponse = userClient.createUser(user);

        userClient.checkStatusCode(repeateResponse, HttpStatus.SC_FORBIDDEN);
        userClient.checkResponseBodyNotEmpty(repeateResponse);
        userClient.checkIsSuccessResponse(repeateResponse, success);
        userClient.checkResponseBodyMessage(repeateResponse, message);
    }

    @Test
    @DisplayName("Создание пользователя без поля \"email\"")
    @Description("При создании пользователя без поля \"email\" возвращается код 403 и \"message\": \"Email, password and name are required fields")
    public void createUserWithoutEmail() {
        Faker faker = new Faker();
        Boolean success = false;
        String message = "Email, password and name are required fields";

        User userWithoutEmail = new User()
                .withEmail(faker.internet().emailAddress())
                .withName(faker.name().name());
        UserClient userClient = new UserClient();
        Response response = userClient.createUser(userWithoutEmail);

        userClient.checkStatusCode(response, HttpStatus.SC_FORBIDDEN);
        userClient.checkResponseBodyNotEmpty(response);
        userClient.checkIsSuccessResponse(response, success);
        userClient.checkResponseBodyMessage(response, message);
    }

    @Test
    @DisplayName("Создание пользователя без поля \"password\"")
    @Description("При создании пользователя без поля \"password\" возвращается код 403 и \"message\": \"Email, password and name are required fields")
    public void createUserWithoutPassword() {
        Faker faker = new Faker();
        Boolean success = false;
        String message = "Email, password and name are required fields";

        User userWithoutPassword = new User()
                .withEmail(faker.internet().emailAddress())
                .withName(faker.name().name());
        UserClient userClient = new UserClient();
        Response response = userClient.createUser(userWithoutPassword);

        userClient.checkStatusCode(response, HttpStatus.SC_FORBIDDEN);
        userClient.checkResponseBodyNotEmpty(response);
        userClient.checkIsSuccessResponse(response, success);
        userClient.checkResponseBodyMessage(response, message);
    }

    @Test
    @DisplayName("Создание пользователя без поля \"name\"")
    @Description("При создании пользователя без поля \"name\" возвращается код 403 и \"message\": \"Email, password and name are required fields")
    public void createUserWithoutName() {
        Faker faker = new Faker();
        Boolean success = false;
        String message = "Email, password and name are required fields";

        User userWithoutName = new User()
                .withEmail(faker.internet().emailAddress())
                .withName(faker.name().name());
        UserClient userClient = new UserClient();
        Response response = userClient.createUser(userWithoutName);

        userClient.checkStatusCode(response, HttpStatus.SC_FORBIDDEN);
        userClient.checkResponseBodyNotEmpty(response);
        userClient.checkIsSuccessResponse(response, success);
        userClient.checkResponseBodyMessage(response, message);
    }
}
