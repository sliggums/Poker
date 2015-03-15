package cardType;

import java.util.*;

public class Hand {
	private Card[] cards = new Card[2];
	public HandValue value = null;

	protected Hand(Card card1, Card card2) {
		cards[0] = card1;
		cards[1] = card2;
	}

	public Card[] getCards() {
		return cards;
	}

	// HandValues are being processed wrong.

	public void changeCard(Card[] cards, int oldNum,
							int newNum, int newSuit) {
		for (int i = 0; i < cards.length; i++) {
			if (cards[i].getNumber() == oldNum) {
				cards[i] = new Card(newNum, newSuit);
			}
		}
	}

	protected HandValue valueOfThisHand(ArrayList<Card> community, int cardsLeft) {
		HandValue handy = null;
		ArrayList<Card> majorityRemoved = new ArrayList<Card>();
		// If we have the normal 7 cards.
		if (cardsLeft == 5) {
			Card[] allCards = merge(community);
			int[][] largest = largestValues(allCards);
			// If we have more than 5 consecutive Cards.
			if (largest[1][0] >= 5) {
				handy = new HandValue(6, largestFlushValue(allCards, largest[1][1]));
			}
			if (largest[2][0] >= 4) { 
				// Check if we have a royal flush.
				for (int i = 0; i < 3; i++) {
					switch (i) {
						case 0:
							break;
						case 1:
							changeCard(allCards, largestFlushValue(allCards, largest[1][1]), -1, -1);
							break;
						case 2:
							changeCard(allCards, largest[2][1], -1, -1);
							break;
					}
					if (checkStraightFlush(allCards, largestFlushValue(allCards, largest[1][1]), largest[1][1])) {
						handy = new HandValue(7, largestFlushValue(allCards, largest[1][1]));
						break;
					}
				}
				if (handy == null) {
					handy = new HandValue(5, largest[2][1]);
				}
			}
			// If neither straight or flush, we look for x-of-a-kind/high card.
			else {
				for (Card card : allCards) {
					if (card.getNumber() != largest[0][1] && !(card.getNumber() == 1 && largest[0][1] == 14)) {
						majorityRemoved.add(card);
					}
				}
				handy = new HandValue(largest[0][0], largest[0][1], 
						valueOfThisHand(majorityRemoved, 5 - largest[0][0]));
			}
		// Less than 7 cards.
		} else {
			Card[] allCards = convert(community);
			int[][] largest = largestValues(allCards);
			if (largest[0][0] > cardsLeft) {
				int i = largest[0][0] - cardsLeft;
				for (Card card: allCards) {
					if ((card.getNumber() != largest[0][1] && !(card.getNumber() == 1 && largest[0][1] == 14)) || i == 0) {
						majorityRemoved.add(card);
					} else {
						i--;
					}
				}
				return valueOfThisHand(majorityRemoved, cardsLeft);
			} else if (largest[0][0] == cardsLeft) {
				handy = new HandValue(largest[0][0], largest[0][1]);
			} else {
				for (Card card : allCards) {
					if (card.getNumber() != largest[0][1] && !(card.getNumber() == 1 && largest[0][1] == 14)) {
						majorityRemoved.add(card);
					}
				}
				handy = new HandValue(largest[0][0], largest[0][1], 
					valueOfThisHand(majorityRemoved, cardsLeft - largest[0][0]));

			}
		}
		value = handy;
		return handy;

	}

	// Outputs: 
	// int[0][0] = greatest amount of same number.
	// int[0][1] = value of card that occurs greatest number of times.
	// int[1][0] = number of most popular suit.
	// int[1][1] = most popular suit.
	// ** IF COMMUNITY + HAND == 7 **
	// int[2][0] = number of consecutive cards. 
	// int[2][1] = number of highest consecutive card. 
	protected int[][] largestValues(Card[] community) {
		int numberOfCards = community.length;
		int[] numbersOnCards = new int[numberOfCards];
		int[] straightChecking = new int[numberOfCards];
		int[] suitOnCards = new int[numberOfCards];
		for (int i = 0; i < numberOfCards; i++) {
			int number = community[i].getNumber();
			straightChecking[i] = number;
			if (number == 1) {
				straightChecking[i] = 14;
			}
			numbersOnCards[i] = number;
			suitOnCards[i] = community[i].getSuit();
		}
		// Need to sort them.
		Arrays.sort(numbersOnCards);
		Arrays.sort(suitOnCards);
		Arrays.sort(straightChecking);
		int[] numberGreatest = greatestAmount(numbersOnCards, 0);
		int[] suitGreatest = greatestAmount(suitOnCards, 1);
		if (numberOfCards == 7) {
			int[] straightPotential = straightExists(numbersOnCards);
			if (straightPotential[0] < 4) {
				straightPotential = straightExists(straightChecking);
			}
			int[][] output = new int[3][2];
			output[0] = numberGreatest;
			output[1] = suitGreatest;
			output[2] = straightPotential;
			return output;
		}
		int[][] output = new int[2][2];
		output[0] = numberGreatest;
		output[1] = suitGreatest;
		return output;
	}

	protected int[] greatestAmount(int[] cardsInHand, int type) {
		int current = cardsInHand[0];
		int greatestValue = 0;
		int currentCount = 0;
		int greatestCount = 0;
		for (int value : cardsInHand) {
			if (current == value) {
				currentCount++;
			} 
			if (current != value) {
				currentCount = 1;
				current = value;
			}
			if (type == 0) {
				if (greatestCount == currentCount) {
					if (current > greatestValue) {
						greatestValue = current;
					}
				}
			} 
			if (greatestCount < currentCount) {
				greatestCount = currentCount;
				greatestValue = current;
				if (value == 1) {
					// Aces have the greatest value.
					greatestValue = 14;			
				}
			}
		}
		int[] output = new int[2];
		output[0] = greatestCount;
		output[1] = greatestValue;
		return output;
	}

	// merge() Takes in the community cards and returns an entire array of
	// Cards that includes both the community and the hand. 
	protected Card[] merge(ArrayList<Card> community) {
		int size = community.size();
		Card[] merged = new Card[2 + size];
		merged[size + 1] = cards[1];
		merged[size] = cards[0];
		for (int i = 0; i < size; i++) {
			merged[i] = community.get(i);
		}
		return merged;
	}

	protected Card[] convert(ArrayList<Card> community) {
		int size = community.size();
		Card[] converted = new Card[size];
		for (int i = 0; i < size; i++) {
			converted[i] = community.get(i);
		}
		return converted;
	}

	protected int[] straightExists(int[] cardsInHand) {
		int greatestLength = 0;
		int currentLength = 0;
		int greatestValue = 0;
		for (int i = 0; i < cardsInHand.length - 1; i++) {
			int next = cardsInHand[i + 1];
			if (cardsInHand[i] == next) {
				continue;
			}
			else if (cardsInHand[i] + 1 == next) {
				currentLength++;
				if (currentLength > greatestLength) {
					greatestLength = currentLength;
					greatestValue = next;
				}
			} else { 
				currentLength = 0;
			}
		}
		int[] output = new int[2];
		output[0] = greatestLength;
		output[1] = greatestValue;
		return output;
	}

	// checkStraightFlush takes in the cards, the highest straight number
	// and the suit to return a boolean of whether there is a straight flush. 
	protected boolean checkStraightFlush(Card[] cards, 
										int highest, int suit) {
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for (int i = 0; i < 5; i++) {
			numbers.add(highest - i);
		}
		for (Card card : cards) {
			if (numbers.indexOf(card.getNumber()) != -1 && card.getSuit() == suit) {
				numbers.remove(numbers.indexOf(card.getNumber()));
			}
		}
		return (numbers.size() == 0) ? true : false;
	}

	protected int largestFlushValue(Card[] cards, int suit) {
		int largestValue = 0;
		for (Card card : cards) {
			if (card.getSuit() == suit) {
				if (card.getNumber() > largestValue) {
					largestValue = card.getNumber();
				}
			}
		}
		return largestValue;
	}

	protected void printArray(int[] array) {
		String message = "[";
		for (int k : array) {
			message += k + ", ";
		}
		message += "]";
		System.out.println(message);
	}

	public static void main(String[] args) {
		Card card1 = new Card(2, 4);
		Card card2 = new Card(5, 1);
		Card card3 = new Card(7, 2);
		Card card4 = new Card(12, 2);
		Card card5 = new Card(4, 1);
		ArrayList<Card> comm = new ArrayList<Card>();
		comm.add(card1);
		comm.add(card2);
		comm.add(card3);
		comm.add(card4);
		comm.add(card5);
		Card card6 = new Card(12, 4);
		Card card7 = new Card(2, 2);
		Hand hand = new Hand(card6, card7);
		//comm.add(card6);
		//comm.add(card7);
		//int[] test = {2, 3, 4, 5};
		//hand.printArray(hand.greatestAmount(test, 0));
		HandValue shit = hand.valueOfThisHand(comm, 5);
		System.out.println(shit.printHandValue());
	}
}
