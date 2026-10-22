package client;

import io.restassured.response.Response;
import model.User;

import static io.restassured.RestAssured.given;

public class UserClient extends RestAssuredClient{

    private static final String REGISTER_PATH = "/api/auth/register";
    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String USER_PATH = "/api/auth/user";

    public Response register(User user){
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(REGISTER_PATH);
    }

    public Response login(User user){
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(LOGIN_PATH);
    }

    public Response delete(String accessToken){
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(USER_PATH);
    }
}
