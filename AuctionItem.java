
class AuctionItem {

	private Integer id;
	private Integer price; 
	private Bidder highest_bidder;
	private volatile boolean priceWrite;

	public AuctionItem (Integer id, Integer starting_price) {
		this.id = id;
		this.price = starting_price;
		this.highest_bidder = null;
	}

	public Integer getId () {
		return this.id;
	}

	public Bidder getHighestBidder () {
		while(priceWrite){};
		return this.highest_bidder;	
	}

	public Integer getPrice () {
	while(priceWrite){};
		return this.price;
	}

	public synchronized boolean bid(Bidder bidder, Integer offer) {
		priceWrite = true;
		if(offer < this.price) {
			priceWrite = false;
			return false;
		}
		this.highest_bidder = bidder;
		this.price = offer;
		priceWrite = false;
		return true;
	}
};
