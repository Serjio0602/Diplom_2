package client;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RestAssuredClient {

    protected static final String BASE_URL = "https://qa-stellarburgers.education-services.ru/";

    protected RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .build();
    }
}
