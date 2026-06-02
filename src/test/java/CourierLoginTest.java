import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;

public class CourierLoginTest {

    private CourierSteps courierSteps;
    private List<String> createdLogins;
    private List<String> createdPasswords;

    @Before
    @Step("Настройка тестового окружения")
    public void setUp() {
        RestAssured.baseURI = Config.BASE_URL;
        courierSteps = new CourierSteps();
        createdLogins = new ArrayList<>();
        createdPasswords = new ArrayList<>();
    }

    @After
    @Step("Очистка тестовых данных: удаление созданных курьеров")
    public void cleanUp() {
        for (int i = 0; i < createdLogins.size(); i++) {
            try {
                String login = createdLogins.get(i);
                String password = createdPasswords.get(i);
                Courier tempCourier = new Courier(login, password, null);
                int courierId = courierSteps.loginCourier(tempCourier)
                        .statusCode(200)
                        .extract()
                        .path("id");
                courierSteps.deleteCourier(courierId).statusCode(200);
                System.out.println("Курьер с логином " + login + " успешно удален");
            } catch (Exception e) {
                System.out.println("Не удалось удалить курьера с логином: " + createdLogins.get(i));
            }
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться с правильными логином и паролем")
    @Description("Проверка успешной авторизации существующего курьера")
    public void courierCanLoginWithValidCredentials() {

        String uniqueLogin = "logintest_" + System.currentTimeMillis();
        String password = "1234";
        Courier courier = new Courier(uniqueLogin, password, "Иван");
        createdLogins.add(uniqueLogin);
        createdPasswords.add(password);

        // Создаем курьера
        courierSteps.createCourier(courier).statusCode(201);

        // Авторизуемся
        courierSteps.loginCourier(courier)
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Авторизация невозможна без логина")
    @Description("При отсутствии логина сервер возвращает 400 с сообщением об ошибке")
    public void loginRequiresLogin() {
        String uniqueLogin = "mandatory_" + System.currentTimeMillis();
        String password = "1234";
        Courier courier = new Courier(uniqueLogin, password, "Иван");
        createdLogins.add(uniqueLogin);
        createdPasswords.add(password);

        // Создаем курьера
        courierSteps.createCourier(courier).statusCode(201);

        // Пытаемся авторизоваться без логина
        Courier courierWithoutLogin = new Courier(null, password, null);
        courierSteps.loginCourier(courierWithoutLogin)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация невозможна без пароля")
    @Description("При отсутствии пароля сервер возвращает 400 с сообщением об ошибке")
    public void loginRequiresPassword() {
        String uniqueLogin = "mandatory_" + System.currentTimeMillis();
        String password = "1234";
        Courier courier = new Courier(uniqueLogin, password, "Иван");
        createdLogins.add(uniqueLogin);
        createdPasswords.add(password);

        // Создаем курьера
        courierSteps.createCourier(courier).statusCode(201);

        // Пытаемся авторизоваться без пароля
        Courier courierWithoutPassword = new Courier(uniqueLogin, null, null);
        courierSteps.loginCourier(courierWithoutPassword)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Система возвращает ошибку при неправильном логине")
    @Description("Проверка, что при авторизации с неверным логином возвращается ошибка 404")
    public void loginWithWrongLoginReturnsError() {

        String uniqueLogin = "correct_" + System.currentTimeMillis();
        String password = "1234";
        Courier correctCourier = new Courier(uniqueLogin, password, "Иван");
        createdLogins.add(uniqueLogin);
        createdPasswords.add(password);

        // Создаем курьера
        courierSteps.createCourier(correctCourier).statusCode(201);

        // Пытаемся авторизоваться с неправильным логином
        Courier wrongLoginCourier = new Courier("wrong_" + System.currentTimeMillis(), password, null);
        courierSteps.loginCourier(wrongLoginCourier)
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Система возвращает ошибку при неправильном пароле")
    @Description("Проверка, что при авторизации с неверным паролем возвращается ошибка 404")
    public void loginWithWrongPasswordReturnsError() {

        String uniqueLogin = "correctpass_" + System.currentTimeMillis();
        String correctPassword = "1234";
        Courier correctCourier = new Courier(uniqueLogin, correctPassword, "Иван");
        createdLogins.add(uniqueLogin);
        createdPasswords.add(correctPassword);

        // Создаем курьера
        courierSteps.createCourier(correctCourier).statusCode(201);

        // Пытаемся авторизоваться с неправильным паролем
        Courier wrongPasswordCourier = new Courier(uniqueLogin, "wrong_password", null);
        courierSteps.loginCourier(wrongPasswordCourier)
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Возвращается ошибка, если поле логин отсутствует")
    @Description("Проверка, что запрос без поля login возвращает ошибку 400")
    public void loginWithoutLoginFieldReturnsError() {

        given()
                .header("Content-type", "application/json")
                .body("{\"password\": \"1234\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Возвращается ошибка, если поле пароль отсутствует")
    @Description("Проверка, что запрос без поля password возвращает ошибку 400")
    public void loginWithoutPasswordFieldReturnsError() {

        given()
                .header("Content-type", "application/json")
                .body("{\"login\": \"some_login\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Возвращается ошибка, если тело запроса пустое")
    @Description("Проверка, что пустое тело запроса возвращает ошибку 400")
    public void loginWithEmptyBodyReturnsError() {

        given()
                .header("Content-type", "application/json")
                .body("{}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация с несуществующим пользователем возвращает ошибку")
    @Description("Проверка, что попытка авторизации несуществующего пользователя возвращает 404")
    public void loginWithNonExistentUserReturnsError() {

        String nonExistentLogin = "nonexistent_" + System.currentTimeMillis();
        String password = "1234";
        Courier nonExistentCourier = new Courier(nonExistentLogin, password, null);

        courierSteps.loginCourier(nonExistentCourier)
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Успешный запрос авторизации возвращает id курьера")
    @Description("Проверка, что при успешной авторизации в ответе приходит корректный id")
    public void successfulLoginReturnsCourierId() {

        String uniqueLogin = "idtest_" + System.currentTimeMillis();
        String password = "1234";
        Courier courier = new Courier(uniqueLogin, password, "Иван");
        createdLogins.add(uniqueLogin);
        createdPasswords.add(password);

        // Создаем курьера
        courierSteps.createCourier(courier).statusCode(201);

        Integer courierId = courierSteps.loginCourier(courier)
                .statusCode(200)
                .extract()
                .path("id");

        assertThat("ID курьера должен быть положительным числом", courierId, greaterThan(0));
        assertThat("ID курьера должен быть целым числом", courierId, notNullValue());
    }

}