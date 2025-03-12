package games.stirfry18;

import core.AbstractGameState;
import core.AbstractParameters;
import evaluation.optimisation.TunableParameters;
import games.sushigo.SGParameters;

import java.util.Objects;

/**
 * <p>This class should hold a series of variables representing game parameters (e.g. number of cards dealt to players,
 * maximum number of rounds in the game etc.). These parameters should be used everywhere in the code instead of
 * local variables or hard-coded numbers, by accessing these parameters from the game state via {@link AbstractGameState#getGameParameters()}.</p>
 *
 * <p>It should then implement appropriate {@link #_copy()}, {@link #_equals(Object)} and {@link #hashCode()} functions.</p>
 *
 * <p>The class can optionally extend from {@link TunableParameters} instead, which allows to use
 * automatic game parameter optimisation tools in the framework.</p>
 */
public class SF18Parameters extends AbstractParameters {
    public String dataPath = "data/stirfry18/";

    public String getDataPath() { return dataPath; }

    @Override
    protected AbstractParameters _copy() {
        SF18Parameters param = new SF18Parameters();
        param.dataPath = this.dataPath;
        // TODO: deep copy of all variables.
        return param;
    }

    @Override
    protected boolean _equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SF18Parameters)) return false;
        if (!super.equals(o)) return false;
        SF18Parameters that = (SF18Parameters) o;
        return this.dataPath == that.dataPath;

        // TODO: compare all variables.

    }

    @Override
    public int hashCode() {
        // TODO: include the hashcode of all variables.
        return Objects.hash(super.hashCode(),dataPath);
    }
}
