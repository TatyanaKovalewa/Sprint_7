import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private OrderSteps orderSteps;
    private List<Integer> createdOrderTracks; // Список созданных треков заказов

    // Параметры для теста
    private final String testName;
    private final List<String> colors;

    public OrderCreateTest(String testName, List<String> colors) {
        this.testName = testName;
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {
                        "Заказ с цветом BLACK",
                        List.of("BLACK")
                },
                {
                        "Заказ с цветом GREY",
                        List.of("GREY")
                },
                {
                        "Заказ с обоими цветами BLACK и GREY",
                        List.of("BLACK", "GREY")
                },
                {
                        "Заказ без указания цвета",
                        Collections.emptyList()
                }
        });
    }

    @Before
    @Step("Настройка тестового окружения")
    public void setUp() {
        RestAssured.baseURI = Config.BASE_URL;
        orderSteps = new OrderSteps();
        createdOrderTracks = new ArrayList<>();
    }

    @After
    @Step("Очистка тестовых данных: отмена созданных заказов")
    public void cleanUp() {
        for (Integer track : createdOrderTracks) {
            try {
                orderSteps.cancelOrder(track);
                System.out.println("Попытка отмены заказа с track " + track);
            } catch (Exception e) {
                System.out.println("Не удалось отменить заказ с track: " + track);
            }
        }
    }

    @Test
    @DisplayName("Создание заказа с разными вариантами цвета")
    @Description("Проверка создания заказа с различными комбинациями цветов. Успешный ответ должен содержать track и код 201")
    public void createOrderWithDifferentColors() {

        System.out.println("Выполняется тест: " + testName);

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

        int track = orderSteps.createOrder(order)
                .statusCode(SC_CREATED)
                .body("$", hasKey("track"))
                .body("track", notNullValue())
                .body("track", instanceOf(Integer.class))
                .body("track", greaterThan(0))
                .extract()
                .path("track");

        // Сохраняем track для последующей очистки
        createdOrderTracks.add(track);
    }

}
