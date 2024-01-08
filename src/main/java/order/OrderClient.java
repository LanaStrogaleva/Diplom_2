package order;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;
import static user.Utils.randomInteger;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static String GET_INGREDIENTS_URL = "/api/ingredients";
    private static String CREATE_ORDER_URL = "/api/orders";

    private static String GET_ORDER_URL = "/api/orders";


    @Step("Получение id ингридиента ")
    public String getIngredientId() {
        return RestAssured
                .given()
                .when()
                .get(GET_INGREDIENTS_URL)
                .body().path("data._id[" + randomInteger() + "]");
    }


    @Step("Coздание заказа с авторизацией")
    public Response createOrderWithAuth(String token, Order order) {
        return given()
                .auth()
                .oauth2(token)
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post(CREATE_ORDER_URL);
    }

    @Step("Coздание заказа без авторизациии")
    public Response createOrderWithoutAuth(Order order) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post(CREATE_ORDER_URL);
    }

    @Step("Получение заказов конкретного пользователя (авторизованный пользователь)")
    public Response getOrderListWithAuth(String token) {
        return given()
                .auth()
                .oauth2(token)
                .when()
                .get(GET_ORDER_URL);
    }

    @Step("Получение заказов конкретного пользователя (неавторизованный пользователь)")
    public Response getOrderListWithoutAuth() {
        return given()
                .when()
                .get(GET_ORDER_URL);
    }

    @Step("Проверить статус код")
    public void checkStatusCode(Response response, int statusCode) {
        response.then().assertThat()
                .statusCode(statusCode);
    }

    @Step("Проверить сообщение в теле ответа")
    public void checkResponseBodyMessage(Response response, String message) {
        response.then().assertThat()
                .body("message", equalTo(message));
        System.out.println(response.body().path("message").toString());
    }

    @Step("Проверить успешность выполнения в теле ответа")
    public void checkIsSuccessResponse(Response response, Boolean success) {
        response.then().assertThat()
                .body("success", equalTo(success));
    }

    @Step("Проверить, что поле orders в теле ответа не пустое")
    public void checkResponseOrdersBodyNotEmpty(Response response) {
        response.then().assertThat()
                .body("orders", notNullValue());
    }

    @Step("Проверить, что поле order в теле ответа не пустое")
    public void checkResponseOrderBodyNotEmpty(Response response) {
        response.then().assertThat()
                .body("order", notNullValue());
    }

    @Step("Проверить order в теле ответа")
    public void checkOrderResponseBody(Response response, String value) {
        response.then().assertThat()
                .body("order", equalTo(value));
    }
}
