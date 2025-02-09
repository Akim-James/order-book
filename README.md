# Limit order-book

In this LimitOrderBook implementation. I chose to go with separating bids and asks, and forced the specification of quantity, price and side.
This will avoid having to traverse through bids when handling an ask, and vice versa. 

By forcing side during creation of an order, we are easily able to store it into the correct book, i.e, a bid into bids and an ask into asks.

For the choice of data structure, I chose to go with a TreeMap with the key being the price for the storage of the two. A tree map offers the most efficient  way of having a sorted order book according to price. 
Once an order is grouped with the correct prices, then it makes sense to go with a Queue for the data structure to store the actual orders.

Since we are giving priority to order with the earliest arrival time and an order has to lose priority during modification, a queue makes sense as the best option to store actual orders, first in, first out (FIFO).

Just separating the two, bids and asks and storing them into tree maps would suffice if we didn't take performance into consideration. 
A search for an order by id would have us iterate through all the prices and then through each queue to find it, this will be of complexity O(m x n).

To improve this, I introduced a map of indexes with the key being the order id. This improves the lookup for an order by id from O(m x n) to O(1). 
This will give the price of the order of which we can use to go to the correct queue by the price grouping. 

This further improves the deletion and modification of an order from O(m x n) to O(1).



# Matching engine

For the matching engine, I chose to go with peek() and poll() methods from the Queue class so that I can maintain the priority of the
orders that are partially filled while at the end adding any remaining quantities as new orders.
Using peek lets us process the order before we decide to remove it from the queue or keep it.
In the worst case scenario, processing a matching order will take O(1) to find the matching group and O(m) to traverse
the queue, so O(n) will be the complexity of processing a new match.