package chess.utilities;

import chess.gui.GameWindow;
import chess.mechanics.*;

import java.util.ArrayList;
import java.util.List;

public class SavedGame {
    private String programVersion = "1.0"; // in case modified program won't support old files
    private String playerName;
    private boolean whiteTurn;
    private List<SavedCell> cellsList;
    //private GameStatus status;

    //public enum GameStatus {x, checkmate}

    public SavedGame(GameWindow gameWindow) {
        Board board = gameWindow.getBoard();
        this.cellsList = new ArrayList<SavedCell>();

        // initialize saved Game values
        this.playerName = gameWindow.getPlayerName();
        this.whiteTurn = board.getWhiteTurn();

        // initialize list of cells
        Cell[][] cells = board.getCells();
        int size = 8; // number of rows and columns
        Cell realCell;
        for (int y = size - 1; y >= 0; --y) {
            for (int x = 0; x < size; ++x) {
                realCell = cells[y][x];
                SavedCell savedCell = new SavedCell(realCell);
                cellsList.add(savedCell);
            }
        }
    }

    public Board restoreBoard() {
        int size = 8;
        Cell[][] cells = new Cell[size][size];

        for (int y = size - 1; y >= 0; --y) {
            for (int x = 0; x < size; ++x) {
                int cellListIndex = x + (7 - y)*8;
                SavedCell savedCell = cellsList.get(cellListIndex);
                Cell cell = savedCell.createCell();
                cells[y][x] = cell;
            }
        }

        return new Board(cells, this.whiteTurn, true);
    }

    public GameAttributes createGameAttributes() {
        GameAttributes savedGameAttr = new GameAttributes();
        savedGameAttr.setBoard(restoreBoard());
        savedGameAttr.setPlayerName(playerName);
        return savedGameAttr;
    }
}