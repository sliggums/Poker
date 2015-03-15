import cardType.*;
import java.util.*;

public class Table {
	protected Deck deck;
	protected int pot = 0;
	protected int currentBet = 0;
	protected int numPlayers = 0;
	protected Scanner user_input = new Scanner(System.in);
	protected int startingMoney = 0;
	protected Player curr;
	protected boolean first = true;

	protected Table() {
		newGame();
	}

	protected void resetBet() {
		currentBet = 0;
	}

	protected void newGame() {
		String message = "How many players? ";
		String players;
		while (numPlayers == 0 || numPlayers > 23 || numPlayers < 2) {
			try {
				System.out.print(message);
				players = user_input.next();
				numPlayers = Integer.parseInt(players);
			} catch (NumberFormatException e) {
				message = "Please enter a number less than 23: ";
			}
		}
		message = "How much money does each player start off with? ";
		while (startingMoney == 0) {
			try {
				System.out.print(message);
				players = user_input.next();
				startingMoney = Integer.parseInt(players);
			} catch (NumberFormatException e) {
				message = "Please enter a number: ";
			}
		}
	}

	protected void play() {
		deck = new Deck();
		// Creating Player linked list.
		for (int i = 0; i < numPlayers; i++) {
			Player newPlayer = new Player(deck.dealHand(), startingMoney, i);
			newPlayer.changeSize(newPlayer.getSize() + 1);

			// Maybe implementation for 2 players is wrong...

			if (i == 0) {
				newPlayer.setHead(newPlayer);
				curr = newPlayer;
			} else if (i == numPlayers - 1) {
				curr.setNext(newPlayer);
				newPlayer.setPrev(curr);
				newPlayer.getHead().setPrev(newPlayer);
				newPlayer.setNext(newPlayer.getHead());
				curr = newPlayer.getHead();
			} else {
				curr.setNext(newPlayer);
				newPlayer.setPrev(curr);
				curr = newPlayer;
			}
			System.out.println("Player " + i + ", your cards are: ");
			printCardsArray(newPlayer.getHand().getCards());
		}
		bettingProcess(curr);
		System.out.println("Here's the flop: ");
		deck.dealFlop();
		printCards(deck.getCommunity());
		bettingProcess(curr);
		System.out.println("Here's the turn: ");
		deck.dealTurnOrRiver();
		printCards(deck.getCommunity());
		bettingProcess(curr);
		System.out.println("Here's the river: ");
		deck.dealTurnOrRiver();
		printCards(deck.getCommunity());
		bettingProcess(curr);
		ArrayList<Player> winner = deck.bestHand(curr.getHead());
		int moneyGiven = pot / winner.size(); // House takes a share!
		Iterator<Player> iterateWinners = winner.iterator();
		while (iterateWinners.hasNext()) {
			Player winningPlayer = iterateWinners.next();
			winningPlayer.changeMoney(moneyGiven);
			HandValue valueWinning = winningPlayer.getHand().value;
			System.out.println("Player " + winningPlayer.getPlayer() + " won with a " + winMessage(valueWinning) + "!");
			System.out.println("Player " + winningPlayer.getPlayer() + " won " + moneyGiven + "!");
		}
	}

	protected void bettingProcess(Player currentPlayer) {
		while (currentPlayer.getPlayer() != currentPlayer.getLevelOfBet() || first) {
			if (currentPlayer.getAllIn() || currentPlayer.getFold()) {
				currentPlayer = currentPlayer.getNext();
			} else {
				bet(currentPlayer);
				currentPlayer = currentPlayer.getNext();
			}
		}
		resetBet();
		currentPlayer.reset();
		first = true;
	}

	protected void bet(Player player) {
		String option = null;
		//boolean selected = false;
		String message;
		//while (!selected) {
		while (option == null) {
			if (currentBet == 0) {
				try {
					if (first) {
						first = false;
						player.changeLevelOfBet(player.getPlayer());
					}
					message = "Player " + player.getPlayer() + ": Check or bet? ";
					System.out.print(message);
					option = user_input.next();
					switch (option) {
						case "check":
							//selected = true;
							break;
						case "bet":
							//selected = true;
							raise(player);
							break;
						default:
							throw new GeneralException();
					}
				} catch (GeneralException e) {
					message = "Please choose to either 'check' or 'bet'.";
				}
			} else {
				message = "Player " + player.getPlayer() + ": Call, raise, or fold? Current bet is at " + currentBet + ". ";
				try {
					System.out.print(message);
					option = user_input.next();
					switch (option) {
						case "call":
							player.makeBet(currentBet);
							pot += currentBet;
							System.out.println("Player " + player.getPlayer() + " has called " + currentBet + ".");
							System.out.println("LevelofBet:" + player.getLevelOfBet());
							break;
						case "fold":
							player.fold();
							break;
						case "raise":
							raise(player);
							break;
						default:
							throw new GeneralException();
					}
				} catch (GeneralException e) {
					message = "Please choose either 'call', 'fold', or 'raise'.";
				}
			}
		}
	}

	protected void raise(Player player) {
		String betAmount = null;
		String message = "How much would you like to bet, player " + player.getPlayer() + "? ";
		while (betAmount == null) {
			try {
				System.out.print(message);
				betAmount = user_input.next();
				currentBet = Integer.parseInt(betAmount);
				if (currentBet > player.getMoney()) {
					throw new GeneralException();
				}
				player.makeBet(currentBet);
				pot += currentBet;
				player.changeLevelOfBet(player.getPlayer());
				System.out.println("LevelofBet:" + player.getLevelOfBet());
				System.out.println("Player " + player.getPlayer() + " has bet " + currentBet + ".");
			} catch (NumberFormatException e) {
				message = "Please enter a number: ";
			} catch (GeneralException e) {
				message = "Not enough money! Bet something else: ";
				betAmount = null;
			}
		}
	}

	protected void printBlank(int numCards) {
		printer(numCards, "------ ");
		printer(numCards, "|    | ");
		printer(numCards, "| ?? | ");
		printer(numCards, "|    | ");
		printer(numCards, "------ ");
	}

	private void printer(int numCards, String text) {
		for (int i = 0; i < numCards; i++) {
			if (i == numCards - 1) {
				System.out.println(text);
			} else {
				System.out.print(text);
			}
		}
	}

	// Need to change argument to ArrayList<Card>
	protected void printCards(ArrayList<Card> cards) {
		int numCards = cards.size();
		printer(numCards, "------ ");
		printer(numCards, "|    | ");
		for (int i = 0; i < numCards; i++) {
			String output = "| " + cards.get(i).determineNumber();
			int number = cards.get(i).getNumber();
			if (number == 10) {
				output = "|" + cards.get(i).determineNumber();
			}
			if (i == numCards - 1) {
				System.out.println(output + cards.get(i).determineSuit() + " | ");
			} else {
				System.out.print(output + cards.get(i).determineSuit() + " | ");
			}
		}
		printer(numCards, "|    | ");
		printer(numCards, "------ ");
	}

	protected void printCardsArray(Card[] cards) {
		int numCards = cards.length;
		printer(numCards, "------ ");
		printer(numCards, "|    | ");
		for (int i = 0; i < numCards; i++) {
			String output = "| " + cards[i].determineNumber();
			int number = cards[i].getNumber();
			if (number == 10) {
				output = "|" + cards[i].determineNumber();
			}
			if (i == numCards - 1) {
				System.out.println(output + cards[i].determineSuit() + " | ");
			} else {
				System.out.print(output + cards[i].determineSuit() + " | ");
			}
		}
		printer(numCards, "|    | ");
		printer(numCards, "------ ");
	}

	protected String winMessage(HandValue valueWinning) {
		String winningHand = "";
		switch (valueWinning.getCardClass()) {
			case 1:
				winningHand = "high card of " + valueWinning.getValue();
				winningHand += " with a " + valueWinning.getKicker().getValue() + " kicker!";
				break;
			case 2:
				System.out.println(valueWinning.getKicker().getCardClass());
				if (valueWinning.getKicker().getCardClass() == 2) {
					winningHand = "two pair of " + valueWinning.getValue() + " and " + valueWinning.getKicker().getValue();
					winningHand += " with a " + valueWinning.getKicker().getKicker().getValue() + " kicker!";
				} else {
					winningHand = "pair of " + valueWinning.getValue() + "'s";
					winningHand += " with a " + valueWinning.getKicker().getValue() + " kicker!";
				}
				break;
			case 3:
				if (valueWinning.getKicker().getCardClass() == 2) {
					winningHand = "full house of " + valueWinning.getValue() + "'s and " + valueWinning.getKicker().getValue() + "'s!";
				} else {
					winningHand = "three of a kind of " + valueWinning.getValue() + "'s";
					winningHand += " with a " + valueWinning.getKicker().getValue() + " kicker!";
				}
				break;
			case 4:
				winningHand = "four of a kind of " + valueWinning.getValue() + "'s";
				winningHand += " with a " + valueWinning.getKicker().getValue() + " kicker!";
				break;
			case 5:
				winningHand = valueWinning.getValue() + " high straight!";
				break;
			case 6:
				winningHand = valueWinning.getValue() + " high flush!";
			case 7:
				if (valueWinning.getValue() == 14) {
					winningHand = "straight flush!";
				} else {
					winningHand = valueWinning.getValue() + " high straight flush!";
				}
				break;
			}
		return winningHand;
	}

	public static void main(String[] args) {
		Table table = new Table();
		while(true) {
			table.play();
		}
	}
}