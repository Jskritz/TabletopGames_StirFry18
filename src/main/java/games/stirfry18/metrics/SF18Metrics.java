package games.stirfry18.metrics;

import core.interfaces.IGameEvent;
import evaluation.listeners.MetricsGameListener;
import evaluation.metrics.AbstractMetric;
import evaluation.metrics.Event;
import evaluation.metrics.IMetricsCollection;
import evaluation.summarisers.TAGOccurrenceStatSummary;
import evaluation.summarisers.TAGStatSummary;
import games.stirfry18.actions.Cook;
import games.sushigo.SGGameState;
import games.sushigo.SGParameters;
import games.sushigo.actions.ChooseCard;
import games.sushigo.cards.SGCard;
import utilities.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SF18Metrics implements IMetricsCollection {

    public static class CookList extends AbstractMetric {

        @Override
        public Map<String, Class<?>> getColumns(int nPlayersPerGame, Set<String> playerNames) {
            Map<String, Class<?>> columns = new HashMap<>();
            columns.put("Cooked with", String.class);
            return columns;
        }

        @Override
        protected boolean _run(MetricsGameListener listener, Event e, Map<String, Object> records) {

            SGGameState gs = (SGGameState) e.state;
            if(e.action instanceof Cook){
                Cook action = (Cook)e.action;
                String cookList="";
                for(int ingredient : action.ingredients){
                    cookList = cookList + gs.getComponentById(ingredient).toString() + " ";
                }

                records.put("Cooked with ", cookList);
            }
//            Cook action = (Cook)e.action;
//            SGCard c = gs.getPlayerHands().get(action.playerId).get(action.cardIdx);
//            records.put("Card played", c.toString());

            return true;
        }

        @Override
        public Set<IGameEvent> getDefaultEventTypes() {
            return Collections.singleton(Event.GameEvent.ACTION_CHOSEN);
        }

    }
}
