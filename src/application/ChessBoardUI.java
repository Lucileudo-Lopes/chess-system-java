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

    private static final Color LIGHT = Color.web("#f0d9b5");
    private static final Color DARK = Color.web("#b58863");
    private static final Color HIGHLIGHT = Color.web("#7fc97f", 0.6);

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
                square.setStyle("-fx-background-color: " + toHex(bg) + ";");

                if (pieces[row][col] != null) {
                    ImageView iv = getPieceImage(pieces[row][col]);
                    if (iv != null) square.getChildren().add(iv);
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
            default  -> null;
        };

        if (pieceName == null) return null;

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
                highlightMoves(row, col);
            } else {
                ChessPosition target = ChessPosition.fromMatrixPosition(row, col);
                chessMatch.performChessMove(sourcePosition, target);
                sourcePosition = null;
                root.setCenter(buildBoard());
                root.setBottom(buildStatusBar());
            }
        } catch (Exception e) {
            sourcePosition = null;
            root.setCenter(buildBoard());
        }
    }

    private void highlightMoves(int row, int col) {
        boolean[][] moves = chessMatch.possibleMoves(
            ChessPosition.fromMatrixPosition(row, col));

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (moves[r][c]) {
                    StackPane sq = getSquare(r, c);
                    if (sq != null)
                        sq.setStyle("-fx-background-color: " + toHex(HIGHLIGHT) + ";");
                }
            }
        }
    }

    private StackPane getSquare(int row, int col) {
        for (var node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) == row
             && GridPane.getColumnIndex(node) == col) {
                return (StackPane) node;
            }
        }
        return null;
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
        return String.format("rgba(%d,%d,%d,%.2f)",
            (int)(c.getRed()*255),
            (int)(c.getGreen()*255),
            (int)(c.getBlue()*255),
            c.getOpacity());
    }
}