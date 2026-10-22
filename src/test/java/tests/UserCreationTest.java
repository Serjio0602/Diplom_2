package tests;

import client.UserClient;
import helper.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserCreationTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void  setUp() {
        userClient = new UserClient();
        user = UserGenerator.getRandomUser();
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    public void createUserTest(){
        Response response = userClient.register(user);

        response.then()
                .assertThat()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());

        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Ошибка 403 при создании существующего пользователя")
    public void createExistingUserTest(){
        Response response = userClient.register(user);
        response.then()
                .assertThat()
                .statusCode(200);

        Response response2 = userClient.register(user);
        response2.then()
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Ошибка 403 при создании пользователя с незаполненным обязательным полем")
    public void createUserWithoutRequiredFieldTest(){

        User userWithoutRequiredField = new User(UserGenerator.getRandomUser().getEmail(), UserGenerator.getRandomUser().getPassword());
        Response response = userClient.register(userWithoutRequiredField);
        response.then()
                .assertThat()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown(){
        if(accessToken != null){
            userClient.delete(accessToken);
        }
    }
}
