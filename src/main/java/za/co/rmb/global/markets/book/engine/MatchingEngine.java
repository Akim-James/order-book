package za.co.rmb.global.markets.book.engine;

import za.co.rmb.global.markets.book.entities.Order;
import za.co.rmb.global.markets.book.entities.Side;
import za.co.rmb.global.markets.book.limit.LimitOrderBook;

import java.util.Queue;
import java.util.TreeMap;

public class MatchingEngine {
    private final LimitOrderBook limitOrderBook;

    public MatchingEngine(LimitOrderBook limitOrderBook) {
        this.limitOrderBook = limitOrderBook;
    }

    public void execute(Order newOrder) {
        // If it's a buy, we traverse the asks and if it's a bid we traverse the asks
        boolean isABid = newOrder.getSide() == Side.BUY;
        TreeMap<Double, Queue<Order>> book = isABid ? this.limitOrderBook.getAsks() : this.limitOrderBook.getBids();

        // Get the queue at the exact price level
        Queue<Order> matchingOrders = book.get(newOrder.getPrice());

        // If no matching orders exist at this price, simply add the order to the book
        if (matchingOrders == null || matchingOrders.isEmpty()) {
            this.limitOrderBook.addNewOrder(newOrder);
            return;
        }

        int remainingQuantity = newOrder.getQuantity();

        while (!matchingOrders.isEmpty() && remainingQuantity > 0) {
            Order matchingOrder = matchingOrders.peek();

            int fillQuantity = Math.min(matchingOrder.getQuantity(), remainingQuantity);

            System.out.println("Trade executed: " + fillQuantity + " @ " + newOrder.getPrice());

            remainingQuantity -= fillQuantity;
            matchingOrder.setQuantity(matchingOrder.getQuantity() - fillQuantity);


            if (matchingOrder.getQuantity() == 0) {
                // Remove the order if fully filled
                matchingOrders.poll();
            }
        }

        if (book.containsKey(newOrder.getPrice()) && book.get(newOrder.getPrice()).isEmpty()) {
            book.remove(newOrder.getPrice());
        }

        // If the newOrder is not fully filled, add the remaining quantity to the book
        if (remainingQuantity > 0) {
            newOrder.setQuantity(remainingQuantity);
            this.limitOrderBook.addNewOrder(newOrder);
        }
    }
}
