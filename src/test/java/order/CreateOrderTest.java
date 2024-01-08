package order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.User;
import user.UserClient;

import static user.Utils.randomString;

public class CreateOrderTest {
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
    @DisplayName("Создание заказа:с авторизацией")
    @Description("Создание заказа:с авторизацией. Запрос возвращает код ответа - 200")
    public void createOrderWithAuth() {

        int statusCode = 200;
        Boolean success = true;

        OrderClient orderClient = new OrderClient();

        Order order = new Order();
        order.addIngredientToOrder(orderClient.getIngredientId());

        Response response = orderClient.createOrderWithAuth(accessToken, order);

        orderClient.checkStatusCode(response, statusCode);
        orderClient.checkIsSuccessResponse(response, success);
        orderClient.checkResponseOrderBodyNotEmpty(response);
    }

    @Test
    @DisplayName("Создание заказа:без авторизациии")
    @Description("Создание заказа:без авторизациии. Заказ не создан")
    public void createOrderWithoutAuth() {

        OrderClient orderClient = new OrderClient();

        Order order = new Order();
        order.addIngredientToOrder(orderClient.getIngredientId());

        Response response = orderClient.createOrderWithoutAuth(order);

        orderClient.checkOrderResponseBody(response, null);
    }

    @Test
    @DisplayName("Создание заказа:с ингредиентами")
    @Description("Создание заказа:с ингредиентами. Запрос возвращает код ответа - 200")
    public void createOrderWithIngredients() {
        int statusCode = 200;
        Boolean success = true;

        OrderClient orderClient = new OrderClient();

        Order order = new Order();
        order.addIngredientToOrder(orderClient.getIngredientId());

        Response response = orderClient.createOrderWithAuth(accessToken, order);

        orderClient.checkStatusCode(response, statusCode);
        orderClient.checkIsSuccessResponse(response, success);
        orderClient.checkResponseOrderBodyNotEmpty(response);
    }
    // Создание заказа:без ингредиентов

    @Test
    @DisplayName("Создание заказа:без ингредиентов")
    @Description("Создание заказа:без ингредиентов. Запрос возвращает код ответа - 400")
    public void createOrderWithoutIngredients() {
        int statusCode = 400;
        Boolean success = false;
        String message = "Ingredient ids must be provided";

        OrderClient orderClient = new OrderClient();

        Order order = new Order();

        Response response = orderClient.createOrderWithAuth(accessToken, order);

        orderClient.checkStatusCode(response, statusCode);
        orderClient.checkIsSuccessResponse(response, success);
        orderClient.checkResponseBodyMessage(response, message);
    }

    @Test
    @DisplayName("Создание заказа:с неверным хешем ингредиентов")
    @Description("Создание заказа:с неверным хешем ингредиентов. Запрос возвращает код ответа - 500")
    public void createOrderWithIncorrectIngredientHash() {
        int statusCode = 500;

        OrderClient orderClient = new OrderClient();

        Order order = new Order();

        order.addIngredientToOrder(orderClient.getIngredientId() + randomString(3));
        Response response = orderClient.createOrderWithAuth(accessToken, order);

        orderClient.checkStatusCode(response, statusCode);
    }

}
