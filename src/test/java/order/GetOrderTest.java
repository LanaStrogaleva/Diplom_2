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

public class GetOrderTest {

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

        OrderClient orderClient = new OrderClient();

        Order order = new Order();
        order.addIngredientToOrder(orderClient.getIngredientId());
        order.addIngredientToOrder(orderClient.getIngredientId());

        orderClient.createOrderWithAuth(accessToken, order);
    }

    @After
    public void TearDown() {
        if (accessToken != null) {
            UserClient userClient = new UserClient();
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя (авторизованный пользователь)")
    @Description("Получение заказов конкретного пользователя (авторизованный пользователь). Запрос возвращает код ответа - 200")
    public void createOrderWithAuth() {

        int statusCode = 200;
        Boolean success = true;

        OrderClient orderClient = new OrderClient();

        Response response = orderClient.getOrderListWithAuth(accessToken);

        orderClient.checkStatusCode(response, statusCode);
        orderClient.checkIsSuccessResponse(response, success);
        orderClient.checkResponseOrdersBodyNotEmpty(response);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя (неавторизованный пользователь)")
    @Description("Получение заказов конкретного пользователя (неавторизованный пользователь). Запрос возвращает код ответа - 401")
    public void createOrderWithoutAuth() {

        int statusCode = 401;
        Boolean success = false;
        String message = "You should be authorised";

        OrderClient orderClient = new OrderClient();

        Response response = orderClient.getOrderListWithoutAuth();

        orderClient.checkStatusCode(response, statusCode);
        orderClient.checkIsSuccessResponse(response, success);
        orderClient.checkResponseBodyMessage(response, message);
    }

}
