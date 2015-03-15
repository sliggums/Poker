package cardType;

public class HandValue {
	// The cardClass of hand it is.
	// cardClass rankings: 
	// 1 = High Card
	// 2 = One Pair
	// 3 = Three of Kind
	// 4 = Four of a Kind
	// 5 = Straight
	// 6 = Flush
	// 7 = Straight Flush
	private int cardClass; 
	// The value of this cardClass. ex: A pair of kings would have value of 13.
	private int value; 
	// The kicker of this group of cards. Not every group will have a kicker.
	private HandValue kicker;

	public HandValue(int cardClass, int value) {
		this.cardClass = cardClass;
		this.value = value;
		this.kicker = null;
	}

	public HandValue(int cardClass, int value, HandValue kicker) {
		this.cardClass = cardClass;
		this.value = value;
		this.kicker = kicker;
	}

	public int getCardClass() {
		return cardClass;
	}

	public int getValue() {
		return value;
	}

	public HandValue getKicker() {
		return kicker;
	}

	public String printHandValue() {
		String message = "{";
		message += cardClass + ", ";
		message += value + ", ";
		if (kicker != null) {
			message += kicker.printHandValue();
		}
		message += "}";
		return message;
	}

}