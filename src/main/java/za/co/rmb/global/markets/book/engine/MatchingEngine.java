package za.co.rmb.global.markets.book.engine;

import za.co.rmb.global.markets.book.entities.Order;
import za.co.rmb.global.markets.book.entities.Side;
import za.co.rmb.global.markets.book.limit.LimitOrderBook;

import java.util.Queue;
import java.util.TreeMap;

/**
 * The MatchingEngine is responsible for executing trades by matching incoming orders
 * against existing orders in the Limit Order Book (LOB). It ensures that orders are
 * matched based on price-time priority and updates the LOB accordingly.
 */
public class MatchingEngine {
    private final LimitOrderBook limitOrderBook;

    /**
     * Constructs a MatchingEngine with a given Limit Order Book.
     *
     * @param limitOrderBook The limit order book that stores bids and asks.
     */
    public MatchingEngine(LimitOrderBook limitOrderBook) {
        this.limitOrderBook = limitOrderBook;
    }

    /**
     * Executes an incoming order by attempting to match it with existing orders
     * in the order book. If no match is found, the order is added to the book.
     *
     * @param newOrder The incoming order to be processed.
     */
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

    /**
     * Retrieves the queue of matching orders at the given price level.
     *
     * @param book  The order book (either bids or asks).
     * @param price The price level to search for matching orders.
     * @return The queue of orders at the specified price level, or null if none exist.
     */
    private Queue<Order> getMatchingOrders(TreeMap<Double, Queue<Order>> book, double price) {
        return book.get(price);
    }

    /**
     * Processes matching orders and determines how much of the new order remains unfilled.
     * Trades are executed in a FIFO (first-in, first-out) manner.
     *
     * @param matchingOrders The queue of orders at the matching price level.
     * @param orderQuantity  The quantity of the new incoming order.
     * @return The remaining unfilled quantity of the new order.
     */
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

    /**
     * Removes a price level from the order book if no orders remain at that price.
     *
     * @param book  The order book (either bids or asks).
     * @param price The price level to be removed if empty.
     */
    private void removeEmptyPriceLevel(TreeMap<Double, Queue<Order>> book, double price) {
        if (book.containsKey(price) && book.get(price).isEmpty()) {
            book.remove(price);
        }
    }

    /**
     * If the new order is not fully filled, the remaining quantity is added to the order book.
     *
     * @param newOrder          The incoming order that was partially filled.
     * @param remainingQuantity The remaining quantity of the order.
     */
    private void addRemainingOrderToBook(Order newOrder, int remainingQuantity) {
        if (remainingQuantity > 0) {
            newOrder.setQuantity(remainingQuantity);
            this.limitOrderBook.addNewOrder(newOrder);
        }
    }
}
