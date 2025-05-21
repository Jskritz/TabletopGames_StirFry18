package games.stirfry18;

import core.AbstractGameState;
import core.AbstractParameters;
import core.CoreConstants;
import core.components.Component;
import core.components.Counter;
import core.components.Deck;
import core.interfaces.IGamePhase;
import games.GameType;
import games.stirfry18.actions.PossibleActions;
import games.stirfry18.components.IngredientCard;
import games.stirfry18.components.SF18Card;
import players.heuristics.OrdinalPosition;
import scala.Int;
import utilities.DeterminisationUtilities;

import java.util.*;

/**
 * <p>The game state encapsulates all game information. It is a data-only class, with game functionality present
 * in the Forward Model or actions modifying the state of the game.</p>
 * <p>Most variables held here should be {@link Component} subclasses as much as possible.</p>
 * <p>No initialisation or game logic should be included here (not in the constructor either). This is all handled externally.</p>
 * <p>Computation may be included in functions here for ease of access, but only if this is querying the game state information.
 * Functions on the game state should never <b>change</b> the state of the game.</p>
 */
public class SF18GameState extends AbstractGameState {

    List<Deck<IngredientCard>> playerHands;
    Deck<IngredientCard> mainDeck;
    Deck<IngredientCard> discard;
    Counter[] playerScores;
    List<PossibleActions> actionsChosen;

    public enum SF18GamePhases implements IGamePhase{
        ActionPhase, DiscardPhase
    }
    /**
     * @param gameParameters - game parameters.
     * @param nPlayers       - number of players in the game
     */
    public SF18GameState(AbstractParameters gameParameters, int nPlayers) {
        super(gameParameters, nPlayers);
    }

    /**
     * @return the enum value corresponding to this game, declared in {@link GameType}.
     */
    @Override
    protected GameType _getGameType() {

        return GameType.StirFry18;
    }

    public List<Deck<IngredientCard>> getPlayerHands() {
        return playerHands;
    }
    public List<SF18Card> getCardTypeInHand(int playerID){
        List<SF18Card> cardsInHand = new ArrayList<>();
        Deck<IngredientCard> deck = playerHands.get(playerID);
        for(IngredientCard card : deck){
            cardsInHand.add(card.getCardType());
        }

        return cardsInHand;
    }

    public Deck<IngredientCard> getMainDeck() {
        return mainDeck;
    }

    public Deck<IngredientCard> getDiscard() {
        return discard;
    }

    public Counter[] getPlayerScores() {
        return playerScores;
    }
    public List<PossibleActions> getActionsChosen(){return  actionsChosen;}

    public void addPlayerScore(Integer playerID, Integer amount){
        playerScores[playerID].increment(amount);
    }

    /**
     * Returns all Components used in the game and referred to by componentId from actions or rules.
     * This method is called after initialising the game state, so all components will be initialised already.
     *
     * @return - List of Components in the game.
     */
    @Override
    protected List<Component> _getAllComponents() {
        List<Component> components = new ArrayList<>();
        for (int i =0; i<nPlayers;i++) {
            components.add(playerHands.get(i));
            components.add(playerScores[i]);
        }
        components.add(mainDeck);
        components.add(discard);
        return components;
    }

    /**
     * <p>Create a deep copy of the game state containing only those components the given player can observe.</p>
     * <p>If the playerID is NOT -1 and If any components are not visible to the given player (e.g. cards in the hands
     * of other players or a face-down deck), then these components should instead be randomized (in the previous examples,
     * the cards in other players' hands would be combined with the face-down deck, shuffled together, and then new cards drawn
     * for the other players). This process is also called 'redeterminisation'.</p>
     * <p>There are some utilities to assist with this in utilities.DeterminisationUtilities. One firm is guideline is
     * that the standard random number generator from getRnd() should not be used in this method. A separate Random is provided
     * for this purpose - redeterminisationRnd.
     *  This is to avoid this RNG stream being distorted by the number of player actions taken (where those actions are not themselves inherently random)</p>
     * <p>If the playerID passed is -1, then full observability is assumed and the state should be faithfully deep-copied.</p>
     *
     * <p>Make sure the return type matches the class type, and is not AbstractGameState.</p>
     *
     *
     * @param playerId - player observing this game state.
     */
    @Override
    protected SF18GameState _copy(int playerId) {
        // TODO: shuffle hidden information
        SF18GameState copy = new SF18GameState(gameParameters, getNPlayers());
        copy.playerScores = new Counter[nPlayers];
        for (int i =0 ; i<nPlayers;i++){
            copy.playerScores[i] = playerScores[i].copy();
        }
        // copy hands
        copy.playerHands = new ArrayList<>();
        for(Deck<IngredientCard> d : playerHands){
            copy.playerHands.add(d.copy());
        }
        // copy main deck
        copy.mainDeck = mainDeck.copy();

        List<Deck<IngredientCard>> toReshuffle = new ArrayList<>();
        toReshuffle.add(copy.mainDeck);
        toReshuffle.addAll(copy.playerHands);
//        for (int i =0 ; i<copy.getPlayerHands().size();i++){
//           if(i!=playerId){
//               toReshuffle.add(copy.playerHands.get(i));
//           }
//        }
        if(playerId!=-1){
            DeterminisationUtilities.reshuffle(playerId,
                    toReshuffle,
                    i->true,
                    redeterminisationRnd
            );
        }

//
//        // put player hand cards into deck
//        for ( int i = 0; i< copy.playerHands.size();i++){
//            if(i != playerId) {
//                copy.mainDeck.add(copy.playerHands.get(i));
//            }
//        }
//        //shuffle deck
//        copy.mainDeck.shuffle(redeterminisationRnd);
//
//        for ( int i = 0; i< copy.playerHands.size();i++){
//            if(i != playerId){
//                Deck<IngredientCard> hand = copy.playerHands.get(i);
//                int nCardsInHand = hand.getSize();
//                hand.clear();
//                for(int j=0; j<nCardsInHand;j++){
//                    hand.add(copy.mainDeck.draw());
//                }
//            }
//        }
//
        copy.discard = discard.copy(); // TODO: with bluffing this should be shuffled

        copy.actionsChosen = new ArrayList<>();
        copy.actionsChosen.addAll(actionsChosen);

        return copy;
    }

    /**
     * @param playerId - player observing the state.
     * @return a score for the given player approximating how well they are doing (e.g. how close they are to winning
     * the game); a value between 0 and 1 is preferred, where 0 means the game was lost, and 1 means the game was won.
     */
    @Override
    protected double _getHeuristicScore(int playerId) {
        if (isNotTerminal()) {
            OrdinalPosition heuristic = new OrdinalPosition();

            //ScoreHeuristic heuristic = new ScoreHeuristic();

            //LeaderHeuristic heuristic = new LeaderHeuristic();

            return heuristic.evaluateState(this,playerId);

        } else {
            // The game finished, we can instead return the actual result of the game for the given player.
            return getPlayerResults()[playerId].value;
        }
    }

    /**
     * @param playerId - player observing the state.
     * @return the true score for the player, according to the game rules. May be 0 if there is no score in the game.
     */
    @Override
    public double getGameScore(int playerId) {
        // TODO: What is this player's score (if any)?

        return playerScores[playerId].getValue();
    }

    @Override
    protected boolean _equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SF18GameState)) return false;
        if (!super.equals(o)) return false;
        SF18GameState that = (SF18GameState) o;
        return Objects.equals(playerHands,that.playerHands) && Arrays.equals(playerScores, that.playerScores) &&
                Objects.equals(mainDeck , that.mainDeck) && Objects.equals(discard , that.discard) && Objects.equals(actionsChosen , that.actionsChosen);
//        return playerHands == that.playerHands && playerScores == that.playerScores &&
//                mainDeck == that.mainDeck && discard == that.discard && actionsChosen == that.actionsChosen;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(playerHands,mainDeck,discard,actionsChosen);
        result = 76*result + Arrays.hashCode(playerScores);
        return result;
    }

    public String toString() {
        return  playerHands.hashCode() + "|" +
                mainDeck.hashCode() + "|" +
                discard.hashCode() + "|" +
                actionsChosen.hashCode() + "|" +
                Arrays.hashCode(playerScores) + "|" +
                super.hashCode() + "|";
    }

    // TODO: Consider the methods below for possible implementation
    // TODO: These all have default implementations in AbstractGameState, so are not required to be implemented here.
    // TODO: If the game has 'teams' that win/lose together, then implement the next two nethods.
    /**
     * Returns the number of teams in the game. The default is to have one team per player.
     * If the game does not have 'teams' that win/lose together, then ignore these two methods.
     */
   // public int getNTeams();
    /**
     * Returns the team number the specified player is on.
     */
    //public int getTeam(int player);

    // TODO: If your game has multiple special tiebreak options, then implement the next two methods.
    // TODO: The default is to tie-break on the game score (if this is the case, ignore these)
    // public double getTiebreak(int playerId, int tier);
    // public int getTiebreakLevels();


    // TODO: If your game does not have a score of any type, and is an 'insta-win' type game which ends
    // TODO: as soon as a player achieves a winning condition, and has some bespoke method for determining 1st, 2nd, 3rd etc.
    // TODO: Then you *may* want to implement:.
    //public int getOrdinalPosition(int playerId);

    // TODO: Review the methods below...these are all supported by the default implementation in AbstractGameState
    // TODO: So you do not (and generally should not) implement your own versions - take advantage of the framework!
    // public Random getRnd() returns a Random number generator for the game. This will be derived from the seed
    // in game parameters, and will be updated correctly on a reset

    // Ths following provide access to the id of the current player; the first player in the Round (if that is relevant to a game)
    // and the current Turn and Round numbers.
    // public int getCurrentPlayer()
    // public int getFirstPlayer()
    // public int getRoundCounter()
    // public int getTurnCounter()
    // also make sure you check out the standard endPlayerTurn() and endRound() methods in StandardForwardModel

    // This method can be used to log a game event (e.g. for something game-specific that you want to include in the metrics)
    // public void logEvent(IGameEvent...)
}
