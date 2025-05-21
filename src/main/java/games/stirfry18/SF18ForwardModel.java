package games.stirfry18;

import core.AbstractGameState;
import core.CoreConstants;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.actions.DoNothing;
import core.components.Counter;
import core.components.Deck;
import games.stirfry18.actions.*;
import games.stirfry18.components.SF18Card;
import games.stirfry18.components.IngredientCard;


import java.util.*;

/**
 * <p>The forward model contains all the game rules and logic. It is mainly responsible for declaring rules for:</p>
 * <ol>
 *     <li>Game setup</li>
 *     <li>Actions available to players in a given game state</li>
 *     <li>Game events or rules applied after a player's action</li>
 *     <li>Game end</li>
 * </ol>
 */
public class SF18ForwardModel extends StandardForwardModel {

    /**
     * Initializes all variables in the given game state. Performs initial game setup according to game rules, e.g.:
     * <ul>
     *     <li>Sets up decks of cards and shuffles them</li>
     *     <li>Gives player cards</li>
     *     <li>Places tokens on boards</li>
     *     <li>...</li>
     * </ul>
     *
     * @param firstState - the state to be modified to the initial game state.
     */
    @Override
    protected void _setup(AbstractGameState firstState) {
        SF18GameState gs = (SF18GameState) firstState;
        gs.playerScores = new Counter[gs.getNPlayers()];
        gs.getGameParameters().setMaxRounds(500); //parameter


        gs.actionsChosen = new ArrayList<>();

        // Setup draw & discard piles
        gs.mainDeck = new Deck<>("Draw pile", CoreConstants.VisibilityMode.HIDDEN_TO_ALL);
        gs.discard = new Deck<>("Discard pile", CoreConstants.VisibilityMode.HIDDEN_TO_ALL);

        // add cards to main deck
        for(SF18Card type : SF18Card.values()){
            for(int i=0; i<type.getQuantity(); i++){
                gs.mainDeck.add(new IngredientCard(type));
            }
        }
        gs.mainDeck.shuffle(new Random());

        // deal player hands
        gs.playerHands = new ArrayList<>();
        for( int i=0; i< gs.getNPlayers();i++){
            gs.playerHands.add(new Deck<IngredientCard>("Player " + i + " hand", i , CoreConstants.VisibilityMode.VISIBLE_TO_OWNER));
            for( int j=0; j<3; j++){
                gs.playerHands.get(i).add(gs.mainDeck.draw());
            }
            gs.playerScores[i] = new Counter(0, 0, Integer.MAX_VALUE, "Player " + i + " score");
        }
        // Set starting player
        gs.setFirstPlayer(0);
        // Start with ACtion Phase
        gs.setGamePhase(SF18GameState.SF18GamePhases.ActionPhase);
    }

    /**
     * Calculates the list of currently available actions, possibly depending on the game phase.
     * @return - List of AbstractAction objects.
     */
    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        SF18GameState gs = (SF18GameState) gameState;
        List<AbstractAction> actions = new ArrayList<>();
        List<SF18Card> discardIngredientAdded = new ArrayList<>();
        if(gs.getGamePhase()==SF18GameState.SF18GamePhases.ActionPhase){
            for(IngredientCard card:gs.getPlayerHands().get(gs.getCurrentPlayer())){
                switch(card.getCardType()){
                    case CHICKEN,SHRIMP, PORK:
                        if(!gs.actionsChosen.contains(PossibleActions.DiscardProtein)){
                            actions.add(new DiscardProtein(card.getComponentID()));
                        }
                        break;
                    default:
                        if(!gs.actionsChosen.contains(PossibleActions.DiscardIngredients)){
                            for(IngredientCard other:gs.getPlayerHands().get(gs.getCurrentPlayer())){
                                if(other.equals(card)){
                                    continue;
                                }
                                if(card.getCardType() == other.getCardType() && !discardIngredientAdded.contains(card.getCardType())){
                                    discardIngredientAdded.add(card.getCardType());
                                    actions.add(new DiscardIngredient(card.getComponentID(),other.getComponentID()));
                                }
                            }
                        }

                }
            }

            //TODO: Check if this madness work @>@ creates options, still need to see if it correctly creates all options

            if(!gs.actionsChosen.contains(PossibleActions.Cook)){
                if(gs.getPlayerHands().get(gs.getCurrentPlayer()).stream().anyMatch(x -> x.getCardType() == SF18Card.NOODLES)){ // need noodles to cook
                    Deck<IngredientCard> hand = gs.getPlayerHands().get(gs.getCurrentPlayer());
                    List<IngredientCard> cards = hand.getComponents();
                    Set<SF18Card> filteredHand = new HashSet<>(gs.getCardTypeInHand(gs.getCurrentPlayer()).stream().toList()); //cannot use replicated cards
                    if(filteredHand.size()>=3){ // need at least 3 cards
                        if(filteredHand.size()<=5){ // can use a maximum of 5 cards
                            addCookRecipe(actions, cards, filteredHand);

                        }
                        else{// with more than 5 we need to select 5
                            Set<Set<SF18Card>> possibleRecipies = getIngretientsSubsets(filteredHand);
                            for(Set<SF18Card> possible:possibleRecipies){
                                if(possible.size()==5 && possible.stream().anyMatch(x->x == SF18Card.NOODLES)){
                                    addCookRecipe(actions, cards, possible);
                                }
                            }
                        }
                    }
                }
            }
            actions.add(new Pass());

        } else if (gs.getGamePhase()==SF18GameState.SF18GamePhases.DiscardPhase) {
            if(gs.getPlayerHands().get(gameState.getCurrentPlayer()).getSize()>3){
                for(IngredientCard card:gs.getPlayerHands().get(gs.getCurrentPlayer())){
                    actions.add(new Discard(card.getComponentID()));
                }
            }
            else {
                actions.add(new Pass());
            }
        }
        return actions;
    }

    private void addCookRecipe(List<AbstractAction> actions, List<IngredientCard> cards, Set<SF18Card> filteredHand) {
        Set<Integer> cardIDs = new HashSet<>();
        for (SF18Card cardType: filteredHand){
            IngredientCard card = cards.stream().filter(x -> x.getCardType() == cardType).findFirst().get();
            cardIDs.add(card.getComponentID());
        }
        Cook cookAction = new Cook(cardIDs);
        if(actions.contains(cookAction)){
            return;
        }
        actions.add(new Cook(cardIDs));
    }

    public Set<Set<SF18Card>> getIngretientsSubsets(Set<SF18Card> set) {
        if (set.isEmpty()) {
            return Collections.singleton(Collections.emptySet());
        }

        Set<Set<SF18Card>> subSets = set.stream().map(item -> {
                    Set<SF18Card> clone = new HashSet<>(set);
                    clone.remove(item);
                    return clone;
                }).map(this::getIngretientsSubsets)
                .reduce(new HashSet<>(), (x, y) -> {
                    x.addAll(y);
                    return x;
                });

        subSets.add(set);
        return subSets;
    }

    @Override
    protected void _afterAction(AbstractGameState currentState, AbstractAction actionTaken) {
        //super._afterAction(currentState, actionTaken);
        if(!(actionTaken instanceof DoNothing)){

            return;
        }
        SF18GameState gs = (SF18GameState) currentState;
        gs.actionsChosen.clear();

        // add game phases to make players discard down to 3
        if(gs.getGamePhase()==SF18GameState.SF18GamePhases.ActionPhase){
            gs.setGamePhase(SF18GameState.SF18GamePhases.DiscardPhase);
            return;
        }

        // End player turn
        if (gs.getGameStatus() == CoreConstants.GameResult.GAME_ONGOING) {
            gs.setGamePhase(SF18GameState.SF18GamePhases.ActionPhase);
            gs.mainDeck.add(gs.discard);
            gs.mainDeck.shuffle(new Random(gs.getGameParameters().getRandomSeed()));
            gs.discard.clear();
            gs.getPlayerHands().get((gs.getCurrentPlayer()+1)% gs.getNPlayers()).add(gs.mainDeck.draw());
            endPlayerTurn(gs, (gs.getCurrentPlayer()+1)% gs.getNPlayers());
        }

        for (int i =0 ; i<gs.getNPlayers();i++){
            if(gs.getGameScore(i)>=50){
                endGame(gs);
            }
        }
    }
}
