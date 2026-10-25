package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssuredClient{

    private static final String ORDER_PATH = "/api/orders";
    private static final String INGREDIENTS_PATH = "/api/ingredients";

    @Step("Создание заказа")
    public Response createOrder(Order order, String accessToken) {

        var request = given()
                .spec(getBaseSpec());

        if (accessToken != null) {
            request.header("Authorization", accessToken);
        }
        return request
                .body(order)
                .when()
                .post(ORDER_PATH);
    }

    @Step("Получение списка ингредиентов")
    public Response getIngredients() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(INGREDIENTS_PATH);
    }
}
