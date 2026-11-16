
import java.lang.Thread;

class Bidder extends Thread {
	static final Integer THINK_TIME = 20;
	private Integer id;
	private Integer funds;
	private Auction auction;

	public Bidder (Integer id, Integer starting_funds, Auction auction) {
		this.id = id;
		this.funds = starting_funds;
		this.auction = auction;
	}

	@Override
	public void run () {
		int i = 0;

		while (auction.isActive ()) {

			AuctionItem thisItem = auction.getAuctionItems().get(i);
			if(isInterestedInItem(thisItem)) {
				//System.out.print("bidder "+id+" interested in "+i+"\n");
				try {
					Thread.sleep(Bidder.THINK_TIME);
				} catch (InterruptedException e){
				}

				Integer targetPrice;
				synchronized(thisItem) {
					if(thisItem.getHighestBidder() == null) {
						targetPrice = thisItem.getPrice();
					} else {
						targetPrice = thisItem.getPrice()+5;
					}
					}
					if(getTotalBids() + targetPrice <= this.funds) {
						thisItem.bid(this,targetPrice);
						System.out.print("bidder "+id+" just bid "+targetPrice+" on item "+i+" (id "+thisItem.getId()+")\n");
					}


			} else {
				//System.out.print("bidder "+id+" NOT interested in "+i+"\n");
			}
			i++;
			if(i >= auction.getAuctionItems().size()) {
				i = 0;
			}
		}

		for(int j = 0; j < auction.getAuctionItems().size(); j++) {
			AuctionItem thisItem = auction.getAuctionItems().get(j);
			if(thisItem.getHighestBidder() == this) {
				this.funds -= thisItem.getPrice();
			}
		}
	}

	public Integer getBidderId () {
		return this.id;
	}

	public Integer getFunds () {
		return this.funds;
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

	/*
	@brief Get the total amount of funds that this Bidder has bid (and is winning).
	Used to prevent a bidder from bidding on more than it can afford
	*/
	private synchronized Integer getTotalBids() {
		Integer total = 0;
		for(int i = 0; i < auction.getAuctionItems().size(); i++) {
			AuctionItem thisItem = auction.getAuctionItems().get(i);
			synchronized(thisItem) {
			if(thisItem.getHighestBidder() == this){
				total += thisItem.getPrice(); // race condition? use mutex?
			}
			}
		}
		return total;
	}

};
