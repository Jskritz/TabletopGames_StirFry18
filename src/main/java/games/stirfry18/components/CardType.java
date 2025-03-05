package games.stirfry18.components;

public enum CardType {
    Shrimp(6, " 9 points with Ginger, 11 points with Soy Sauce and Ginger, when discarded draw 4 cards",1),
    Pork(5, "8 points with Mushrooms, when discarded draw 3 cards",1),
    Chicken(3, "5 points with Green Onion, 7 points with Ginger and Green Onion, when discarded draw 2 cards",1),
    Ginger(2, "4 points with Pork",2),
    GreenOnion(3,"5 points with Shrimp",2),
    SoySauce(1, "2 points with Green Onion, 3 points with Ginger",3),
    Mushrooms(2, "3 points with Chicken",3),
    Noodles(1,"needed to stir fry",5);

    // TODO: refactor to include the different scorings in the enum

    private final String cardText;
    private final int value;
    private final int quantity;

    CardType(int value, String text, int quantity) {
        this.value = value;
        this.cardText = text;
        this.quantity = quantity;
    }

    public int getValue() {
        return value;
    }
    public int getQuantity() {
        return quantity;
    }
}
