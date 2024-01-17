package order;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
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
    @DisplayName("Создание заказа:с авторизацией")
    @Description("Создание заказа:с авторизацией. Запрос возвращает код ответа - 200")
    public void createOrderWithAuth() {

        Boolean success = true;

        OrderClient orderClient = new OrderClient();

        Order order = new Order();
        order.addIngredientToOrder(orderClient.getIngredientId());

        Response response = orderClient.createOrderWithAuth(accessToken, order);

        orderClient.checkStatusCode(response, HttpStatus.SC_OK);
        orderClient.checkIsSuccessResponse(response, success);
        orderClient.checkResponseOrderBodyNotEmpty(response);
    }

    @Test
    @DisplayName("Создание заказа:без авторизациии")
    @Description("Создание заказа:без авторизациии. Заказ не создан")
    public void createOrderWithoutAuth() {
        Boolean success = true;

        OrderClient orderClient = new OrderClient();

        Order order = new Order();
        order.addIngredientToOrder(orderClient.getIngredientId());

        Response response = orderClient.createOrderWithoutAuth(order);

        orderClient.checkStatusCode(response, HttpStatus.SC_OK);
        orderClient.checkIsSuccessResponse(response, success);
        orderClient.checkResponseOrderBodyNotEmpty(response);
    }

    @Test
    @DisplayName("Создание заказа:с ингредиентами")
    @Description("Создание заказа:с ингредиентами. Запрос возвращает код ответа - 200")
    public void createOrderWithIngredients() {

        Boolean success = true;

        OrderClient orderClient = new OrderClient();

        Order order = new Order();
        order.addIngredientToOrder(orderClient.getIngredientId());

        Response response = orderClient.createOrderWithAuth(accessToken, order);

        orderClient.checkStatusCode(response, HttpStatus.SC_OK);
        orderClient.checkIsSuccessResponse(response, success);
        orderClient.checkResponseOrderBodyNotEmpty(response);
    }
    // Создание заказа:без ингредиентов

    @Test
    @DisplayName("Создание заказа:без ингредиентов")
    @Description("Создание заказа:без ингредиентов. Запрос возвращает код ответа - 400")
    public void createOrderWithoutIngredients() {

        Boolean success = false;
        String message = "Ingredient ids must be provided";

        OrderClient orderClient = new OrderClient();

        Order order = new Order();

        Response response = orderClient.createOrderWithAuth(accessToken, order);

        orderClient.checkStatusCode(response, HttpStatus.SC_BAD_REQUEST);
        orderClient.checkIsSuccessResponse(response, success);
        orderClient.checkResponseBodyMessage(response, message);
    }

    @Test
    @DisplayName("Создание заказа:с неверным хешем ингредиентов")
    @Description("Создание заказа:с неверным хешем ингредиентов. Запрос возвращает код ответа - 500")
    public void createOrderWithIncorrectIngredientHash() {

        OrderClient orderClient = new OrderClient();

        Order order = new Order();

        order.addIngredientToOrder(orderClient.getIngredientId() + randomString(3));
        Response response = orderClient.createOrderWithAuth(accessToken, order);

        orderClient.checkStatusCode(response, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

}
