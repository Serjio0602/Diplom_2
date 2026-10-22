package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssuredClient{

    private static final String ORDER_PATH = "/api/orders";

    @Step("Создание заказа")
    public Response createOrder(Order order, String accessToken) {

        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken != null ? accessToken : "")
                .body(order)
                .when()
                .post(ORDER_PATH);
    }
}
