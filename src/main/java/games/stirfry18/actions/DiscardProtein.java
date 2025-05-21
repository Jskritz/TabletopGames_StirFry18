package games.stirfry18.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.stirfry18.SF18GameState;
import games.stirfry18.components.IngredientCard;
import games.sushigo.SGGameState;


public class DiscardProtein extends AbstractAction {
    // Discards one protein card to draw cards equal to its value

    public final Integer discardedCard;

    public DiscardProtein (Integer discardedCard){
        this.discardedCard = discardedCard;
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
        gameState.getActionsChosen().add(PossibleActions.DiscardProtein);
        IngredientCard discCard = (IngredientCard) gameState.getComponentById(discardedCard);
        gameState.getDiscard().add(discCard);
        gameState.getPlayerHands().get(gameState.getCurrentPlayer()).remove(discCard);

        for(int i = 0; i<discCard.getCardType().getDiscardCardDraws(); i++){
            IngredientCard draw = gameState.getMainDeck().draw();
            if (draw==null){
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
    public DiscardProtein copy() {
        return new DiscardProtein(discardedCard);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DiscardProtein && ((DiscardProtein) obj).discardedCard.equals(discardedCard);
    }

    @Override
    public int hashCode() {
        return discardedCard.hashCode();
    }

    @Override
    public String toString() {
        // TODO: Replace with appropriate string, including any action parameters
        String action = "Discard Protein: " + discardedCard.toString();

        return action ;
    }

    IngredientCard getCard(AbstractGameState gameState){
        return (IngredientCard) gameState.getComponentById(discardedCard);
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
        action += getCard(gameState).cardType.toString();

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
