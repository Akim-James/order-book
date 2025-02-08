package za.co.rmb.global.markets.order.book;

import za.co.rmb.global.markets.order.book.entities.Order;
import za.co.rmb.global.markets.order.book.entities.Side;

import java.util.*;

public class LimitOrderBook {
    private final TreeMap<Double, Queue<Order>> bids;
    private final TreeMap<Double, Queue<Order>> asks;
    private final Map<String, Order> orderIndex; // Direct order lookup

    public LimitOrderBook() {
        bids = new TreeMap<>();
        asks = new TreeMap<>();
        orderIndex = new HashMap<>();
    }

    public List<Order> retrieveBidsByPrice(Double price) {
        return bids.containsKey(price) ? new ArrayList<>(bids.get(price)) : Collections.emptyList();
    }
    public List<Order> retrieveAsksByPrice(Double price) {
        return asks.containsKey(price) ? new ArrayList<>(asks.get(price)) : Collections.emptyList();
    }

    public void addNewOrder(Order order) {
        TreeMap<Double, Queue<Order>> book = Side.BUY.equals(order.getSide()) ? bids : asks;
        book.computeIfAbsent(order.getPrice(), price -> new LinkedList<>())
                .add(order);
        orderIndex.put(order.getOrderId(), order);
    }

    public Order retrieveOrderById(String orderId) {
        return orderIndex.get(orderId);
    }

    public void deleteOrderById(String orderId) {
        Order order = retrieveOrderById(orderId);

        if (order == null) {
            throw new IllegalArgumentException("Order with ID " + orderId + " not found.");
        }

        removeOrderFromBook(order);
    }

    public void updateOrderQuantity(String orderId, int newQuantity) {
        Order order = orderIndex.get(orderId);

        if (order == null) {
            throw new IllegalArgumentException("Order with ID " + orderId + " not found.");
        }

        removeOrderFromBook(order);
        order.setQuantity(newQuantity);
        addNewOrder(order);
    }

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
}
