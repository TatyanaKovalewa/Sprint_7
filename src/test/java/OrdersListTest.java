import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class OrdersListTest {

    private OrderSteps orderSteps;

    @Before
    public void setUp() {
        RestAssured.baseURI = Config.BASE_URL;
        orderSteps = new OrderSteps();
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что тело ответа содержит список заказов")
    public void getOrdersListReturnsOrders() {
        orderSteps.getOrdersList()
                .statusCode(200)
                .body("orders", notNullValue());
    }

}
