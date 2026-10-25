package tests;

import client.OrderClient;
import client.UserClient;
import helper.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Order;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class OrderCreationTest {

    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp(){
        userClient = new UserClient();
        orderClient = new OrderClient();
        User user = UserGenerator.getRandomUser();

        Response registerResponse = userClient.register(user);
        accessToken = registerResponse.path("accessToken");
    }

    @Test
    @DisplayName("Успешный заказ с авторизацией")
    public void createOrderWithAuthTest(){

        Response ingredientsResponse = orderClient.getIngredients();
        List<String> ingredientIds = ingredientsResponse.path("data._id");
        List<String> selectedIngredients = ingredientIds.subList(3, 5);

        Order order = new Order(selectedIngredients);
        Response orderResponse = orderClient.createOrder(order, accessToken);
        orderResponse.then()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Успешный заказ без авторизации")
    public void createOrderWithNoAuthTest(){
        Response ingredientsResponse = orderClient.getIngredients();
        List<String> ingredientIds = ingredientsResponse.path("data._id");
        List<String> selectedIngredients = ingredientIds.subList(0, 2);

        Order order = new Order(selectedIngredients);
        Response orderResponse = orderClient.createOrder(order, null);
        orderResponse.then()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Успешный заказ с ингредиентами")
    public void createOrderWithIngredientsTest(){

        Response ingredientsResponse = orderClient.getIngredients();
        List<String> ingredientIds = ingredientsResponse.path("data._id");
        List<String> selectedIngredients = ingredientIds.subList(1, 4);

        Order order = new Order(selectedIngredients);
        Response orderResponse = orderClient.createOrder(order, accessToken);
        orderResponse.then()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Ошибка 400 при заказе без ингредиентов")
    public void createOrderWithNoIngredientsTest(){

        Order order = new Order();
        Response orderResponse = orderClient.createOrder(order, accessToken);
        orderResponse.then()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Ошибка 500 при передаче неверного хэша ингредиента")
    public void createOrderWithIncorrectHashTest() {

        List<String> incorrectHashIngredient = List.of("ingredient_hash_123");

        Order order = new Order(incorrectHashIngredient);
        Response orderResponse = orderClient.createOrder(order, accessToken);
        orderResponse.then()
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    public void tearDown(){
        if(accessToken != null){
            userClient.delete(accessToken);
        }
    }
}
