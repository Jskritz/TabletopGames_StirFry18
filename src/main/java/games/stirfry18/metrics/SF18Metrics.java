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
import games.stirfry18.components.IngredientCard;
import utilities.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SF18Metrics implements IMetricsCollection {

    /**
     * Record of all cooked sets along the game
     */
    public static class CookList extends AbstractMetric {

        @Override
        public Map<String, Class<?>> getColumns(int nPlayersPerGame, Set<String> playerNames) {
            Map<String, Class<?>> columns = new HashMap<>();
            columns.put("Cooked ingredients", String.class);
            return columns;
        }

        @Override
        protected boolean _run(MetricsGameListener listener, Event e, Map<String, Object> records) {

            SF18GameState gs = (SF18GameState) e.state;
            if(e.action instanceof Cook){
                Cook action = (Cook)e.action;
                String cookList="";
                for(int ingredient : action.ingredients){
                    cookList = cookList + ((IngredientCard) gs.getComponentById(ingredient)).getCardType().toString() + "-";
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

    public static class CookFrequency extends AbstractMetric {

        @Override
        public Map<String, Class<?>> getColumns(int nPlayersPerGame, Set<String> playerNames) {
            Map<String, Class<?>> columns = new HashMap<>();
            columns.put("Cooked ingredients", String.class);
            return columns;
        }

        @Override
        protected boolean _run(MetricsGameListener listener, Event e, Map<String, Object> records) {

            SGGameState gs = (SGGameState) e.state;
            if(e.action instanceof Cook){
                Cook action = (Cook)e.action;
                String cookList="";
                for(int ingredient : action.ingredients){
                    cookList = cookList + gs.getComponentById(ingredient).toString() + "-";
                }

                records.put("Cooked with ", cookList);
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

    }
}
