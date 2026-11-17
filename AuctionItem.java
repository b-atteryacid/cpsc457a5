
class AuctionItem {

	private Integer id;
	private Integer price; 
	private Bidder highest_bidder;
	public Semaphore mutex;


	public AuctionItem (Integer id, Integer starting_price) {
		this.id = id;
		this.price = starting_price;
		this.highest_bidder = null;
		mutex = new Semaphore(1);
	}

	public Integer getId () {
		return this.id;
	}

	public Bidder getHighestBidder () {
		return this.highest_bidder;	
	}

	public Integer getPrice () {
		return this.price;
	}

	public synchronized boolean bid(Bidder bidder, Integer offer) {
		if(offer < this.price) {
			return false;
		}
		this.highest_bidder = bidder;
		this.price = offer;
		return true;
	}
};
