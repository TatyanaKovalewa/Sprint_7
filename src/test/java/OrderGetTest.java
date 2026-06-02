import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrderGetTest {

    private OrderSteps orderSteps;

    @Before
    @Step("Настройка тестового окружения")
    public void setUp() {
        RestAssured.baseURI = Config.BASE_URL;
        orderSteps = new OrderSteps();
    }

    private int createOrderAndGetTrack() {
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

        return orderSteps.createOrder(order)
                .statusCode(201)
                .extract()
                .path("track");
    }

    @Test
    @DisplayName("Успешное получение заказа по номеру")
    @Description("Проверка: успешный запрос возвращает объект с заказом")
    public void getOrderByTrackSuccess() {
        int track = createOrderAndGetTrack();

        orderSteps.getOrderByTrack(track)
                .statusCode(200)
                .body("order", notNullValue())
                .body("order.track", equalTo(track))
                .body("order.firstName", equalTo("Иван"))
                .body("order.lastName", equalTo("Иванов"))
                .body("order.address", equalTo("Москва"))
                .body("order.metroStation", equalTo("Центральная"))
                .body("order.phone", equalTo("+79998887766"))
                .body("order.rentTime", equalTo(5))
                .body("order.deliveryDate", containsString("2024-12-31"))
                .body("order.comment", equalTo("Тестовый заказ"))
                .body("order.color", notNullValue());
    }

    @Test
    @DisplayName("Нельзя получить заказ без номера заказа")
    @Description("Проверка: запрос без номера заказа возвращает ошибку")
    public void cannotGetOrderWithoutTrack() {
        orderSteps.getOrderWithoutTrack()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Нельзя получить заказ с несуществующим номером")
    @Description("Проверка: запрос с несуществующим заказом возвращает ошибку")
    public void cannotGetOrderWithInvalidTrack() {
        int invalidTrack = 999999999;

        orderSteps.getOrderByTrack(invalidTrack)
                .statusCode(404)
                .body("message", equalTo("Заказ не найден"));
    }

}
