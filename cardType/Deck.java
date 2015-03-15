package cardType;

import java.util.*;

public class Deck {
	public static final int NUMCARDS = 52;
	private Map<Integer, Card> deck = new HashMap<Integer, Card>();
	private ArrayList<Card> community = new ArrayList<Card>();

	// A deck is a dictionary with each integer from 1-52 representing a card.
	public Deck() {
		for (int suit = 0; suit < 4; suit++) {
			for (int i = 1; i < 14; i++) {
				Card newCard = new Card(i, suit);
				deck.put(13 * suit + i, newCard);
			}
		}
	}

	// Returns a list of keys representing the current integers of cards 
	// still in the deck.
	public ArrayList<Integer> currentDeck() {
		return new ArrayList<Integer>(deck.keySet());
	}

	// Picks a card in the deck at random and removes it.
	public Card draw() {
		int randomInt;
		ArrayList<Integer> current = currentDeck();
		do {
			randomInt = (int) (Math.random() * NUMCARDS);
		} while (!current.contains(randomInt));
		Card pickedCard = this.deck.get(randomInt);
		this.deck.remove(randomInt);
		return pickedCard;
	}

	public Hand dealHand() {
		Card first = draw();
		Card second = draw();
		Hand hand = new Hand(first, second);
		return hand;
	}

	public void dealFlop() {
		for (int i = 0; i < 3; i++) {
			community.add(draw());
		}
	}

	public void dealTurnOrRiver() {
		community.add(draw());
	}

	public ArrayList<Card> getCommunity() {
		return community;
	}

	public void printHand() {
		Iterator printe = community.iterator();
		String message = "[";
		while (printe.hasNext()) {
			message += printe.next();
		}
		message += "]";
		System.out.println(message);
	}

	// Possible problem in determining kickers (full house, two pair).
	public ArrayList<Player> bestHand(Player player) {
		ArrayList<Player> winner = new ArrayList<Player>();
		int highestClass = 0;
		int highestValue = 0;
		HandValue highestKicker = null;
		HandValue value;
		while (highestClass == 0 || player.getPlayer() != player.getHead().getPlayer()) {
			if (player.getFold() == true) {
				player = player.getNext();
				continue;
			}
			value = player.getHand().valueOfThisHand(community, 5);
			System.out.println(player.getHand().getCards()[0].getNumber());
			System.out.println(player.getHand().getCards()[1].getNumber());
			System.out.println("The HandValue of this " + player.getPlayer() + " hand is " + value.printHandValue());
			if (value.getCardClass() > highestClass) {
				winner.clear();
				winner.add(player);
				highestClass = value.getCardClass();
				highestValue = value.getValue();
				highestKicker = value.getKicker();
			} else if (value.getCardClass() == highestClass) {
				if (value.getValue() > highestValue) {
					winner.clear();
					winner.add(player);
					highestClass = value.getCardClass();
					highestValue = value.getValue();
					highestKicker = value.getKicker();
				} else if (value.getValue() == highestValue) {
					int tieBreaker = compareKicker(value.getKicker(), highestKicker);
					switch (tieBreaker) {
						case 1: 
							break;
						case 2:
							winner.clear();
							winner.add(player);
							highestClass = value.getCardClass();
							highestValue = value.getValue();
							highestKicker = value.getKicker();
							player = player.getNext();
							break;
						case 3:
							winner.add(player);
							break;
					}
				}
			}
			player = player.getNext();	
		}
		return winner;
	}

	// 1 means original wins
	// 2 means kicker2 wins
	// 3 means both win
	public int compareKicker(HandValue original, HandValue kicker2) {
		if (original == null && kicker2 == null) {
			return 3;
		}
		if (original.getCardClass() < kicker2.getCardClass()) {
			return 1;
		} else if (original.getCardClass() == kicker2.getCardClass()) {
			if (original.getValue() > kicker2.getValue()) {
				return 1;
			} else if (original.getValue() == kicker2.getValue()) {
				return compareKicker(original.getKicker(), kicker2.getKicker());
			} else {
				return 2;
			}
		} else {
			return 2;
		}
	}
}