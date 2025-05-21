package games.stirfry18.metrics;

import core.interfaces.IGameEvent;
import evaluation.listeners.MetricsGameListener;
import evaluation.metrics.AbstractMetric;
import evaluation.metrics.Event;
import evaluation.metrics.IMetricsCollection;
import evaluation.summarisers.TAGOccurrenceStatSummary;
import evaluation.summarisers.TAGStatSummary;
import games.stirfry18.SF18GameState;
import games.stirfry18.SF18Parameters;
import games.stirfry18.actions.Cook;
import games.stirfry18.actions.Discard;
import games.stirfry18.actions.DiscardIngredient;
import games.stirfry18.actions.DiscardProtein;
import games.stirfry18.components.IngredientCard;
import utilities.Pair;

import java.util.*;

public class SF18Metrics implements IMetricsCollection {

    /**
     * Record of all cooked sets along the game
     */
    public static class CookList extends AbstractMetric {

        @Override
        public Map<String, Class<?>> getColumns(int nPlayersPerGame, Set<String> playerNames) {
            Map<String, Class<?>> columns = new HashMap<>();
            columns.put("Cooked with -", String.class);
            return columns;
        }
        @Override
        protected boolean _run(MetricsGameListener listener, Event e, Map<String, Object> records) {

            SF18GameState gs = (SF18GameState) e.state;
            if(e.action instanceof Cook){
                Cook action = (Cook)e.action;
                List<String> cookRecipe =new ArrayList<>();
                String cookList="";
                for(int ingredient : action.ingredients){
                    IngredientCard card = (IngredientCard) gs.getComponentById(ingredient);
                    cookRecipe.add(card.getCardType().toString());

                }
                Collections.sort(cookRecipe);
                for (String card :cookRecipe){
                    cookList = cookList + card + "-";
                }
                records.put("Cooked with -", cookList);
                return true;
            }
            else {
                return false;
            }
//            Cook action = (Cook)e.action;
//            SGCard c = gs.getPlayerHands().get(action.playerId).get(action.cardIdx);
//            records.put("Card played", c.toString());


        }

        @Override
        public Set<IGameEvent> getDefaultEventTypes() {
            return Collections.singleton(Event.GameEvent.ACTION_CHOSEN);
        }

        // Count how many times a card is used on cooking
//        public Map<String, Object> postProcessingGameOver(Event e, TAGStatSummary recordedData) {
//            // Process the recorded data during the game and return game over summarised data
//            //TODO: still on sushigo
//            Map<String, Object> toRecord = new HashMap<>();
//            Map<String, Object> summaryData = recordedData.getSummary();
//            SF18Parameters params = (SF18Parameters) e.state.getGameParameters();
//            for (String k: summaryData.keySet()) {
//                String[] split = k.split("-");
//                SGCard.SGCardType type = SGCard.SGCardType.valueOf(split[0]);
//                int count = 1;
//                if (split.length > 1) count = Integer.parseInt(split[1]);
//                toRecord.put(getClass().getSimpleName() + "(" + k + "):" + e.type, ((TAGOccurrenceStatSummary)recordedData).getElements().get(k) * 1.0 / params.nCardsPerType.get(new Pair<>(type, count)));
//            }
//            return toRecord;
//        }

    }



    public static class DiscardedCards extends AbstractMetric {

        @Override
        public Map<String, Class<?>> getColumns(int nPlayersPerGame, Set<String> playerNames) {
            Map<String, Class<?>> columns = new HashMap<>();
            columns.put("Discard Reason", String.class);
            columns.put("Discarded Card", String.class);
            return columns;
        }

        @Override
        protected boolean _run(MetricsGameListener listener, Event e, Map<String, Object> records) {

            SF18GameState gs = (SF18GameState) e.state;
            if(e.action instanceof Discard){
                Discard action = (Discard)e.action;

                records.put("Discard Reason", "End of Turn");
                records.put("Discarded Card", gs.getComponentById(action.discardedCard).toString());

                return true;
            }
            else if(e.action instanceof DiscardProtein) {
                DiscardProtein action = (DiscardProtein)e.action;

                records.put("Discard Reason", "Protein Action");
                records.put("Discarded Card", gs.getComponentById(action.discardedCard).toString());

                return true;

            } else if(e.action instanceof DiscardIngredient) {
                DiscardIngredient action = (DiscardIngredient)e.action;

                records.put("Discard Reason", "Ingredient Action");
                records.put("Discarded Card", gs.getComponentById(action.discardedCards.get(0)).toString());
                return true;
            }

            return false;

        }

        @Override
        public Set<IGameEvent> getDefaultEventTypes() {
            return Collections.singleton(Event.GameEvent.ACTION_CHOSEN);
        }

    }


    public static class FavoredCards extends AbstractMetric {
        // Get cards that were kept in hand in preference of the discarded card
        @Override
        public Map<String, Class<?>> getColumns(int nPlayersPerGame, Set<String> playerNames) {
            Map<String, Class<?>> columns = new HashMap<>();
            columns.put("Discarded card",String.class);
            columns.put("Discard Reason", String.class);
            columns.put("Favored Cards", String.class);
            return columns;
        }

        @Override
        protected boolean _run(MetricsGameListener listener, Event e, Map<String, Object> records) {

            SF18GameState gs = (SF18GameState) e.state;
            if(e.action instanceof Discard){
                Discard action = (Discard)e.action;
                List<String> cardsKept =new ArrayList<>();
                String handList="";
                for(IngredientCard ingredient : gs.getPlayerHands().get(e.playerID)){
                    cardsKept.add(ingredient.getCardType().toString());

                }
                Collections.sort(cardsKept);
                for (String card :cardsKept){
                    handList = handList + card + "-";
                }
                records.put("Discarded card", gs.getComponentById(action.discardedCard).toString());
                records.put("Discard Reason", "End of Turn");
                records.put("Favored Cards", handList);
                return true;
            }else if(e.action instanceof DiscardIngredient){
                DiscardIngredient action = (DiscardIngredient)e.action;
                List<String> cardsKept =new ArrayList<>();
                String handList="";
                for(IngredientCard ingredient : gs.getPlayerHands().get(e.playerID)){
                    cardsKept.add(ingredient.getCardType().toString());

                }
                Collections.sort(cardsKept);
                for (String card :cardsKept){
                    handList = handList + card + "-";
                }
                records.put("Discarded card", gs.getComponentById(action.discardedCards.get(0)).toString());
                records.put("Discard Reason", "Ingredient Action");
                records.put("Favored Cards", handList);
                return true;
            }
            else if(e.action instanceof DiscardProtein){
                DiscardProtein action = (DiscardProtein)e.action;
                List<String> cardsKept =new ArrayList<>();
                String handList="";
                for(IngredientCard ingredient : gs.getPlayerHands().get(e.playerID)){
                    cardsKept.add(ingredient.getCardType().toString());

                }
                Collections.sort(cardsKept);
                for (String card :cardsKept){
                    handList = handList + card + "-";
                }
                records.put("Discarded card", gs.getComponentById(action.discardedCard).toString());
                records.put("Discard Reason", "Protein Action");
                records.put("Favored Cards", handList);
                return true;
            }
            else {
                return false;
            }

        }
        @Override
        public Set<IGameEvent> getDefaultEventTypes() {
            return Collections.singleton(Event.GameEvent.ACTION_TAKEN);
        }

    }
}
