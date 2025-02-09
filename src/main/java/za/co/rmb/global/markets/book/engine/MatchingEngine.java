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
        boolean isABid = newOrder.getSide() == Side.BUY;
        TreeMap<Double, Queue<Order>> book = isABid ? this.limitOrderBook.getAsks() : this.limitOrderBook.getBids();

        Queue<Order> matchingOrders = getMatchingOrders(book, newOrder.getPrice());

        if (matchingOrders == null || matchingOrders.isEmpty()) {
            this.limitOrderBook.addNewOrder(newOrder);
            return;
        }

        int remainingQuantity = processMatchingOrders(matchingOrders, newOrder.getQuantity());

        removeEmptyPriceLevel(book, newOrder.getPrice());

        addRemainingOrderToBook(newOrder, remainingQuantity);
    }

    // Retrieves the queue of matching orders at the given price level
    private Queue<Order> getMatchingOrders(TreeMap<Double, Queue<Order>> book, double price) {
        return book.get(price);
    }

    // Processes matching orders and returns remaining quantity of the new order
    private int processMatchingOrders(Queue<Order> matchingOrders, int orderQuantity) {
        int remainingQuantity = orderQuantity;

        while (!matchingOrders.isEmpty() && remainingQuantity > 0) {
            Order matchingOrder = matchingOrders.peek();

            int fillQuantity = Math.min(matchingOrder.getQuantity(), remainingQuantity);
            remainingQuantity -= fillQuantity;
            matchingOrder.setQuantity(matchingOrder.getQuantity() - fillQuantity);

            if (matchingOrder.getQuantity() == 0) {
                matchingOrders.poll(); // Remove fully filled order
            }
        }

        return remainingQuantity;
    }

    // Removes price level if no orders remain at that price
    private void removeEmptyPriceLevel(TreeMap<Double, Queue<Order>> book, double price) {
        if (book.containsKey(price) && book.get(price).isEmpty()) {
            book.remove(price);
        }
    }

    // Adds the remaining order to the book if it's not fully filled
    private void addRemainingOrderToBook(Order newOrder, int remainingQuantity) {
        if (remainingQuantity > 0) {
            newOrder.setQuantity(remainingQuantity);
            this.limitOrderBook.addNewOrder(newOrder);
        }
    }
}
