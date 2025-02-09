package za.co.rmb.global.markets.book.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.rmb.global.markets.book.entities.Order;
import za.co.rmb.global.markets.book.entities.Side;
import za.co.rmb.global.markets.book.limit.LimitOrderBook;

import java.util.List;

public class MatchingEngineTest {

    private LimitOrderBook limitOrderBook;

    private MatchingEngine matchingEngine;

    @BeforeEach
    void setUp() {
        this.limitOrderBook = new LimitOrderBook();
        addBids();
        addAsks();

        this.matchingEngine = new MatchingEngine(limitOrderBook);
    }

    @Test
    public void testSellMatchDoesntExist() {

        this.matchingEngine.execute(new Order(40, 35.0, Side.SELL));

        List<Order> asksByPrice = this.limitOrderBook.retrieveAsksByPrice(35.0);
        Assertions.assertFalse(asksByPrice.isEmpty()); // Shouldn't exist since it only exists in the bids

        List<Order> bidsByPrice = this.limitOrderBook.retrieveBidsByPrice(35.0);
        Assertions.assertTrue(bidsByPrice.isEmpty()); // Newly added bid
    }

    @Test
    public void testSellWhenBidExistsWithMoreBuys() {
        this.matchingEngine.execute(new Order(90, 9.0, Side.SELL));

        List<Order> bidsByPrice = this.limitOrderBook.retrieveBidsByPrice(9.0);

        Assertions.assertEquals(0, bidsByPrice.size(), "There should be no more bids at price 9");

        List<Order> asksByPrice = this.limitOrderBook.retrieveAsksByPrice(9.0);

        Assertions.assertEquals(1, asksByPrice.size(), "There should be one ask at price 9");

        // This validates that the bid with 90 was filled 90-60 = 30 remaining
        Assertions.assertEquals(30, asksByPrice.get(0).getQuantity(), "This new order should be added to the asks with a quantity of 30");
    }

    @Test
    public void testSellWhenBidExistsWithLessBuys() {
        this.matchingEngine.execute(new Order(35, 9.0, Side.SELL));

        List<Order> bidsByPrice = this.limitOrderBook.retrieveBidsByPrice(9.0);

        Assertions.assertEquals(2, bidsByPrice.size());

        // This validates that the bid with 40 was filled and then the 20 partially with 5 remaining
        Assertions.assertEquals(5, bidsByPrice.get(0).getQuantity(), "First order should have 5 remaining");
        Assertions.assertEquals(20, bidsByPrice.get(1).getQuantity(), "Second order should still have 20");

    }

    @Test
    public void testSellWhenBidExistsWithBuysMatchingNewSell() {
        this.matchingEngine.execute(new Order(60, 9.0, Side.SELL));

        List<Order> bidsByPrice = this.limitOrderBook.retrieveBidsByPrice(9.0);

        Assertions.assertEquals(0, bidsByPrice.size(), "All orders should be filled");
    }

    // BUYS
    @Test
    public void testBuyMatchDoesntExist() {

        this.matchingEngine.execute(new Order(40, 35.0, Side.BUY));

        List<Order> asksByPrice = this.limitOrderBook.retrieveAsksByPrice(35.0);
        Assertions.assertTrue(asksByPrice.isEmpty()); // Shouldn't exist since it only exists in the bids

        List<Order> bidsByPrice = this.limitOrderBook.retrieveBidsByPrice(35.0);
        Assertions.assertFalse(bidsByPrice.isEmpty()); // Newly added bid
    }

    @Test
    public void testBuyWhenMoreSellExists() {
        this.matchingEngine.execute(new Order(90, 10.0, Side.BUY));

        List<Order> asksByPrice = this.limitOrderBook.retrieveAsksByPrice(10.0);

        Assertions.assertEquals(1, asksByPrice.size(), "There should be one ask at price 10.0");

        // This validates that the bid with 105-90 was filled 90-60 = 30 remaining
        Assertions.assertEquals(15, asksByPrice.get(0).getQuantity(), "This new order should be added to the asks with a quantity of 15");

        List<Order>  bidsByPrice = this.limitOrderBook.retrieveBidsByPrice(10.0);

        Assertions.assertEquals(0, bidsByPrice.size(), "There should be no more bids at price 10.0");
      }

    @Test
    public void testBuyWhenLessSellsExists() {
        this.matchingEngine.execute(new Order(150, 10.0, Side.BUY));

        List<Order> asksByPrice = this.limitOrderBook.retrieveBidsByPrice(10.0);
        Assertions.assertEquals(1, asksByPrice.size());
        Assertions.assertEquals(45, asksByPrice.get(0).getQuantity(), "Second order should still have 20");
    }

    @Test
    public void testBuyWhenSellExistsWithBuysMatchingNewSell() {
        this.matchingEngine.execute(new Order(105, 10.0, Side.BUY));
        List<Order> asksByPrice = this.limitOrderBook.retrieveAsksByPrice(10.0);
        Assertions.assertEquals(0, asksByPrice.size(), "All orders should be filled");
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
