package games.stirfry18.components;

import core.components.Component;
import core.components.Card;

import java.util.Objects;


/**
 * <p>Components represent a game piece, or encompass some unit of game information (e.g. cards, tokens, score counters, boards, dice etc.)</p>
 * <p>Components in the game can (and should, if applicable) extend one of the other components, in package {@link core.components}.
 * Or, the game may simply reuse one of the existing core components.</p>
 * <p>They need to extend at a minimum the {@link Component} super class and implement the {@link Component#copy()} method.</p>
 * <p>They also need to include {@link Object#equals(Object)} and {@link Object#hashCode()} methods.</p>
 * <p>They <b>may</b> keep references to other components or actions (but these should be deep-copied in the copy() method, watch out for infinite loops!).</p>
 */
public class IngredientCard extends Card {

    public final SF18Card cardType;

    public IngredientCard(SF18Card cardType) {
        super(cardType.toString());
        this.cardType = cardType;
    }

    public IngredientCard(SF18Card cardType, int componentID) {
        super(cardType.toString(), componentID);
        this.cardType = cardType;
    }

    public SF18Card getCardType() {
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

        return (o instanceof IngredientCard) && super.equals(o) && ((IngredientCard) o).cardType.equals(cardType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), cardType);
    }
}
