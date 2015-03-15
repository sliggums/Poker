package cardType;

import java.util.*;

public class Card{
	private int number;
	private int suit;

	public Card(int number, int suit) {
		this.number = number;
		this.suit = suit;
	}

	// The number of the Card. Between 1-13.
	public int getNumber() {
		return number;
	}
	// The suit of the Card. Either "S", "H", "C", or "D".
	public int getSuit() {
		return suit;
	}

	public String determineNumber() {
		switch(number) {
			case 11: return "J";
			case 12: return "Q";
			case 13: return "K";
			case 1: return "A";
			default: return Integer.toString(number);
		}
	}

	public char determineSuit() {
		switch(suit) {
			case 0: return (char)'\u2660';
			case 1: return (char)'\u2666';
			case 2: return (char)'\u2663';
			case 3: return (char)'\u2764';
			default: return (char) 'a';
		}
	}
}