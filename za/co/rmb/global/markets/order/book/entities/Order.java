package za.co.rmb.global.markets.order.book.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Order implements Serializable {

    private final String orderId;

    private int quantity;

    private double price;

    private Side side;

    public Order(int quantity, double price, Side side) {
        this.orderId = UUID.randomUUID().toString();
        this.quantity = quantity;
        this.price = price;
        this.side = side;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return quantity == order.quantity && Double.compare(order.price, price) == 0 && orderId.equals(order.orderId) && side == order.side;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, quantity, price, side);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", side=" + side +
                '}';
    }

    public enum Operation {
        DELETE, UPDATE
    }
}
