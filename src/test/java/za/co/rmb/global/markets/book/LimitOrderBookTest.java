package za.co.rmb.global.markets.book;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.rmb.global.markets.book.entities.Order;
import za.co.rmb.global.markets.book.entities.Side;

import java.util.List;

public class LimitOrderBookTest {

    private LimitOrderBook limitOrderBook;

    @BeforeEach
    void setUp() {
        this.limitOrderBook = new LimitOrderBook();
        addBids();
        addAsks();
    }

    // Assert that bids and asks are stored
    @Test
    public void testRetrieveAllOrders() {
        List<Order> orders = this.limitOrderBook.retrieveAllOrders();
        Assertions.assertEquals(13, orders.size());
    }

    // Assert that asks are stored correctly by price
    @Test
    public void testRetrieveAsksByPrice() {
        List<Order> price10 = this.limitOrderBook.retrieveAsksByPrice(10.0);
        Assertions.assertEquals(2, price10.size());

        List<Order> price11 = this.limitOrderBook.retrieveAsksByPrice(11.0);
        Assertions.assertEquals(2, price11.size());

        List<Order> price12 = this.limitOrderBook.retrieveAsksByPrice(12.0);
        Assertions.assertEquals(2, price12.size());
    }

    // Assert that bids are stored correctly by price
    @Test
    public void testRetrieveBidsByPrice() {
        List<Order> price9 = this.limitOrderBook.retrieveBidsByPrice(9.0);
        Assertions.assertEquals(2, price9.size());

        List<Order> price8 = this.limitOrderBook.retrieveBidsByPrice(8.0);
        Assertions.assertEquals(2, price8.size());

        List<Order> price7 = this.limitOrderBook.retrieveBidsByPrice(7.0);
        Assertions.assertEquals(2, price7.size());

        List<Order> price6 = this.limitOrderBook.retrieveBidsByPrice(6.0);
        Assertions.assertEquals(1, price6.size());
    }

    // Assert that orders retrieved by price is equals to the same order if retrieved by Id.
    @Test
    public void testRetrieveOrderById() {

        List<Order> price6 = this.limitOrderBook.retrieveBidsByPrice(6.0);

        Assertions.assertEquals(1, price6.size());

        Order order = price6.get(0);

        Order orderById = this.limitOrderBook.retrieveOrderById(order.getOrderId());

        Assertions.assertEquals(order, orderById);

        // Test deep equality
        List<Order> price7 = this.limitOrderBook.retrieveBidsByPrice(7.0);
        Assertions.assertFalse(price7.isEmpty());
        Assertions.assertNotEquals(order, price7.get(0));
    }

    // Assert that if an order existed, it can be successfully deleted
    @Test
    public void testDeleteOrderById() {

        List<Order> price6 = this.limitOrderBook.retrieveBidsByPrice(6.0);

        Assertions.assertEquals(1, price6.size());

        Order order = price6.get(0);

        this.limitOrderBook.deleteOrderById(order.getOrderId());

        List<Order> orders = this.limitOrderBook.retrieveBidsByPrice(6.0);

        Assertions.assertEquals(0, orders.size());

    }

    // Test that if an order doesn't exist, an exception is thrown
    @Test
    public void testDeleteNonExistentOrderById() {
        Exception exception = Assertions.assertThrows(
                IllegalArgumentException.class, () ->   this.limitOrderBook.deleteOrderById("SOME_RANDOM_ID")
        );

        Assertions.assertEquals("Order with ID SOME_RANDOM_ID not found.", exception.getMessage());
    }

    // Assert that an order that exists can be modified successfully.
    @Test
    public void testUpdateOrderQuantity() {

        List<Order> price6Quantity60 = this.limitOrderBook.retrieveBidsByPrice(6.0);

        Assertions.assertEquals(1, price6Quantity60.size());

        Order quantity60 = price6Quantity60.get(0);
        Assertions.assertEquals(60, quantity60.getQuantity());


        this.limitOrderBook.updateOrderQuantity(quantity60.getOrderId(), 200);

        List<Order> price6Quantity200 = this.limitOrderBook.retrieveBidsByPrice(6.0);
        Order quantity200 = price6Quantity60.get(0);

        Assertions.assertEquals(1, price6Quantity200.size());
        Assertions.assertEquals(200, quantity200.getQuantity());
    }

    // Test that if an order doesn't exist, an exception is thrown when modification is attempted.
    @Test
    public void testUpdateNonExistentOrderById() {
        Exception exception = Assertions.assertThrows(
                IllegalArgumentException.class, () ->   this.limitOrderBook.updateOrderQuantity("SOME_RANDOM_ID", 200)
        );

        Assertions.assertEquals("Order with ID SOME_RANDOM_ID not found.", exception.getMessage());
    }

    private void addBids() {
        this.limitOrderBook.addNewOrder(new Order(40, 9.0, Side.BUY));
        this.limitOrderBook.addNewOrder(new Order(30, 8.0, Side.BUY));
        this.limitOrderBook.addNewOrder(new Order(50, 7.0, Side.BUY));
        this.limitOrderBook.addNewOrder(new Order(60, 6.0, Side.BUY));

        this.limitOrderBook.addNewOrder(new Order(20, 9.0, Side.BUY));
        this.limitOrderBook.addNewOrder(new Order(20, 8.0, Side.BUY));
        this.limitOrderBook.addNewOrder(new Order(50, 7.0, Side.BUY));
    }

    private void addAsks() {
        this.limitOrderBook.addNewOrder(new Order(5, 10.0, Side.SELL));
        this.limitOrderBook.addNewOrder(new Order(40, 11.0, Side.SELL));
        this.limitOrderBook.addNewOrder(new Order(20, 12.0, Side.SELL));

        this.limitOrderBook.addNewOrder(new Order(100, 10.0, Side.SELL));
        this.limitOrderBook.addNewOrder(new Order(50, 11.0, Side.SELL));
        this.limitOrderBook.addNewOrder(new Order(10, 12.0, Side.SELL));
    }
}
