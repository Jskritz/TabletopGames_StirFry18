package games.stirfry18.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.components.Component;
import games.stirfry18.SF18GameState;
import games.stirfry18.components.IngredientCard;


import java.util.ArrayList;
import java.util.List;

public class DiscardIngredient extends AbstractAction {
    // Discards two identical card to draw 3 cards

    public final List<Integer> discardedCards;


    public DiscardIngredient(Integer firstID, Integer secondID){
        discardedCards = new ArrayList<Integer>();
        discardedCards.add(firstID);

        discardedCards.add(secondID);

    }
    /**
     * Executes this action, applying its effect to the given game state. Can access any component IDs stored
     * through the {@link AbstractGameState#getComponentById(int)} method.
     * @param gs - game state which should be modified by this action.
     * @return - true if successfully executed, false otherwise.
     */
    @Override
    public boolean execute(AbstractGameState gs) {
        SF18GameState gameState = (SF18GameState) gs;
        gameState.getActionsChosen().add(PossibleActions.DiscardIngredients);
        gameState.getDiscard().add((IngredientCard) gameState.getComponentById(discardedCards.get(0)));
        gameState.getPlayerHands().get(gameState.getCurrentPlayer()).remove((IngredientCard) gameState.getComponentById(discardedCards.get(0)));
        gameState.getDiscard().add((IngredientCard) gameState.getComponentById(discardedCards.get(1)));
        gameState.getPlayerHands().get(gameState.getCurrentPlayer()).remove((IngredientCard) gameState.getComponentById(discardedCards.get(1)));
        for(int i=0; i<3; i++){
            IngredientCard draw = gameState.getMainDeck().draw();
            if(draw == null){
                return true;
            }
            gameState.getPlayerHands().get(gameState.getCurrentPlayer()).add(draw);
        }
        return true;
    }

    /**
     * @return Make sure to return an exact <b>deep</b> copy of the object, including all of its variables.
     * Make sure the return type is this class (e.g. GTAction) and NOT the super class AbstractAction.
     * <p>If all variables in this class are final or effectively final (which they should be),
     * then you can just return <code>`this`</code>.</p>
     */
    @Override
    public DiscardIngredient copy() {

        return new DiscardIngredient(discardedCards.get(0), discardedCards.get(1));
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof DiscardIngredient && ((DiscardIngredient) obj).discardedCards.equals(discardedCards);
    }

    @Override
    public int hashCode() {

        return discardedCards.hashCode();
    }

    @Override
    public String toString() {
        String action = "Discard Ingretients: ";
        action += discardedCards.get(0).toString() + " and ";
        action += discardedCards.get(1).toString();

        return action ;
    }

    IngredientCard getCard(AbstractGameState gameState, Integer index){
        return (IngredientCard) gameState.getComponentById(discardedCards.get(index));
    }

    /**
     * @param gameState - game state provided for context.
     * @return A more descriptive alternative to the toString action, after access to the game state to e.g.
     * retrieve components for which only the ID is stored on the action object, and include the name of those components.
     * Optional.
     */
    @Override
    public String getString(AbstractGameState gameState) {
        String action = "Discard: ";
        action += getCard(gameState,0).cardType.toString() + "and ";
        action += getCard(gameState,1).cardType.toString();

        return action;
    }


    /**
     * This next one is optional.
     *
     *  May optionally be implemented if Actions are not fully visible
     *  The only impact this has is in the GUI, to avoid this giving too much information to the human player.
     *
     *  An example is in Resistance or Sushi Go, in which all cards are technically revealed simultaneously,
     *  but the game engine asks for the moves sequentially. In this case, the action should be able to
     *  output something like "Player N plays card", without saying what the card is.
     * @param gameState - game state to be used to generate the string.
     * @param playerId - player to whom the action should be represented.
     * @return
     */
   // @Override
   // public String getString(AbstractGameState gameState, int playerId);
}
