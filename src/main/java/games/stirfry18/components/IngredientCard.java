package games.stirfry18.components;

import core.CoreConstants;
import core.components.Component;
import core.components.Card;


/**
 * <p>Components represent a game piece, or encompass some unit of game information (e.g. cards, tokens, score counters, boards, dice etc.)</p>
 * <p>Components in the game can (and should, if applicable) extend one of the other components, in package {@link core.components}.
 * Or, the game may simply reuse one of the existing core components.</p>
 * <p>They need to extend at a minimum the {@link Component} super class and implement the {@link Component#copy()} method.</p>
 * <p>They also need to include {@link Object#equals(Object)} and {@link Object#hashCode()} methods.</p>
 * <p>They <b>may</b> keep references to other components or actions (but these should be deep-copied in the copy() method, watch out for infinite loops!).</p>
 */
public class IngredientCard extends Card {

    public final CardType cardType;

    public IngredientCard(CardType cardType) {
        super(cardType.toString());
        this.cardType = cardType;
    }

    public IngredientCard(CardType cardType, int componentID) {
        super(cardType.toString(), componentID);
        this.cardType = cardType;
    }

    public CardType getCardType() {
        return cardType;
    }



    public String toString(){
        return cardType.toString();
    }

    @Override
    public IngredientCard copy() {
        return new IngredientCard(cardType, componentID);
    }

    @Override
    public boolean equals(Object o) {
        // TODO: compare all class variables (if any).
        return (o instanceof IngredientCard) && super.equals(o);
    }

    @Override
    public int hashCode() {
        // TODO: include all class variables (if any).
        return super.hashCode();
    }
}
