package application;

import chess.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;

public class ChessBoardUI {

	private BorderPane root;
	private GridPane grid;
	private ChessMatch chessMatch;
	private ChessPosition sourcePosition;
	private boolean[][] possibleMoves;

	private static final Color LIGHT = Color.web("#f0d9b5");
	private static final Color DARK = Color.web("#b58863");
	private static final Color HIGHLIGHT = Color.web("#7fc97f", 0.6);
	private static final Color SELECTED = Color.web("#f6f669", 0.5);

	public ChessBoardUI() {
		chessMatch = new ChessMatch();
		root = new BorderPane();
		grid = new GridPane();
		root.setCenter(buildBoard());
		root.setBottom(buildStatusBar());
	}

	public BorderPane getRoot() {
		return root;
	}

	private GridPane buildBoard() {
		grid.getChildren().clear();
		ChessPiece[][] pieces = chessMatch.getPieces();

		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				StackPane square = new StackPane();
				square.setPrefSize(80, 80);

				Color bg = (row + col) % 2 == 0 ? LIGHT : DARK;

				if (possibleMoves != null && possibleMoves[row][col]) {
					bg = HIGHLIGHT;
				}

				if (sourcePosition != null) {
					int sRow = 8 - sourcePosition.getRow();
					int sCol = sourcePosition.getColumn() - 'a';
					if (row == sRow && col == sCol) {
						bg = SELECTED;
					}
				}

				square.setStyle("-fx-background-color: " + toHex(bg) + ";");

				if (pieces[row][col] != null) {
					ImageView iv = getPieceImage(pieces[row][col]);
					if (iv != null)
						square.getChildren().add(iv);
				}

				final int r = row, c = col;
				square.setOnMouseClicked(e -> handleClick(r, c));
				grid.add(square, col, row);
			}
		}
		return grid;
	}

	private ImageView getPieceImage(ChessPiece piece) {
		String color = piece.getColor() == chess.Color.WHITE ? "white" : "black";
		String letter = piece.toString().toLowerCase();

		String pieceName = switch (letter) {
		case "k" -> "king";
		case "q" -> "queen";
		case "r" -> "rook";
		case "b" -> "bishop";
		case "n" -> "knight";
		case "p" -> "pawn";
		default -> null;
		};

		if (pieceName == null)
			return null;

		String path = "/resources/images/" + color + "_" + pieceName + ".png";
		try {
			Image img = new Image(getClass().getResourceAsStream(path));
			ImageView iv = new ImageView(img);
			iv.setFitWidth(70);
			iv.setFitHeight(70);
			return iv;
		} catch (Exception e) {
			return null;
		}
	}

	private void handleClick(int row, int col) {
		try {
			if (sourcePosition == null) {
				sourcePosition = ChessPosition.fromMatrixPosition(row, col);
				possibleMoves = chessMatch.possibleMoves(sourcePosition);
				root.setCenter(buildBoard());
			} else {
				ChessPosition target = ChessPosition.fromMatrixPosition(row, col);
				chessMatch.performChessMove(sourcePosition, target);
				sourcePosition = null;
				possibleMoves = null;
				root.setCenter(buildBoard());
				root.setBottom(buildStatusBar());
			}
		} catch (Exception e) {
			sourcePosition = null;
			possibleMoves = null;
			root.setCenter(buildBoard());
		}
	}

	private HBox buildStatusBar() {
		HBox bar = new HBox(10);
		Label lbl = new Label("Turn: " + chessMatch.getCurrentPlayer());

		if (chessMatch.getCheck()) {
			Label check = new Label("  CHECK!");
			bar.getChildren().addAll(lbl, check);
		} else {
			bar.getChildren().add(lbl);
		}
		return bar;
	}

	private String toHex(Color c) {
		int r = (int) (c.getRed() * 255);
		int g = (int) (c.getGreen() * 255);
		int b = (int) (c.getBlue() * 255);
		double a = c.getOpacity();
		return "rgba(" + r + "," + g + "," + b + "," + a + ")";
	}
}