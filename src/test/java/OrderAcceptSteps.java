import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class OrderAcceptSteps {

   private static final String ACCEPT_ORDER_PATH = "/api/v1/orders/accept";

    @Step("Принятие заказа: orderId = {orderId}, courierId = {courierId}")
    public ValidatableResponse acceptOrder(int orderId, int courierId) {
        return given()
                .header("Content-type", "application/json")
                .queryParam("courierId", courierId)
                .when()
                .put(ACCEPT_ORDER_PATH + "/" + orderId)
                .then();
    }

    @Step("Принятие заказа без courierId: orderId = {orderId}")
    public ValidatableResponse acceptOrderWithoutCourierId(int orderId) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .put(ACCEPT_ORDER_PATH + "/" + orderId)
                .then();
    }

    @Step("Принятие заказа без orderId: courierId = {courierId}")
    public ValidatableResponse acceptOrderWithoutOrderId(int courierId) {
        return given()
                .header("Content-type", "application/json")
                .queryParam("courierId", courierId)
                .when()
                .put(ACCEPT_ORDER_PATH + "/")
                .then();
    }

}
