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

public class ChangeUserDataTest {
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
    @DisplayName("Изменение данных пользователя (с авторизацией)")
    @Description("Изменение имени и email пользователя (с авторизацией). Запрос возвращает код ответа - 200")
    public void changeUserDataAuth() {
        Faker faker = new Faker();
        Boolean success = true;
        String newName = faker.name().name();
        String newEmail = faker.internet().emailAddress();

        UserClient userClient = new UserClient();

        User newUser = new User().withEmail(newEmail).withName(newName);

        Response changeResponse = userClient.changeUserDataWithAuth(accessToken, newUser);

        userClient.checkStatusCode(changeResponse, HttpStatus.SC_OK);
        userClient.checkResponseBodyNotEmpty(changeResponse);
        userClient.checkIsSuccessResponse(changeResponse, success);
        userClient.checkEmailResponseBody(changeResponse, newUser.getEmail());
        userClient.checkNameResponseBody(changeResponse, newUser.getName());

    }


    @Test
    @DisplayName("Изменение данных пользователя (без авторизации)")
    @Description("Изменение имени email пользователя (без авторизации). Запрос возвращает код ответа - 401")
    public void changeUserDataWithoutAuth() {
        Faker faker = new Faker();
        Boolean success = false;
        String message = "You should be authorised";
        String newName = faker.name().name();
        String newEmail = faker.internet().emailAddress();

        UserClient userClient = new UserClient();

        User newUser = new User().withEmail(newEmail).withName(newName);

        Response changeResponse = userClient.changeUserDataWithoutAuth(newUser);

        userClient.checkStatusCode(changeResponse, HttpStatus.SC_UNAUTHORIZED);
        userClient.checkResponseBodyNotEmpty(changeResponse);
        userClient.checkIsSuccessResponse(changeResponse, success);
        userClient.checkResponseBodyMessage(changeResponse, message);
    }
}
