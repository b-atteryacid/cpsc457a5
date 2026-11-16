
import java.lang.Thread;

class Bidder extends Thread {
	static final Integer THINK_TIME = 50;
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
			/* <<TODO>> */

			if(isInterestedInItem(auction.getAuctionItems().get(i))) {
				System.out.print("bidder "+id+" interested in "+auction.getAuctionItems().get(i)+"\n");
				try {
				Thread.sleep(Bidder.THINK_TIME);
				} catch (InterruptedException e){
				}

				auction.getAuctionItems().get(i).bid(this,auction.getAuctionItems().get(i).getPrice()+5);

			} else {
			System.out.print("bidder "+id+" NOT interested in "+auction.getAuctionItems().get(i)+"\n");
			}
			i++;
			if(i >= auction.getAuctionItems().size()) {
				i = 0;
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

};
