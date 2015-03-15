package cardType;

import java.util.*;

public class Player {
	private int playerNumber;
	private Hand cards;
	private int money; 
	private static int levelOfBet = -1;
	private boolean allIn = false;
	private boolean fold = false;
	private Player next;
	private Player prev;
	private static Player head;
	private static int size;

	public Player(Hand cards, int money, int playerNumber) {
		this.cards = cards;
		this.money = money;
		this.playerNumber = playerNumber;
	}

	public int getPlayer() {
		return playerNumber;
	}

	public int getSize() {
		return size;
	}

	public void changeSize(int change) {
		size = change;
	} 

	public Player getNext() {
		return next;
	}

	public Player getHead() {
		return head;
	}

	public void setNext(Player player) {
		next = player;
	}

	public void setPrev(Player player) {
		prev = player;
	}

	public void setHead(Player player) {
		head = player;
	}
	
	public void remove() {
		if (this == head) {
			next = head; 
		}
		prev.next = next;
		next.prev = prev;
	}

	public Hand getHand() {
		return cards;
	}

	public int getMoney() {
		return money;
	}

	public void changeMoney(int amount) {
		money += amount;
	}

	public int getLevelOfBet() {
		return levelOfBet;
	}

	public void changeLevelOfBet(int change) {
		levelOfBet = change;
	}

	public void reset() {
		levelOfBet = -1;
	}

	public boolean getAllIn() {
		return allIn;
	}

	public void allIn() {
		allIn = true;
	}

	public boolean getFold() {
		return fold;
	}

	public void fold() {
		fold = true;
	}

	public void makeBet(int bet) {
		money -= bet;
		if (money == 0) {
			allIn = true;
		}
	}


}