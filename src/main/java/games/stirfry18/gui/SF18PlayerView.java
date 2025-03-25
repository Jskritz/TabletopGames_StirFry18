package games.stirfry18.gui;

import core.components.Deck;
import games.stirfry18.SF18GameState;
import games.stirfry18.components.IngredientCard;
import games.sushigo.SGGameState;
import games.sushigo.cards.SGCard;
import games.sushigo.gui.SGDeckView;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

import static games.stirfry18.gui.SF18GUIManager.playerAreaHeight;
import static games.stirfry18.gui.SF18GUIManager.playerAreaWidth;


public class SF18PlayerView extends JComponent {

    // ID of player showing
    int playerId;
    // Number of points player has
    SF18DeckView playerHandView;
    SF18DeckView playedCardsView;
    JLabel pointsText;

    // Border offsets
    int border = 5;
    int borderBottom = 20;
    int width, height;

    SF18GameState gs;

    public SF18PlayerView(Deck<IngredientCard> deck, int playerId, Set<Integer> humanId, String dataPath)
    {
//        this.width = playerAreaWidth + border*20;
//        this.height = playerAreaHeight + border + borderBottom;
        this.playerId = playerId;
        this.playerHandView = new SF18DeckView(playerId, deck, true, dataPath, new Rectangle(border, border, playerAreaWidth-50, playerAreaHeight));
        this.pointsText = new JLabel(0 + " points");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel wrapperHand = new JPanel();
        wrapperHand.setLayout(new BoxLayout(wrapperHand, BoxLayout.X_AXIS));
        wrapperHand.add(new JLabel(new ImageIcon(dataPath + "hand.png")));
        wrapperHand.add(playerHandView);
        wrapperHand.setOpaque(false);

        JPanel wrapperPlayed = new JPanel();
        wrapperPlayed.setLayout(new BoxLayout(wrapperPlayed, BoxLayout.X_AXIS));
        JLabel playedLabel = new JLabel(new ImageIcon(dataPath + "play.png"));
        wrapperPlayed.add(playedLabel);
        wrapperPlayed.setOpaque(false);

        playerHandView.setOpaque(false);
        pointsText.setOpaque(false);

        add(wrapperHand);
        add(wrapperPlayed);
        add(pointsText);
        setBackground(Color.WHITE);
    }

//    @Override
//    public Dimension getPreferredSize() {
//        return new Dimension(width, height);
//    }

    public void update(SF18GameState gameState)
    {
        gs = gameState;
        playerHandView.updateComponent(gameState.getPlayerHands().get(playerId));
        //playedCardsView.updateComponent(gameState.getPlayedCards().get(playerId));
        this.pointsText.setText(gameState.getGameScore(playerId) + " points");
    }
}
