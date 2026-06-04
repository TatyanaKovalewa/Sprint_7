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
import static org.hamcrest.Matchers.equalTo;

public class CourierDeleteTest {

    private CourierSteps courierSteps;
    private List<Integer> createdCourierIds;
    private int testCourierId;

    @Before
    @Step("Настройка тестового окружения")
    public void setUp() {
        RestAssured.baseURI = Config.BASE_URL;
        courierSteps = new CourierSteps();
        createdCourierIds = new ArrayList<>();

        // Создаем тестового курьера
        String uniqueLogin = "delete_test_" + System.currentTimeMillis();
        String password = "1234";
        Courier courier = new Courier(uniqueLogin, password, "Тестовый");

        courierSteps.createCourier(courier).statusCode(SC_CREATED);
        testCourierId = courierSteps.getCourierId(courier);
        createdCourierIds.add(testCourierId);
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
        courierSteps.deleteCourier(testCourierId)
                .statusCode(SC_OK)
                .body("ok", equalTo(true));

        // Убираем id из списка на удаление, так как курьер уже удален
        createdCourierIds.remove((Integer) testCourierId);
    }

    @Test
    @DisplayName("Нельзя удалить курьера без id")
    @Description("Проверка, что запрос на удаление без id возвращает ошибку")
    public void cannotDeleteCourierWithoutId() {
        courierSteps.deleteCourierWithoutId()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Нельзя удалить курьера с несуществующим id")
    @Description("Проверка, что запрос на удаление с несуществующим id возвращает ошибку 404")
    public void cannotDeleteCourierWithNonExistentId() {
        int nonExistentId = Integer.MAX_VALUE;

        courierSteps.deleteCourier(nonExistentId)
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Курьера с таким id нет."));
    }
}
