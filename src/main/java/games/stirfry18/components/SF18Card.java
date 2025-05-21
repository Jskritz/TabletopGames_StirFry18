package games.stirfry18.components;

public enum SF18Card {
    SHRIMP("Shrimp", 6, 4, 1),
    PORK("Pork", 5, 3, 1),
    CHICKEN("Chicken", 3, 2, 1),
    GINGER("Ginger", 2, 0, 2),
    GREEN_ONION("GreenOnion", 3, 0, 2),
    SOY_SAUCE("SoySauce", 1, 0, 3),
    MUSHROOMS("Mushrooms", 2, 0, 3),
    NOODLES("Noodles", 1, 0, 5);

    private final String name;
    private final int basePoints;
    private final int discardCardDraws;
    private final int quantity;
    private Synergy[] synergies;

    SF18Card(String name, int basePoints, int discardCardDraws, int quantity) {
        this.name = name;
        this.basePoints = basePoints;
        this.discardCardDraws = discardCardDraws;
        this.quantity = quantity;
    }

    static {
        SHRIMP.synergies = new Synergy[]{new Synergy(GINGER, 9), new Synergy(SOY_SAUCE, GINGER, 11)};
        PORK.synergies = new Synergy[]{new Synergy(MUSHROOMS, 8)};
        CHICKEN.synergies = new Synergy[]{new Synergy(GREEN_ONION, 5), new Synergy(GINGER, GREEN_ONION, 7)};
        GINGER.synergies = new Synergy[]{new Synergy(PORK, 4)};
        GREEN_ONION.synergies = new Synergy[]{new Synergy(SHRIMP, 5)};
        SOY_SAUCE.synergies = new Synergy[]{new Synergy(GREEN_ONION, 2), new Synergy(GINGER, 3)};
        MUSHROOMS.synergies = new Synergy[]{new Synergy(CHICKEN, 3)};
        NOODLES.synergies = new Synergy[]{};
    }

    public String getName() {
        return name;
    }

    public int getBasePoints() {
        return basePoints;
    }

    public Synergy[] getSynergies() {
        return synergies;
    }

    public int getDiscardCardDraws() {
        return discardCardDraws;
    }

    public int getQuantity() {
        return quantity;
    }

    public static class Synergy {
        private final SF18Card[] conditions;
        private final int points;

        public Synergy(SF18Card condition, int points) {
            this.conditions = new SF18Card[]{condition};
            this.points = points;
        }

        public Synergy(SF18Card condition1, SF18Card condition2, int points) {
            this.conditions = new SF18Card[]{condition1, condition2};
            this.points = points;
        }

        public SF18Card[] getConditions() {
            return conditions;
        }

        public int getPoints() {
            return points;
        }
    }
}
