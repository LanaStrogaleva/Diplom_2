package user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static user.Utils.randomString;

public class ChangeUserDataTest {
    String accessToken;


    @Before
    public void SetUp() {
        UserClient userClient = new UserClient();
        userClient.setBaseURL();

        User user = new User()
                .withEmail(randomString(6) + "@yandex.ru")
                .withPassword(randomString(6))
                .withName(randomString(6));

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
        Boolean success = true;
        int statusCode = 200;
        String newName = randomString(7);
        String newEmail = randomString(7) + "@mail.ru";

        UserClient userClient = new UserClient();

        User newUser = new User().withEmail(newEmail).withName(newName);

        Response changeResponse = userClient.changeUserDataWithAuth(accessToken, newUser);

        userClient.checkStatusCode(changeResponse, statusCode);
        userClient.checkResponseBodyNotEmpty(changeResponse);
        userClient.checkIsSuccessResponse(changeResponse, success);
        userClient.checkEmailResponseBody(changeResponse, newUser.getEmail());
        userClient.checkNameResponseBody(changeResponse, newUser.getName());

    }


    @Test
    @DisplayName("Изменение данных пользователя (без авторизации)")
    @Description("Изменение имени email пользователя (без авторизации). Запрос возвращает код ответа - 401")
    public void changeUserDataWithoutAuth() {
        Boolean success = false;
        int statusCode = 401;
        String message = "You should be authorised";
        String newName = randomString(7);
        String newEmail = randomString(7) + "@mail.ru";

        UserClient userClient = new UserClient();

        User newUser = new User().withEmail(newEmail).withName(newName);

        Response changeResponse = userClient.changeUserDataWithoutAuth(newUser);

        userClient.checkStatusCode(changeResponse, statusCode);
        userClient.checkResponseBodyNotEmpty(changeResponse);
        userClient.checkIsSuccessResponse(changeResponse, success);
        userClient.checkResponseBodyMessage(changeResponse, message);
    }
}
