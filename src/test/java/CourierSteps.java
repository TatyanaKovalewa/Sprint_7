import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class CourierSteps {

    private static final String COURIER_PATH = "/api/v1/courier";
    private static final String LOGIN_PATH = "/api/v1/courier/login";

    @Step("Создание курьера с логином: {courier.login}")
    public ValidatableResponse createCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(COURIER_PATH)
                .then();
    }

    @Step("Логин курьера с логином: {courier.login}")
    public ValidatableResponse loginCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    @Step("Удаление курьера с id: {courierId}")
    public ValidatableResponse deleteCourier(int courierId) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .delete(COURIER_PATH + "/" + courierId)
                .then();
    }

    @Step("Получение id курьера после логина")
    public int getCourierId(Courier courier) {
        return loginCourier(courier)
                .statusCode(200)
                .extract()
                .path("id");
    }

}
