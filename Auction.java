
import java.util.ArrayList;

class Auction {

	static final Integer MIN_ARGS = 2;
	static final Integer DURATION = 15000; /* 60s */


	private ArrayList <AuctionItem> items; /* Items available in the auction. */
	private ArrayList <Bidder> bidders; /* Bidders participating in the auction. */

	private volatile Boolean is_active; /* A boolean flag indicating whether or not the auction is active or not. */

	static void main (String [] args) {

		if (args.length < Auction.MIN_ARGS) {
			System.out.printf ("Requires two arguments: <num_items> <num_bidders>\n");
		}
		else {
			Auction auction = new Auction (Integer.parseInt (args [0]), Integer.parseInt (args [1]));

			auction.runAuction ();
		}
	};

	/**
	 * @author meevs
	 * @date 2026-11-06
	 *
	 * @brief Constructor for the Auction.
	 *
	 * @param [in] "num_items" An Integer denoting the number of items that are available to purchase in the auction.
	 * @param [in] "num_bidders" An Integer denoting the number of bidders that are participating within the auction.
	 */
	public Auction (Integer num_items, Integer num_bidders) {
		this.initAuctionItems (num_items);
		this.initBidders (num_bidders);
	}

	/**
	 * @author meevs
	 * @date 2026-11-04
	 *
	 * @brief Returns a Boolean flag indicating whether or not the auction is currently active.
	 *
	 * @returns A Boolean flag indicating whether or not the auction is currently active.
	 */
	public Boolean isActive () {
		return this.is_active;
	}


	/**
	 * @author meevs
	 * @date 2026-11-04
	 *
	 * @brief Gets the AuctionItems available for sale in this Auction.
	 *
	 * @returns An ArrayList of AuctionItems representing the items available to purchase in this auction.
	 */
	public ArrayList <AuctionItem> getAuctionItems () {
		return this.items;
	}

	/**
	 * @author meevs
	 * @date 2026-11-04
	 *
	 * @brief Initializes items for the auction.
	 *
	 * @param [in] "item_count" The number of items to initialize the auction with.
	 */
	private void initAuctionItems (Integer item_count) {
		Integer prime_count = 0;
		Integer n = 2;
		ArrayList <Integer> primes = new ArrayList <Integer> (item_count);


		this.items = new ArrayList <AuctionItem> (item_count);

		while (prime_count < item_count) {
			for (; true; n++) {
				if (isPrime (n)) {
					primes.add (prime_count++, n++);
					break;
				}
			}
		}

		System.out.printf ("\n\nItems available for sale:\n");
		for (int item_index = 0; item_index < item_count; item_index++) {
			this.items.add (item_index, new AuctionItem (primes.get (item_index), 250));

			System.out.printf ("Item %d (Id %d) is available with a starting price of $%d.\n", item_index, this.items.get (item_index).getId (), this.items.get (item_index).getPrice ());
		};
	}

	/**
	 * @author meevs
	 * @date 2026-11-04
	 *
	 * @brief Initializes the bidders for the auction.
	 *
	 * @param [in] "bidder_count" The number of bidders to initialize the auction with.
	 */
	private void initBidders (Integer bidder_count) {
		final Integer STARTING_FUNDS = 1000;
		this.bidders = new ArrayList <Bidder> (bidder_count);

		System.out.printf ("\n\nBidders participating:\n");
		for (int bidder_index = 0; bidder_index < bidder_count; bidder_index++) {
			this.bidders.add (bidder_index, new Bidder (
				bidder_index,
				STARTING_FUNDS,
				this
			));

			System.out.printf (
				"Bidder %d with $%d.\n",
				this.bidders.get (bidder_index).getBidderId (),
				this.bidders.get (bidder_index).getFunds ()
			);

		}
	}

	/**
	 * @author meevs
	 * @date 2026-11-04
	 *
	 * @brief Opens the auction to bidders.
	 */
	private void startAuction () {

		System.out.printf ("\n\nThe auction is now open!\n\n");

		this.is_active = true;
		for (int bidder_index = 0; bidder_index < this.bidders.size (); bidder_index++) {
			this.bidders.get (bidder_index).start ();
		}
	}

	/**
	 * @author meevs
	 * @date 2026-11-04
	 *
	 * @brief Closes the auction.
	 */
	private void closeAuction () {
		this.is_active = false;
		for (int bidder_index = 0; bidder_index < this.bidders.size (); bidder_index++) {
			try {
				this.bidders.get (bidder_index).join ();
			}
			catch (InterruptedException e) {
				System.out.printf ("This probably shouldn't have happened...\n");
			}
		}

		System.out.printf ("\n\nThe auction has now concluded!\n\n");
	}

	/**
	 * @author meevs
	 * @date 2026-11-04
	 *
	 * @brief Runs the auction for 30 seconds.
	 */
	private void runAuction () {
		this.startAuction ();

		try {
			Thread.sleep (Auction.DURATION); /* Sleep for 30s. */ 
		}
		catch (InterruptedException e) {

		}

		this.closeAuction ();

		/* Print out summary of auction. */
		System.out.printf ("\n\nBidder Summary:\n");
		for (int bidder_index = 0; bidder_index < this.bidders.size (); bidder_index++) {
			System.out.printf ("\tBidder %d was left with $%d.\n", this.bidders.get (bidder_index).getBidderId (), this.bidders.get (bidder_index).getFunds ());
		}

		System.out.printf ("\n\nItem Summary:\n");
		for (int item_index = 0; item_index < this.items.size (); item_index++) {
			Bidder highest_bidder = this.items.get (item_index).getHighestBidder ();

			if (highest_bidder != null) {
				System.out.printf ("\tItem %d (Id %d) sold to Bidder %d for $%d.\n", item_index, this.items.get (item_index).getId (), highest_bidder.getBidderId (), this.items.get (item_index).getPrice ());
			}
			else {
				System.out.printf ("\tItem %d (Id %d) recieved no bid!\n", item_index, this.items.get (item_index).getId ());
			}
		}
	}

	/**
	 * @author meevs
	 * @date 2026-11-05
	 *
	 * @brief Determines whether or not the given number is prime.
	 *
	 * @param [in] "n" The given Integer which will be checked if it is prime or not.
	 *
	 * @returns A Boolean flag indicating whether or not the given number is prime or not.
	 */
	private Boolean isPrime (Integer n) {
		if (n <= 1) {
			return false;
		}

		for (Integer i = 2; i <= n / 2; i++) {
			if (n % i == 0) {
				return false;
			}
		}

		return true;
	}
}
