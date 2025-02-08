package za.co.rmb.global.markets.order.book;

import za.co.rmb.global.markets.order.book.entities.Order;
import za.co.rmb.global.markets.order.book.entities.Side;

import java.util.List;

public class LimitOrderBokTest {

    public static void main(String[] args) {
        LimitOrderBook limitOrderBook = new LimitOrderBook();

        addLimitOrderBooks(limitOrderBook);

        // Delete all orders that have a 300 price

        List<Order> asks = limitOrderBook.retrieveAsksByPrice(300.0);

        asks.forEach(order -> limitOrderBook.deleteOrderById(order.getOrderId()));

        // Update all orders with price 3 to have quantity 1000
        List<Order> bids = limitOrderBook.retrieveBidsByPrice(3.0);

        bids.forEach(order -> limitOrderBook.updateOrderQuantity(order.getOrderId(), 1000));

    }

    private static void addLimitOrderBooks(LimitOrderBook limitOrderBook) {
        limitOrderBook.addNewOrder(new Order(6, 304, Side.BUY));
        limitOrderBook.addNewOrder(new Order(45, 330, Side.BUY));
        limitOrderBook.addNewOrder(new Order(5, 80, Side.BUY));
        limitOrderBook.addNewOrder(new Order(2, 300, Side.BUY));
        limitOrderBook.addNewOrder(new Order(20, 6730, Side.BUY));


        limitOrderBook.addNewOrder(new Order(20, 9, Side.SELL));
        limitOrderBook.addNewOrder(new Order(3, 7, Side.SELL));
        limitOrderBook.addNewOrder(new Order(43, 3, Side.SELL));
        limitOrderBook.addNewOrder(new Order(21, 10, Side.SELL));
        limitOrderBook.addNewOrder(new Order(90, 3, Side.SELL));
    }
}
