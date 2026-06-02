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

public class CourierDeleteTest {

    private CourierSteps courierSteps;
    private List<Integer> createdCourierIds;

    @Before
    @Step("Настройка тестового окружения")
    public void setUp() {
        RestAssured.baseURI = Config.BASE_URL;
        courierSteps = new CourierSteps();
        createdCourierIds = new ArrayList<>();
    }

    @After
    @Step("Очистка тестовых данных: удаление созданных курьеров")
    public void cleanUp() {
        for (Integer courierId : createdCourierIds) {
            try {
                courierSteps.deleteCourier(courierId);
            } catch (Exception e) {
                System.out.println("Не удалось удалить курьера с id: " + courierId);
            }
        }
    }

    @Test
    @DisplayName("Успешное удаление курьера")
    @Description("Проверка, что существующего курьера можно удалить - возвращает ok: true")
    public void deleteCourierSuccess() {
        String uniqueLogin = "delete_" + System.currentTimeMillis();
        String password = "1234";
        Courier courier = new Courier(uniqueLogin, password, "Иван");

        courierSteps.createCourier(courier).statusCode(201);
        int courierId = courierSteps.getCourierId(courier);
        createdCourierIds.add(courierId);

        courierSteps.deleteCourier(courierId)
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя удалить курьера без id")
    @Description("Проверка, что запрос на удаление без id возвращает ошибку")
    public void cannotDeleteCourierWithoutId() {
        given()
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Нельзя удалить курьера с несуществующим id")
    @Description("Проверка, что запрос на удаление с несуществующим id возвращает ошибку 404")
    public void cannotDeleteCourierWithNonExistentId() {
        int nonExistentId = 999999999;

        courierSteps.deleteCourier(nonExistentId)
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id нет."));
    }
}
