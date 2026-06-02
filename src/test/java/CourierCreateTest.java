import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.given;

public class CourierCreateTest {

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
    @DisplayName("Успешное создание курьера")
    @Description("Проверка создания курьера со всеми обязательными полями")
    public void createCourier() {
        String uniqueLogin = "ivan_" + System.currentTimeMillis();
        String password = "1234";
        Courier courier = new Courier(uniqueLogin, password, "Иван");
        createdLogins.add(uniqueLogin);
        createdPasswords.add(password);

        courierSteps.createCourier(courier)
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать дубликат курьера")
    @Description("Проверка, что при попытке создать курьера с существующим логином возвращается ошибка")
    public void cannotCreateDuplicateCourier() {
        String uniqueLogin = "duplicate_" + System.currentTimeMillis();
        String password = "1234";
        Courier courier = new Courier(uniqueLogin, password, "Иван");
        createdLogins.add(uniqueLogin);
        createdPasswords.add(password);

        // Создаем первого курьера
        courierSteps.createCourier(courier).statusCode(201);

        // Пытаемся создать второго с теми же данными
        courierSteps.createCourier(courier)
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Нельзя создать курьера без логина (null)")
    @Description("Проверка, что при отсутствии логина возвращается ошибка 400")
    public void cannotCreateCourierWithoutLogin() {
        Courier courier = new Courier(null, "1234", "Иван");

        courierSteps.createCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без пароля (null)")
    @Description("Проверка, что при отсутствии пароля возвращается ошибка 400")
    public void cannotCreateCourierWithoutPassword() {
        String uniqueLogin = "nopass_" + System.currentTimeMillis();
        Courier courier = new Courier(uniqueLogin, null, "Иван");

        courierSteps.createCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Можно создать курьера без имени (null)")
    @Description("Проверка, что поле firstName не является обязательным")
    public void canCreateCourierWithoutFirstName() {
        String uniqueLogin = "noname_" + System.currentTimeMillis();
        String password = "1234";
        Courier courier = new Courier(uniqueLogin, password, null);
        createdLogins.add(uniqueLogin);
        createdPasswords.add(password);

        courierSteps.createCourier(courier)
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Проверка статус кода при успешном создании")
    @Description("Успешный запрос должен возвращать статус код 201")
    public void createCourierReturnsCorrectStatusCode() {
        String uniqueLogin = "statuscode_" + System.currentTimeMillis();
        String password = "1234";
        Courier courier = new Courier(uniqueLogin, password, "Иван");
        createdLogins.add(uniqueLogin);
        createdPasswords.add(password);

        courierSteps.createCourier(courier).statusCode(201);
    }

    @Test
    @DisplayName("Проверка поля ok в успешном ответе")
    @Description("Успешный ответ должен содержать ok: true")
    public void successResponseReturnsOkTrue() {
        String uniqueLogin = "oktest_" + System.currentTimeMillis();
        String password = "1234";
        Courier courier = new Courier(uniqueLogin, password, "Иван");
        createdLogins.add(uniqueLogin);
        createdPasswords.add(password);

        courierSteps.createCourier(courier)
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать курьера с пустым логином (строка)")
    @Description("Пустая строка в логине считается недостаточными данными")
    public void cannotCreateCourierWithEmptyLoginString() {
        Courier courier = new Courier("", "1234", "Иван");

        courierSteps.createCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера с пустым паролем (строка)")
    @Description("Пустая строка в пароле считается недостаточными данными")
    public void cannotCreateCourierWithEmptyPasswordString() {
        String uniqueLogin = "emptypass_" + System.currentTimeMillis();
        Courier courier = new Courier(uniqueLogin, "", "Иван");

        courierSteps.createCourier(courier)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера с пустым телом запроса")
    @Description("Пустой JSON объект не содержит обязательных полей")
    public void cannotCreateCourierWithEmptyBody() {
        given()
                .header("Content-type", "application/json")
                .body("{}")
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Можно создать курьера с пустым именем (строка)")
    @Description("Пустая строка в поле firstName допустима")
    public void canCreateCourierWithEmptyFirstName() {
        String uniqueLogin = "emptyfirst_" + System.currentTimeMillis();
        String password = "1234";
        Courier courier = new Courier(uniqueLogin, password, "");
        createdLogins.add(uniqueLogin);
        createdPasswords.add(password);

        courierSteps.createCourier(courier)
                .statusCode(201)
                .body("ok", equalTo(true));
    }

}