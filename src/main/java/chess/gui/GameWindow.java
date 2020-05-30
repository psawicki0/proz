package chess.gui;

import chess.actions.ActionSaveAs;
import chess.mechanics.Board;
import chess.network.ConnectionHandler;
import chess.utilities.GameAttributes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
//import java.time.LocalDateTime;


public class GameWindow {
    private JFrame window;
    private Board board;
    private String playerName;
    private String opponentName;
    private String opponentIP;
    private ConnectionHandler connectionHandler;

    public GameWindow(GameAttributes gameAttributes) {
        window = new JFrame("Chess");
        window.setLayout(new BorderLayout(10, 10));

        this.connectionHandler = gameAttributes.getConnectionHandler();

        if (gameAttributes.getBoard() != null) {
            this.board = gameAttributes.getBoard();
        } else {
            this.board = new Board(gameAttributes.getPlayerColour(), gameAttributes.getConnectionHandler());
        }
        setPlayerName(gameAttributes.getPlayerName());
        setOpponentName(gameAttributes.getOpponentName());

        window.add(this.board, BorderLayout.CENTER);

        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (ignoredWarning("Do you want to exit game?")) {
                    System.exit(0);
                }
            }
        });
        window.setVisible(true);

        initializeGameMenuBar(window);
        window.pack();
    }

    private void initializeGameMenuBar(JFrame window) {
        // Menu buttons
        final JMenuItem goBackToMenuButton = new JMenuItem("Go to menu");
        final JMenuItem saveGameButton = new JMenuItem("Save game");
        saveGameButton.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        final JMenuItem quitGameButton = new JMenuItem("Quit game");
        quitGameButton.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        final JLabel playerNameLabel;
        if (board.checkIfSinglePlayer()) {
            playerNameLabel = new JLabel("  |  " + playerName + "  ");
        } else {
            playerNameLabel = new JLabel("  |  " + playerName + " vs " + opponentName + "  ");
        }

        // buttons' listeners
        goBackToMenuButton.addActionListener(e -> {
            // if at least 1 move was made, ask player if he wants to exit a game without save
            // if he is playing with another player then notify another player
            if (!checkIfSaveIsPossible() || ignoredWarning("Do you want to exit game without saving?")) {
                SwingUtilities.invokeLater(new Menu());
                window.dispose();
                if (connectionHandler != null)
                    connectionHandler.stopReceiving();
            }
        });
        saveGameButton.addActionListener(new ActionSaveAs(this));
        quitGameButton.addActionListener(e -> {
            if (!checkIfSaveIsPossible() || ignoredWarning("Do you want to exit game without saving?")) {
                System.exit(0);
            }
        });

        // Menu bar in game
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(goBackToMenuButton);
        if(isSinglePlayer())
            menuBar.add(saveGameButton);
        menuBar.add(quitGameButton);
        menuBar.add(playerNameLabel);
        window.setJMenuBar(menuBar);
    }

    public JFrame createWarningWindow() {
        final JFrame warningWindow = new JFrame("Warning");
        warningWindow.setSize(new Dimension(300, 100));
        warningWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        warningWindow.setVisible(true);
        return warningWindow;
    }

    public boolean ignoredWarning(String warningMessage) {
        JFrame warningWindow = createWarningWindow();
        boolean yesResponse = JOptionPane.showConfirmDialog(warningWindow, warningMessage) == JOptionPane.YES_OPTION;
        warningWindow.setVisible(false);
        warningWindow.dispose();
        return yesResponse;
    }

    public boolean checkIfSaveIsPossible() {
        return board.checkIfBoardAltered() && isSinglePlayer();
    }

    public boolean isSinglePlayer() {
        return board.checkIfSinglePlayer();
    }

    // getters
    public Board getBoard() {
        return board;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public String getOpponentIP() {
        return this.opponentIP;
    }

    // setters
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public void setOpponentIP(String opponentIP) {
        this.opponentIP = opponentIP;
    }
}