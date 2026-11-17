
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
			thisItem.mutex.acquire();
			if(isInterestedInItem(thisItem)) { // READ HIGHEST BIDDER from item so needs mutex
				thisItem.mutex.release();
				try {
					Thread.sleep(Bidder.THINK_TIME);
				} catch (InterruptedException e){}

				thisItem.mutex.acquire();
				Integer targetPrice = getTargetPrice(thisItem);
				if(getTotalBids() + targetPrice <= this.funds) { // RACE CONDITION BETWEEN BIDDERS because we are checking items other than this one in gettotalbids! NEEDS BIDDER mutex? maybe
					thisItem.bid(this,targetPrice);
					System.out.print("bidder "+id+" just bid "+targetPrice+" on item "+i+" (id "+thisItem.getId()+")\n");
				}
				thisItem.mutex.release();

			} else {
				thisItem.mutex.release();
			}
			i++;
			if(i >= auction.getAuctionItems().size()) {
				i = 0;
			}
		}
		try {
		Thread.sleep(Bidder.THINK_TIME*2); // possible for other threads to still be bidding right after this loop exits up until 2x think time
		}
		catch (InterruptedException e) {}

		// nothing should still be bidding at this point, just readonly for items here
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
	private Integer getTotalBids() {
		Integer total = 0;
		for(int i = 0; i < auction.getAuctionItems().size(); i++) {
			AuctionItem thisItem = auction.getAuctionItems().get(i);
			if(thisItem.getHighestBidder() == this){
				total += thisItem.getPrice();

			}
		}
		return total;
	}

};
