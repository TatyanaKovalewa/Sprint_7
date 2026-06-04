import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class CourierLoginTest {

    private CourierSteps courierSteps;
    private List<String> createdLogins;
    private List<String> createdPasswords;

    // Данные для тестового курьера
    private String testLogin;
    private String testPassword;
    private Courier testCourier;

    @Before
    @Step("Настройка тестового окружения")
    public void setUp() {
        RestAssured.baseURI = Config.BASE_URL;
        courierSteps = new CourierSteps();
        createdLogins = new ArrayList<>();
        createdPasswords = new ArrayList<>();

        // Создаем тестового курьера
        testLogin = "logintest_" + System.currentTimeMillis();
        testPassword = "1234";
        testCourier = new Courier(testLogin, testPassword, "Иван");
        createdLogins.add(testLogin);
        createdPasswords.add(testPassword);

        courierSteps.createCourier(testCourier).statusCode(SC_CREATED);

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
                        .statusCode(SC_OK)
                        .extract()
                        .path("id");
                courierSteps.deleteCourier(courierId).statusCode(SC_OK);
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
        courierSteps.loginCourier(testCourier)
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Авторизация невозможна без логина")
    @Description("При отсутствии логина сервер возвращает 400 с сообщением об ошибке")
    public void loginRequiresLogin() {
        // Пытаемся авторизоваться без логина
        Courier courierWithoutLogin = new Courier(null, testPassword, null);
        courierSteps.loginCourier(courierWithoutLogin)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация невозможна без пароля")
    @Description("При отсутствии пароля сервер возвращает 400 с сообщением об ошибке")
    public void loginRequiresPassword() {
        // Пытаемся авторизоваться без пароля
        Courier courierWithoutPassword = new Courier(testLogin, null, null);
        courierSteps.loginCourier(courierWithoutPassword)
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Система возвращает ошибку при неправильном логине")
    @Description("Проверка, что при авторизации с неверным логином возвращается ошибка 404")
    public void loginWithWrongLoginReturnsError() {
        // Пытаемся авторизоваться с неправильным логином
        Courier wrongLoginCourier = new Courier("wrong_" + System.currentTimeMillis(), testPassword, null);
        courierSteps.loginCourier(wrongLoginCourier)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Система возвращает ошибку при неправильном пароле")
    @Description("Проверка, что при авторизации с неверным паролем возвращается ошибка 404")
    public void loginWithWrongPasswordReturnsError() {
        // Пытаемся авторизоваться с неправильным паролем
        Courier wrongPasswordCourier = new Courier(testLogin, "wrong_password", null);
        courierSteps.loginCourier(wrongPasswordCourier)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Возвращается ошибка, если поле логин отсутствует")
    @Description("Проверка, что запрос без поля login возвращает ошибку 400")
    public void loginWithoutLoginFieldReturnsError() {
        courierSteps.loginWithoutLoginField()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Возвращается ошибка, если поле пароль отсутствует")
    @Description("Проверка, что запрос без поля password возвращает ошибку 400")
    public void loginWithoutPasswordFieldReturnsError() {
        courierSteps.loginWithoutPasswordField()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Возвращается ошибка, если тело запроса пустое")
    @Description("Проверка, что пустое тело запроса возвращает ошибку 400")
    public void loginWithEmptyBodyReturnsError() {
        courierSteps.loginWithEmptyBody()
                .statusCode(SC_BAD_REQUEST)
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
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Успешный запрос авторизации возвращает id курьера")
    @Description("Проверка, что при успешной авторизации в ответе приходит корректный id")
    public void successfulLoginReturnsCourierId() {
        Integer courierId = courierSteps.loginCourier(testCourier)
                .statusCode(SC_OK)
                .extract()
                .path("id");

        assertThat("ID курьера должен быть положительным числом", courierId, greaterThan(0));
        assertThat("ID курьера должен быть целым числом", courierId, notNullValue());
    }

}