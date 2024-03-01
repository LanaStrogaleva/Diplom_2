package user;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matcher;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

public class UserClient {
    public static String BASE_URL = "https://stellarburgers.nomoreparties.site";
    private static String CREATE_USER_URL = "/api/auth/register";
    private static String LOGIN_USER_URL = "/api/auth/login";
    private static String GET_USER_DATA_URL = "/api/auth/user";
    private static String CHANGE_USER_DATA_URL = "/api/auth/user";
    private static String DELETE_USER_URL = "/api/auth/user";

    @Step("Перейти по базовому URL")
    public void setBaseURL() {
        RestAssured.baseURI = BASE_URL;
    }


    @Step("Создать пользователя {user}")
    public Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(CREATE_USER_URL);
    }

    @Step("Логин пользователя {user}")
    public Response loginUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(LOGIN_USER_URL);
    }

    @Step("Получить данные пользователя {user}")
    public Response getUserData(String accessToken) {
        return given()
                .auth()
                .oauth2(accessToken)
                .when()
                .get(GET_USER_DATA_URL);
    }

    @Step("Изменить данные пользователя {user} с авторизацией")
    public Response changeUserDataWithAuth(String accessToken,User user) {
        return given()
                .auth()
                .oauth2(accessToken)
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .patch(CHANGE_USER_DATA_URL);
    }

    @Step("Изменить данные пользователя {user} без авторизации")
    public Response changeUserDataWithoutAuth(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .when()
                .patch(CHANGE_USER_DATA_URL);
    }

    @Step("Удалить пользователя")
    public void deleteUser(String accessToken) {
        given()
                .auth()
                .oauth2(accessToken)
                .when()
                .delete(DELETE_USER_URL)
                .body().asString();
    }

    @Step("Проверить статус код")
    public void checkStatusCode(Response response, int httpStatus) {
        response.then().assertThat()
                .statusCode(httpStatus);
    }

    @Step("Проверить сообщение в теле ответа")
    public void checkResponseBodyMessage(Response response, String message ) {
        response.then().assertThat()
                .body("message", equalTo(message));
        System.out.println(response.body().path("message").toString());
    }

    @Step("Проверить имя пользователя в теле ответа")
    public void checkNameResponseBody(Response response, String value ) {
        response.then().assertThat()
                .body("user.name", equalTo(value));
    }

    @Step("Проверить email пользователя в теле ответа")
    public void checkEmailResponseBody(Response response, String value ) {
        response.then().assertThat()
                .body("user.email", equalTo(value));
    }


    @Step("Проверить успешность выполнения в теле ответа")
    public void checkIsSuccessResponse(Response response, Boolean success ) {
        response.then().assertThat()
                .body("success", equalTo(success));
        System.out.println(response.body().path("success").toString());
    }

    @Step("Проверить, что тело ответа не пустое")
    public void checkResponseBodyNotEmpty(Response response) {
        response.then().assertThat()
                .body(notNullValue());
    }

}


