import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class OrderSteps {

    private static final String ORDER_PATH = "/api/v1/orders";
    private static final String GET_ORDER_PATH = "/api/v1/orders/track";

    @Step("Создание заказа с параметрами: firstName={order.firstName}, color={order.color}")
    public ValidatableResponse createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Получение списка заказов")
    public ValidatableResponse getOrdersList() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(ORDER_PATH)
                .then();
    }

    @Step("Получение заказа по track-номеру: track = {track}")
    public ValidatableResponse getOrderByTrack(int track) {
        return given()
                .header("Content-type", "application/json")
                .queryParam("t", track)
                .when()
                .get(GET_ORDER_PATH)
                .then();
    }

    @Step("Получение заказа без track-номера")
    public ValidatableResponse getOrderWithoutTrack() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(GET_ORDER_PATH)
                .then();
    }

}
