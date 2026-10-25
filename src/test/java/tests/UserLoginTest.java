package tests;

import client.UserClient;
import helper.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserLoginTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getRandomUser();

        Response response = userClient.register(user);
        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Успешный логин существующего пользователя")
    public void loginUserTest(){


        Response response = userClient.login(user);
        response.then()
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());

        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Ошибка 401 при логине с неверным паролем")
    public void loginWithIncorrectPasswordTest(){

        User incorrectPassUser = new User(user.getEmail(), "wrongPassword111");
        Response response = userClient.login(incorrectPassUser);
        response.then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Ошибка 401 при логине с неверным email")
    public void loginWithIncorrectEmailTest(){

        User incorrectEmailUser = new User("wrongEmail333@ya.ru", user.getPassword());
        Response response = userClient.login(incorrectEmailUser);
        response.then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }
}
