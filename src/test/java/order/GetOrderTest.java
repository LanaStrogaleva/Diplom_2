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

public class GetOrderTest {

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

        Boolean success = true;

        OrderClient orderClient = new OrderClient();

        Response response = orderClient.getOrderListWithAuth(accessToken);

        orderClient.checkStatusCode(response, HttpStatus.SC_OK);
        orderClient.checkIsSuccessResponse(response, success);
        orderClient.checkResponseOrdersBodyNotEmpty(response);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя (неавторизованный пользователь)")
    @Description("Получение заказов конкретного пользователя (неавторизованный пользователь). Запрос возвращает код ответа - 401")
    public void createOrderWithoutAuth() {

        Boolean success = false;
        String message = "You should be authorised";

        OrderClient orderClient = new OrderClient();

        Response response = orderClient.getOrderListWithoutAuth();

        orderClient.checkStatusCode(response, HttpStatus.SC_UNAUTHORIZED);
        orderClient.checkIsSuccessResponse(response, success);
        orderClient.checkResponseBodyMessage(response, message);
    }

}
