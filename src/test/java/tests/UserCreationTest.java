package tests;

import client.UserClient;
import helper.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
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
                .statusCode(SC_OK)
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
                .statusCode(SC_OK);

        Response response2 = userClient.register(user);
        response2.then()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
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
                .statusCode(SC_FORBIDDEN)
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
