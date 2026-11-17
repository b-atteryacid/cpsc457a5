
import java.lang.Thread;

class Bidder extends Thread {
	static final Integer THINK_TIME = 500;
	private Integer id;
	private Integer funds;
	private Auction auction;

	public Bidder (Integer id, Integer starting_funds, Auction auction) {
		this.id = id;
		this.funds = starting_funds;
		this.auction = auction;
	}

	private Integer getTargetPrice(AuctionItem item) {
		if(item.getHighestBidder() == null) {
			return item.getPrice();
		} else {
			return item.getPrice() + 5;
		}
	}

	@Override
	public void run () {
		int i = 0;

		while (auction.isActive ()) {

			AuctionItem thisItem = auction.getAuctionItems().get(i);

			thisItem.readerEntry();
			if(isInterestedInItem(thisItem)) {
				thisItem.readerExit();

				try {
					Thread.sleep(Bidder.THINK_TIME);
				} catch (InterruptedException e){}

				// Check item price and, if affordable, bid on it in the same block
				thisItem.writerEntry();
				Integer targetPrice = getTargetPrice(thisItem);
				if(targetPrice <= getFunds()) {
					thisItem.bid(this,targetPrice);
					System.out.print("Bidder "+id+" just bid $"+targetPrice+" on item "+i+" (id "+thisItem.getId()+")\n");
				}
				thisItem.writerExit();

			} else {
				thisItem.readerExit();
			}

			// cycle through all the auction items until auction is closed
			i++;
			if(i >= auction.getAuctionItems().size()) {
				i = 0;
			}
		}
	}

	public Integer getBidderId () {
		return this.id;
	}

	public synchronized Integer getFunds () {
		return this.funds;
	}

	// Called when another bidder outbid this one, now it knows it can reuse these funds
	public synchronized void returnFunds(Integer funds) {
		this.funds += funds;
	}

	// Called when making a new bid
	public synchronized void allocateFunds(Integer funds) {
		this.funds -= funds;
	}

	private Boolean isInterestedInItem (AuctionItem item) {
		Boolean is_interested = true;
			
		/* Simulate evaluating the item. */
		try {
			Thread.sleep (Bidder.THINK_TIME);
		}
		catch (InterruptedException e) {
		}

		/* Ensure we're no bidding against ourself. */
		is_interested &= item.getHighestBidder () != this; 
		/* 
		 * Use prime decomposition to add a bit of variance in who's interested in what items.
		 * Included a fix for bidder 1 who would never bid on anything.
	     */
		is_interested &= this.id % item.getId () == 0 || this.id == 1; 
		return is_interested;
	}

};
