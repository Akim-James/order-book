package za.co.rmb.global.markets.book.limit;

import java.util.*;
import za.co.rmb.global.markets.book.entities.*;

public class LimitOrderBook {
    private final TreeMap<Double, Queue<Order>> bids;
    private final TreeMap<Double, Queue<Order>> asks;
    private final Map<String, Order> orderIndex; // Direct order lookup

    public LimitOrderBook() {
        bids = new TreeMap<>();
        asks = new TreeMap<>();
        orderIndex = new HashMap<>();
    }

    public Order retrieveOrderById(String orderId) {
        return orderIndex.get(orderId);
    }

    public List<Order> retrieveAllOrders() {
        List<Order> allOrders = new ArrayList<>();
        // Add all orders from bids
        for (Queue<Order> queue : bids.values()) {
            allOrders.addAll(queue);
        }
        // Add all orders from asks
        for (Queue<Order> queue : asks.values()) {
            allOrders.addAll(queue);
        }
        return allOrders;
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

    public void deleteOrderById(String orderId) {
        Order order = retrieveOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order with ID " + orderId + " not found.");
        }
        removeOrderFromBook(order);
        this.orderIndex.remove(order.getOrderId());
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

    public TreeMap<Double, Queue<Order>> getAsks() {
        return asks;
    }

    public TreeMap<Double, Queue<Order>> getBids() {
        return bids;
    }
}
