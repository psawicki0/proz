import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public abstract class Piece {
    protected final Cell.Colour colour;
    protected Cell cell;
    protected BufferedImage image;

    public Piece(Cell.Colour colour) {
        this.colour = colour;
        try {
            image = ImageIO.read(getImageFile());
        } catch (IOException e) {
            System.out.println("Could not open image: " + e.getMessage());
        }
    }

    public void draw(Graphics g) {
        g.drawImage(image, cell.getX(), cell.getY(), cell.getWidth(), cell.getHeight(), null);
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    protected ArrayList<Move> trimPossibleMoves(ArrayList<Move> moves) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (Move move : moves) {
            int y = move.after.y;
            int x = move.after.x;
            if (y >= 0 && y < cell.getBoard().getBoardSize()) {
                if (x >= 0 && x < cell.getBoard().getBoardSize()) {
                    Piece piece = cell.getBoard().getCells()[y][x].getPiece();
                    if (piece == null || piece.getColour() != colour) {
                        move.after = cell.getBoard().getCells()[y][x].getPosition();
                        possibleMoves.add(move);
                    }
                }
            }
        }
        return possibleMoves;
    }

    protected ArrayList<Move> getDiagonalMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        Point start = cell.getPosition();
        int y = start.y - 1;
        int x = start.x - 1;
        while (y >= 0 && x >= 0) {
            moves.add(Move.get(start, cell.getBoard().getCells()[y][x].getPosition()));
            if (cell.getBoard().getCells()[y][x].getPiece() != null)
                break;
            --y;
            --x;
        }
        y = start.y - 1;
        x = start.x + 1;
        while (y >= 0 && x < cell.getBoard().getBoardSize()) {
            moves.add(Move.get(start, cell.getBoard().getCells()[y][x].getPosition()));
            if (cell.getBoard().getCells()[y][x].getPiece() != null)
                break;
            --y;
            ++x;
        }
        y = start.y + 1;
        x = start.x - 1;
        while (y < cell.getBoard().getBoardSize() && x >= 0) {
            moves.add(Move.get(start, cell.getBoard().getCells()[y][x].getPosition()));
            if (cell.getBoard().getCells()[y][x].getPiece() != null)
                break;
            ++y;
            --x;
        }
        y = start.y + 1;
        x = start.x + 1;
        while (y < cell.getBoard().getBoardSize() && x < cell.getBoard().getBoardSize()) {
            moves.add(Move.get(start, cell.getBoard().getCells()[y][x].getPosition()));
            if (cell.getBoard().getCells()[y][x].getPiece() != null)
                break;
            ++y;
            ++x;
        }
        return moves;
    }

    protected ArrayList<Move> getVerticalMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        Point start = cell.getPosition();
        int x = start.x;
        for (int y = cell.getPosition().y - 1; y >= 0; --y) {
            moves.add(Move.get(start, cell.getBoard().getCells()[y][x].getPosition()));
            if (cell.getBoard().getCells()[y][x].getPiece() != null)
                break;
        }
        for (int y = cell.getPosition().y + 1; y < cell.getBoard().getBoardSize(); ++y) {
            moves.add(Move.get(start, cell.getBoard().getCells()[y][x].getPosition()));
            if (cell.getBoard().getCells()[y][x].getPiece() != null)
                break;
        }
        return moves;
    }

    protected ArrayList<Move> getHorizontalMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        Point start = cell.getPosition();
        int y = start.y;
        for (int x = cell.getPosition().x - 1; x >= 0; --x) {
            moves.add(Move.get(start, cell.getBoard().getCells()[y][x].getPosition()));
            if (cell.getBoard().getCells()[y][x].getPiece() != null)
                break;
        }
        for (int x = cell.getPosition().x + 1; x < cell.getBoard().getBoardSize(); ++x) {
            moves.add(Move.get(start, cell.getBoard().getCells()[y][x].getPosition()));
            if (cell.getBoard().getCells()[y][x].getPiece() != null)
                break;
        }
        return moves;
    }

    public Cell.Colour getColour() {
        return colour;
    }

    public abstract String getName();

    public abstract ArrayList<Move> getPossibleMoves();

    private File getImageFile() {
        return new File("src/main/resources/alpha/alpha/320/" + getColourAsString() + getName() + ".png");
    }

    protected String getColourAsString(){
        return colour == Cell.Colour.black ? "Black" : "White";
    }

    public abstract String getName();

    public abstract boolean isAppropriateMove(Cell destination); //checks if piece selected by player can be moved to selected cell

    public boolean checkIfPathIsClear(Cell start, Cell destination){ // checks if cells between start and destionation are not occupied
        Point startPos = start.getPosition();
        int startX = startPos.x;
        int startY = startPos.y;
        Point desPos = destination.getPosition();
        int destX = desPos.x;
        int destY = desPos.y;
        Cell[][] cells = start.board.getCells();
        int s,d, m, t;

        if(startX == destX){ // vertical movement
            s = Math.min(destY, startY);
            d = Math.max(destY, startY);
            for(int k = s+1; k < d; k++){
                if(cells[k][destX].getOccupation())
                    return false;
            }
        }
        if(startY == destY){ // horizontal movement
            s = Math.min(destX, startX);
            d = Math.max(destX, startX);
            for(int k = s+1; k < d; k++){
                if(cells[destY][k].getOccupation())
                    return false;
            }
        }
        if(startX != destX && startY != destY) { // diagonal movement
            if( (destX > startX && destY > startY) || (destX < startX && destY < startY) ) {
                s = Math.min(destX, startX); // s - lowest X, d - highest X
                d = Math.max(destX, startX);
                m = Math.min(destY, startY); // m - lowest Y, t - highest Y
                t = Math.max(destY, startY);
                int l = m + 1;
                for (int k = s + 1; k<d; k++){ //increase X, increase Y
                    if (cells[l][k].getOccupation()) {
                        System.out.println("Path is not clear");
                        return false;
                    }
                    l++;
                }
            }
            else{
                s = Math.min(destX, startX); // s - lowest X, d - highest X
                d = Math.max(destX, startX);
                m = Math.min(destY, startY); // m - lowest Y, t - highest Y
                t = Math.max(destY, startY);
                int l = t - 1;
                for (int k = s + 1; k<d; k++){ //increase X, decrease Y
                    if (cells[l][k].getOccupation()) {
                        System.out.println("Path is not clear");
                        return false;
                    }
                    l--;
                }
            }
        }
        System.out.println("Path is clear");
        return true;
    }
}
