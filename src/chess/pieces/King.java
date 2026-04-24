package chess.pieces;

import boardgame.Board;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

	public King(Board board, Color color) {
		super(board, color);
	}

	@Override
	public String toString() {
		return "K";
	}

	@Override
	public boolean[][] possibleMoves() {
	    boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

	    int[][] moves = {
	        {-1, 0}, // cima
	        {1, 0},  // baixo
	        {0, -1}, // esquerda
	        {0, 1},  // direita
	        {-1, -1}, // diagonal cima esquerda
	        {-1, 1},  // diagonal cima direita
	        {1, -1},  // diagonal baixo esquerda
	        {1, 1}    // diagonal baixo direita
	    };

	    for (int[] move : moves) {
	        int newRow = position.getRow() + move[0];
	        int newCol = position.getColumn() + move[1];

	        if (getBoard().positionExists(newRow, newCol)) {
	            mat[newRow][newCol] = true;
	        }
	    }

	    return mat;
	}
}