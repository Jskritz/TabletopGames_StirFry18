package games.stirfry18.actions;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.stirfry18.SF18GameState;
import games.stirfry18.components.IngredientCard;
import games.stirfry18.components.SF18Card;

import java.util.Set;


public class Cook extends AbstractAction {
    // cook and score. uses 3 to 5 ingredients and need at least 1 noodle, cannot use more than one of the same ingredient

    public Set<Integer> ingredients;

    public Cook(Set<Integer> ingredients) {
        this.ingredients = ingredients;
    }
    /**
     * Executes this action, applying its effect to the given game state. Can access any component IDs stored
     * through the {@link AbstractGameState#getComponentById(int)} method.
     * @param gs - game state which should be modified by this action.
     * @return - true if successfully executed, false otherwise.
     */
    @Override
    public boolean execute(AbstractGameState gs) {

        SF18GameState gamestate = (SF18GameState) gs;
        gamestate.getActionsChosen().add(PossibleActions.Cook);
        Integer points =0;
        for(Integer index : ingredients) {
            IngredientCard ingredient = (IngredientCard) gamestate.getComponentById(index);
            if (ingredient.getCardType().getSynergies().length ==0){
                points += ingredient.getCardType().getBasePoints();
            }
            else {
                int possible = ingredient.getCardType().getBasePoints();
                for(SF18Card.Synergy synergy : ingredient.getCardType().getSynergies()){
                    boolean haveSynergy = true;
                    for(SF18Card condition : synergy.getConditions()){
                        if(gamestate.getPlayerHands().get(gamestate.getCurrentPlayer()).stream().anyMatch(x-> x.getCardType() != condition)){
                            haveSynergy=false;
                        }
                    }
                    if(haveSynergy){
                        possible = Math.max(possible,synergy.getPoints());
                    }
                }
                points+=possible;
            }
            gamestate.getDiscard().add(ingredient);
            gamestate.getPlayerHands().get(gamestate.getCurrentPlayer()).remove(ingredient);
        }
        gamestate.addPlayerScore(gamestate.getCurrentPlayer(),points);

        return true;
    }

    /**
     * @return Make sure to return an exact <b>deep</b> copy of the object, including all of its variables.
     * Make sure the return type is this class (e.g. GTAction) and NOT the super class AbstractAction.
     * <p>If all variables in this class are final or effectively final (which they should be),
     * then you can just return <code>`this`</code>.</p>
     */
    @Override
    public Cook copy() {

        return new Cook(ingredients);
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Cook && ((Cook) obj).ingredients.equals(this.ingredients);
    }

    @Override
    public int hashCode() {

        return ingredients.hashCode();
    }

    @Override
    public String toString() {

        String action = "Cook with ids: ";
        for(Integer index : ingredients){
            action+= index.toString() + ", ";
        }
        return action;
    }

    /**
     * @param gameState - game state provided for context.
     * @return A more descriptive alternative to the toString action, after access to the game state to e.g.
     * retrieve components for which only the ID is stored on the action object, and include the name of those components.
     * Optional.
     */
    @Override
    public String getString(AbstractGameState gameState) {
        SF18GameState gs = (SF18GameState) gameState;
        String action = "Cook with: ";
        for (Integer index:ingredients){
            IngredientCard card = (IngredientCard) gs.getComponentById(index);
            action+= card.cardType.toString() + ",";
        }
        return action ;
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
