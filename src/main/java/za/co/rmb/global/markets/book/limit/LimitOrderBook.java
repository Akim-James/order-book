package za.co.rmb.global.markets.book.limit;

import za.co.rmb.global.markets.book.entities.Order;
import za.co.rmb.global.markets.book.entities.Side;

import java.util.*;

/**
 * Represents a Limit Order Book (LOB) that maintains and processes bid and ask orders.
 * It supports adding, modifying, retrieving, and deleting orders while preserving price-time priority.
 */
public class LimitOrderBook {

    /**
     * Stores bid orders, sorted by price in descending order (highest price first).
     */
    private final TreeMap<Double, Queue<Order>> bids;

    /**
     * Stores ask orders, sorted by price in ascending order (lowest price first).
     */
    private final TreeMap<Double, Queue<Order>> asks;

    /**
     * Direct lookup index for orders by their unique order ID.
     */
    private final Map<String, Order> orderIndex;

    /**
     * Initializes an empty Limit Order Book.
     */
    public LimitOrderBook() {
        this.bids = new TreeMap<>(Collections.reverseOrder()); // Highest bid first
        this.asks = new TreeMap<>(); // Lowest ask first
        this.orderIndex = new HashMap<>();
    }

    /**
     * Retrieves an order by its unique ID.
     *
     * @param orderId the unique order ID
     * @return the Order object if found, otherwise null
     */
    public Order retrieveOrderById(String orderId) {
        return this.orderIndex.get(orderId);
    }

    /**
     * Retrieves all orders from both the bid and ask books.
     *
     * @return a list containing all orders in the book
     */
    public List<Order> retrieveAllOrders() {
        List<Order> allOrders = new ArrayList<>();
        this.bids.values().forEach(allOrders::addAll);
        this.asks.values().forEach(allOrders::addAll);
        return allOrders;
    }

    /**
     * Retrieves all bid orders at a specific price level.
     *
     * @param price the bid price level to search for
     * @return a list of bid orders at the given price, or an empty list if none exist
     */
    public List<Order> retrieveBidsByPrice(Double price) {
        return this.bids.containsKey(price) ? new ArrayList<>(this.bids.get(price)) : Collections.emptyList();
    }

    /**
     * Retrieves all ask orders at a specific price level.
     *
     * @param price the ask price level to search for
     * @return a list of ask orders at the given price, or an empty list if none exist
     */
    public List<Order> retrieveAsksByPrice(Double price) {
        return this.asks.containsKey(price) ? new ArrayList<>(this.asks.get(price)) : Collections.emptyList();
    }

    /**
     * Adds a new order to the order book while maintaining price-time priority.
     *
     * @param order the new order to be added
     */
    public void addNewOrder(Order order) {
        TreeMap<Double, Queue<Order>> book = order.getSide() == Side.BUY ? bids : asks;
        book.computeIfAbsent(order.getPrice(), price -> new LinkedList<>()).add(order);
        this.orderIndex.put(order.getOrderId(), order);
    }

    /**
     * Removes a specific order from the book and cleans up empty price levels.
     *
     * @param order the order to be removed
     */
    public void removeOrderFromBook(Order order) {
        TreeMap<Double, Queue<Order>> book = order.getSide() == Side.BUY ? bids : asks;
        Queue<Order> queue = book.get(order.getPrice());

        if (queue != null) {
            queue.remove(order);
            if (queue.isEmpty()) {
                book.remove(order.getPrice()); // Remove empty price level
            }
        }
    }

    /**
     * Deletes an order from the order book by its ID.
     *
     * @param orderId the unique ID of the order to be deleted
     * @throws IllegalArgumentException if the order does not exist
     */
    public void deleteOrderById(String orderId) {
        Order order = retrieveOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order with ID " + orderId + " not found.");
        }
        removeOrderFromBook(order);
        this.orderIndex.remove(order.getOrderId());
    }

    /**
     * Updates the quantity of an existing order and moves it to the back of the queue to reflect the priority change.
     *
     * @param orderId     the unique ID of the order to update
     * @param newQuantity the new quantity of the order
     * @throws IllegalArgumentException if the order does not exist
     */
    public void updateOrderQuantity(String orderId, int newQuantity) {
        Order order = this.orderIndex.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order with ID " + orderId + " not found.");
        }
        removeOrderFromBook(order);
        order.setQuantity(newQuantity);
        addNewOrder(order);
    }

    /**
     * Retrieves the ask order book.
     *
     * @return the ask order book (TreeMap of price levels with queues of orders)
     */
    public TreeMap<Double, Queue<Order>> getAsks() {
        return this.asks;
    }

    /**
     * Retrieves the bid order book.
     *
     * @return the bid order book (TreeMap of price levels with queues of orders)
     */
    public TreeMap<Double, Queue<Order>> getBids() {
        return this.bids;
    }
}
