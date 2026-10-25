package helper;

import com.github.javafaker.Faker;
import model.User;

public class UserGenerator {

    private static final Faker faker = new Faker();

    public static User getRandomUser() {
        return new User(
                faker.internet().emailAddress(),
                faker.internet().password(6, 10),
                faker.name().firstName());
    }
}
