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

public class OrderAcceptTest {

    private OrderSteps orderSteps;
    private OrderAcceptSteps orderAcceptSteps;
    private CourierSteps courierSteps;
    private List<Integer> createdCourierIds;

    @Before
    @Step("Настройка тестового окружения")
    public void setUp() {
        RestAssured.baseURI = Config.BASE_URL;
        orderSteps = new OrderSteps();
        orderAcceptSteps = new OrderAcceptSteps();
        courierSteps = new CourierSteps();
        createdCourierIds = new ArrayList<>();
    }

    @After
    @Step("Очистка тестовых данных")
    public void cleanUp() {
        for (Integer courierId : createdCourierIds) {
            try {
                courierSteps.deleteCourier(courierId);
            } catch (Exception e) {
                System.out.println("Не удалось удалить курьера с id: " + courierId);
            }
        }
    }

    private int createCourier() {
        String uniqueLogin = "courier_" + System.currentTimeMillis();
        Courier courier = new Courier(uniqueLogin, "1234", "Тестовый");

        courierSteps.createCourier(courier).statusCode(201);
        int courierId = courierSteps.loginCourier(courier)
                .statusCode(200)
                .extract()
                .path("id");
        createdCourierIds.add(courierId);
        return courierId;
    }

    private int createOrderAndGetId() {
        Order order = new Order(
                "Иван",
                "Иванов",
                "Москва",
                "Центральная",
                "+79998887766",
                5,
                "2024-12-31",
                "Тестовый заказ",
                List.of("BLACK")
        );

        int track = orderSteps.createOrder(order)
                .statusCode(201)
                .extract()
                .path("track");

        // Получаем orderId по track
        return orderSteps.getOrderByTrack(track)
                .extract()
                .path("order.id");

    }

    @Test
    @DisplayName("Успешное принятие заказа")
    @Description("Проверка: успешный запрос возвращает ok: true")
    public void acceptOrderSuccess() {
        int courierId = createCourier();
        int orderId = createOrderAndGetId();

        orderAcceptSteps.acceptOrder(orderId, courierId)
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя принять заказ без id курьера")
    @Description("Проверка: если не передать id курьера, запрос вернёт ошибку")
    public void cannotAcceptOrderWithoutCourierId() {
        int orderId = createOrderAndGetId();

        orderAcceptSteps.acceptOrderWithoutCourierId(orderId)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Нельзя принять заказ с неверным id курьера")
    @Description("Проверка: если передать неверный id курьера, запрос вернёт ошибку")
    public void cannotAcceptOrderWithInvalidCourierId() {
        int orderId = createOrderAndGetId();
        int invalidCourierId = 999999999;

        orderAcceptSteps.acceptOrder(orderId, invalidCourierId)
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Нельзя принять заказ без номера заказа")
    @Description("Проверка: если не передать номер заказа, запрос вернёт ошибку")
    public void cannotAcceptOrderWithoutOrderId() {
        int courierId = createCourier();

        orderAcceptSteps.acceptOrderWithoutOrderId(courierId)
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Нельзя принять заказ с неверным номером заказа")
    @Description("Проверка: если передать неверный номер заказа, запрос вернёт ошибку")
    public void cannotAcceptOrderWithInvalidOrderId() {
        int courierId = createCourier();
        int invalidOrderId = 999999999;

        orderAcceptSteps.acceptOrder(invalidOrderId, courierId)
                .statusCode(404)
                .body("message", equalTo("Заказа с таким id не существует"));
    }
}
