package games.stirfry18.gui;

import core.AbstractGameState;
import core.AbstractPlayer;
import core.Game;
import games.stirfry18.SF18GameState;
import games.stirfry18.SF18Parameters;
import games.sushigo.SGGameState;
import games.sushigo.SGParameters;
import games.sushigo.gui.SGPlayerView;
import gui.AbstractGUIManager;
import gui.GamePanel;
import gui.IScreenHighlight;
import players.human.ActionController;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Set;

/**
 * <p>This class allows the visualisation of the game. The game components (accessible through {@link Game#getGameState()}
 * should be added into {@link JComponent} subclasses (e.g. {@link JLabel},
 * {@link JPanel}, {@link JScrollPane}; or custom subclasses such as those in {@link gui} package).
 * These JComponents should then be added to the <code>`parent`</code> object received in the class constructor.</p>
 *
 * <p>An appropriate layout should be set for the parent GamePanel as well, e.g. {@link BoxLayout} or
 * {@link BorderLayout} or {@link GridBagLayout}.</p>
 *
 * <p>Check the super class for methods that can be overwritten for a more custom look, or
 * {@link games.terraformingmars.gui.TMGUI} for an advanced game visualisation example.</p>
 *
 * <p>A simple implementation example can be found in {@link games.tictactoe.gui.TicTacToeGUIManager}.</p>
 */
public class SF18GUIManager extends AbstractGUIManager {
    // Settings for display areas
    final static int playerAreaWidth = 350;
    final static int playerAreaHeight = 100;
    final static int SGCardWidth = 60;
    final static int SGCardHeight = 85;
    // List of player hand views
    SF18PlayerView[] playerHands;
    // Currently active player
    int activePlayer = -1;
    // Border highlight of active player
    Border highlightActive = BorderFactory.createLineBorder(new Color(47, 132, 220), 3);
    Border highlightWin = BorderFactory.createLineBorder(new Color(31, 190, 58), 3);
    Border highlightLose = BorderFactory.createLineBorder(new Color(220, 73, 47), 3);
    Border[] playerViewBorders;
    Border[] playerViewWinBorders;
    Border[] playerViewLoseBorders;

    public SF18GUIManager(GamePanel parent, Game game, ActionController ac, Set<Integer> human) {
        super(parent, game, ac, human);
        if (game == null) return;

        // TODO: set up GUI components and add to `parent`
        if (game != null) {
            AbstractGameState gameState = game.getGameState();
            if (gameState != null) {
                //Initialise active player
                activePlayer = gameState.getCurrentPlayer();

                // Find required size of window. nxn grid of player areas
                int nPlayers = gameState.getNPlayers();
                int perColumn = (int)Math.ceil(Math.sqrt(nPlayers));
                int nVertAreas = (int)Math.ceil((double)nPlayers / perColumn);
                this.width = playerAreaWidth * perColumn;
                this.height = playerAreaHeight * nVertAreas + defaultInfoPanelHeight;
                if (!human.isEmpty()) {
                    this.height += defaultActionPanelHeight;
                }

                SF18GameState parsedGameState = (SF18GameState) gameState;
                SF18Parameters parameters = (SF18Parameters) gameState.getGameParameters();

                // Create main game area that will hold all game views
                playerHands = new SF18PlayerView[nPlayers];
                playerViewBorders = new Border[nPlayers];
                playerViewWinBorders = new Border[nPlayers];
                playerViewLoseBorders = new Border[nPlayers];
                JPanel mainGameArea = new JPanel();
                mainGameArea.setOpaque(false);
                mainGameArea.setLayout(new GridLayout(nVertAreas, perColumn));
                mainGameArea.setPreferredSize(new Dimension(playerAreaWidth * perColumn, playerAreaHeight * nVertAreas));

                // Player hands go on the edges
                for (int i = 0; i < nPlayers; i++) {
                    SF18PlayerView playerHand = new SF18PlayerView(parsedGameState.getPlayerHands().get(i), i, human, parameters.getDataPath());
                    playerHand.setOpaque(false);
                    playerHand.setPreferredSize(new Dimension(playerAreaWidth, playerAreaHeight));

                    // Get agent name
                    String[] split = game.getPlayers().get(i).getClass().toString().split("\\.");
                    String agentName = split[split.length - 1];

                    // Create border, layouts and keep track of this view
                    TitledBorder title = BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Player " + i + " [" + agentName + "]",
                            TitledBorder.CENTER, TitledBorder.ABOVE_TOP);
                    TitledBorder titleWin = BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Player " + i + " [" + agentName + "] - WIN",
                            TitledBorder.CENTER, TitledBorder.ABOVE_TOP);
                    TitledBorder titleLose = BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Player " + i + " [" + agentName + "] - LOSE",
                            TitledBorder.CENTER, TitledBorder.ABOVE_TOP);
                    playerViewBorders[i] = title;
                    playerViewWinBorders[i] = titleWin;
                    playerViewLoseBorders[i] = titleLose;
                    playerHand.setBorder(title);

                    mainGameArea.add(playerHand);
                    playerHands[i] = playerHand;
                }

                // Add all views to frame
                parent.setLayout(new BorderLayout());

                // Top area will show state information
                JPanel infoPanel = createGameStateInfoPanel("Stir Fry 18", gameState, width, defaultInfoPanelHeight);
                parent.add(infoPanel, BorderLayout.NORTH);

                // Bottom area will show actions available
                if (!human.isEmpty()) {
                    JComponent actionPanel = createActionPanel(new IScreenHighlight[0], width, defaultActionPanelHeight, false, false, null, null, null);
                    parent.add(actionPanel, BorderLayout.SOUTH);
                }

                // Center view will show game
                parent.add(mainGameArea, BorderLayout.CENTER);

                parent.setPreferredSize(new Dimension(width, height + defaultActionPanelHeight + defaultInfoPanelHeight + 20));
            }

        }
    }

    /**
     * Defines how many action button objects will be created and cached for usage if needed. Less is better, but
     * should not be smaller than the number of actions available to players in any game state.
     *
     * @return maximum size of the action space (maximum actions available to a player for any decision point in the game)
     */
    @Override
    public int getMaxActionSpace() {
        // TODO
        return 10;
    }

    /**
     * Updates all GUI elements given current game state and player that is currently acting.
     *
     * @param player    - current player acting.
     * @param gameState - current game state to be used in updating visuals.
     */
    @Override
    protected void _update(AbstractPlayer player, AbstractGameState gameState) {
        // TODO
    }
}
