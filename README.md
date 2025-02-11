# Limit order-book

In this LimitOrderBook implementation, I chose to separate bids and asks while enforcing the specification of quantity, price, 
and side when creating an order. This ensures that we do not need to traverse bids when handling an ask, and vice versa.
By forcing the side at order creation, we can efficiently store it in the correct book for buy orders and asks for sell orders.

### Choice of Data Structures
For storage, I chose a TreeMap with the price as the key. A TreeMap provides an efficient way to maintain a sorted order book by price.

Once orders are grouped by price, a Queue is the most logical data structure to store the actual orders. Since priority is given to the 
earliest arrival time, and orders lose priority when modified, a FIFO (First-In, First-Out) queue ensures fair execution.

### Optimizing Order Lookups
If performance were not a concern, simply separating bids and asks into TreeMaps would be sufficient. 
However, searching for an order by ID would require iterating through all price levels and then through each queue—resulting in a time complexity of O(m × n).
To optimize this, I introduced an index map, where the order ID is the key. 
This improves order lookup time from O(m × n) to O(1) by allowing direct access to the price group in the book. 
This also enhances deletion and modification efficiency from O(m × n) to O(1).

# Matching engine

For the matching engine, I used the peek() and poll() methods from the Queue class to maintain order priority when handling partial fills.
* peek() allows processing an order before deciding whether to remove it.
* poll() removes fully filled orders.
* Any remaining quantity is added back to the order book as a new order.

### Performance Considerations
In the worst-case scenario, processing a matching order involves:

* O(1) to find the matching price level.
* O(m) to traverse the queue at that price.
* O(n) for overall processing.

Thus, the complexity of processing a new match is O(n).

