import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static org.apache.http.HttpStatus.*;
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

    @Step("Создание курьера с пустым телом запроса")
    public ValidatableResponse createCourierWithEmptyBody() {
        return given()
                .header("Content-type", "application/json")
                .body("{}")
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

    @Step("Попытка логина без поля login")
    public ValidatableResponse loginWithoutLoginField() {
        return given()
                .header("Content-type", "application/json")
                .body("{\"password\": \"1234\"}")
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    @Step("Попытка логина без поля password")
    public ValidatableResponse loginWithoutPasswordField() {
        return given()
                .header("Content-type", "application/json")
                .body("{\"login\": \"some_login\"}")
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    @Step("Попытка логина с пустым телом запроса")
    public ValidatableResponse loginWithEmptyBody() {
        return given()
                .header("Content-type", "application/json")
                .body("{}")
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

    @Step("Попытка удаления курьера без указания id")
    public ValidatableResponse deleteCourierWithoutId() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .delete(COURIER_PATH + "/")
                .then();
    }

    @Step("Получение id курьера после логина")
    public int getCourierId(Courier courier) {
        return loginCourier(courier)
                .statusCode(SC_OK)
                .extract()
                .path("id");
    }

}
