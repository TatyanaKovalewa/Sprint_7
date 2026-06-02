import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private OrderSteps orderSteps;

    // Параметры для теста
    private final String testName;
    private final List<String> colors;
    private final String expectedDescription;

    public OrderCreateTest(String testName, List<String> colors, String expectedDescription) {
        this.testName = testName;
        this.colors = colors;
        this.expectedDescription = expectedDescription;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {
                        "Заказ с цветом BLACK",
                        List.of("BLACK"),
                        "Заказ создается с одним цветом BLACK"
                },
                {
                        "Заказ с цветом GREY",
                        List.of("GREY"),
                        "Заказ создается с одним цветом GREY"
                },
                {
                        "Заказ с обоими цветами BLACK и GREY",
                        List.of("BLACK", "GREY"),
                        "Заказ создается с двумя цветами"
                },
                {
                        "Заказ без указания цвета",
                        Collections.emptyList(),
                        "Заказ создается без указания цвета"
                }
        });
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = Config.BASE_URL;
        orderSteps = new OrderSteps();
    }

    @Test
    @DisplayName("Создание заказа с разными вариантами цвета")
    @Description("Проверка создания заказа с различными комбинациями цветов")
    public void createOrderWithDifferentColors() {

        System.out.println("Выполняется тест: " + testName);
        System.out.println("Описание: " + expectedDescription);

        Order order = new Order(
                "Иван",
                "Петров",
                "Москва, ул. Тестовая, д. 1",
                "Центральная",
                "+79991234567",
                5,
                "2024-12-31",
                "Тестовый заказ",
                colors
        );

        orderSteps.createOrder(order)
                .statusCode(201)
                .body("track", notNullValue())
                .body("track", greaterThan(0));
    }

    @Test
    @DisplayName("Проверка структуры ответа при создании заказа")
    @Description("Тело ответа должно содержать поле track")
    public void orderCreationResponseContainsTrack() {

        System.out.println("Выполняется тест: " + testName);
        System.out.println("Описание: " + expectedDescription);

        Order order = new Order(
                "Анна",
                "Сидорова",
                "Санкт-Петербург, Невский пр., д. 10",
                "Центральная",
                "+79876543210",
                3,
                "2024-12-25",
                "Подарок",
                List.of("BLACK")
        );

        orderSteps.createOrder(order)
                .statusCode(201)
                .body("$", hasKey("track"))
                .body("track", instanceOf(Integer.class))
                .body("track", notNullValue());
    }
}
