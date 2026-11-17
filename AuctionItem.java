
class AuctionItem {

	private Integer id;
	private Integer price; 
	private Bidder highest_bidder;
	public Semaphore mutex;
	public Semaphore CS;
	private int readers;


	public AuctionItem (Integer id, Integer starting_price) {
		this.id = id;
		this.price = starting_price;
		this.highest_bidder = null;
		this.readers = 0;
		mutex = new Semaphore(1);
		CS = new Semaphore(1);
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

	public boolean bid(Bidder bidder, Integer offer) {
		if(offer < this.price) {
			return false;
		}
		if(this.highest_bidder != null) {
			this.highest_bidder.returnFunds(this.price);
		}
		this.highest_bidder = bidder;

		this.price = offer;
		bidder.allocateFunds(offer);
		return true;
	}

	public void readerEntry() {
		mutex.acquire();
		readers++;
		if(readers == 1) {
			CS.acquire();
		}
		mutex.release();

	}

	public void readerExit() {
		mutex.acquire();
		readers--;
		if(readers == 0) {
			CS.release();
		}
		mutex.release();
	}

	public void writerEntry() {
		CS.acquire();
	}

	public void writerExit() {
		CS.release();
	}
};
