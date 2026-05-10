package application;

import java.util.Arrays;
import java.util.List;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ChessBoardUI {

	private BorderPane root;
	private GridPane grid;
	private ChessMatch chessMatch;
	private ChessPosition sourcePosition;
	private boolean[][] possibleMoves;
	private MoveHistory moveHistory;

	private Label whiteTimerLabel;
	private Label blackTimerLabel;
	private TimerController whiteTimer;
	private TimerController blackTimer;

	private static final Color LIGHT = Color.web("#f0d9b5");
	private static final Color DARK = Color.web("#b58863");
	private static final Color HIGHLIGHT = Color.web("#7fc97f", 0.6);
	private static final Color SELECTED = Color.web("#f6f669", 0.5);
	private static final int TIMER_SECONDS = 600;

	public ChessBoardUI() {
		chessMatch = new ChessMatch();
		root = new BorderPane();
		grid = new GridPane();
		moveHistory = new MoveHistory();

		setupTimers();

		root.setTop(buildTopBar());
		root.setCenter(buildBoard());
		root.setBottom(buildStatusBar());
		root.setRight(moveHistory.getPanel());

		whiteTimer.start();
	}

	public BorderPane getRoot() {
		return root;
	}

	private void setupTimers() {
		whiteTimerLabel = new Label();
		blackTimerLabel = new Label();

		whiteTimerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
		blackTimerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

		whiteTimer = new TimerController(whiteTimerLabel, TIMER_SECONDS, () -> {
			whiteTimerLabel.setText("TIMEOUT!");
		});

		blackTimer = new TimerController(blackTimerLabel, TIMER_SECONDS, () -> {
			blackTimerLabel.setText("TIMEOUT!");
		});
	}

	private HBox buildTopBar() {
		HBox bar = new HBox(15);
		bar.setStyle("-fx-background-color: #333; -fx-padding: 10px;");

		Label whiteLabel = new Label("WHITE: ");
		whiteLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

		Label blackLabel = new Label("BLACK: ");
		blackLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");

		Button btnSave = new Button("Save");
		btnSave.setStyle("-fx-background-color: #4caf50; " + "-fx-text-fill: white; " + "-fx-font-weight: bold;");
		btnSave.setOnAction(e -> GameSaver.save(moveHistory.getMoves()));

		Button btnLoad = new Button("Load");
		btnLoad.setStyle("-fx-background-color: #2196f3; " + "-fx-text-fill: white; " + "-fx-font-weight: bold;");
		btnLoad.setOnAction(e -> loadGame());

		bar.getChildren().addAll(whiteLabel, whiteTimerLabel, blackLabel, blackTimerLabel, btnSave, btnLoad);
		return bar;
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

				ChessPiece piece = (ChessPiece) chessMatch.getPieces()[8 - sourcePosition.getRow()][sourcePosition
						.getColumn() - 'a'];

				ChessPiece targetPiece = (ChessPiece) chessMatch.getPieces()[8 - target.getRow()][target.getColumn()
						- 'a'];
				boolean isCapture = targetPiece != null;

				chessMatch.performChessMove(sourcePosition, target);

				moveHistory.addMove(piece.toString(), sourcePosition.toString().trim(), target.toString().trim(),
						isCapture);

				sourcePosition = null;
				possibleMoves = null;

				if (chessMatch.getCurrentPlayer() == chess.Color.WHITE) {
					blackTimer.stop();
					whiteTimer.start();
				} else {
					whiteTimer.stop();
					blackTimer.start();
				}

				root.setCenter(buildBoard());
				root.setBottom(buildStatusBar());

				if (chessMatch.getCheckMate()) {
					whiteTimer.stop();
					blackTimer.stop();
				}
			}
		} catch (Exception e) {
			sourcePosition = null;
			possibleMoves = null;
			root.setCenter(buildBoard());
		}
	}

	private void loadGame() {
		String[] saves = GameSaver.getSavedGames();

		if (saves == null || saves.length == 0) {
			System.out.println("No saved games found!");
			return;
		}

		Arrays.sort(saves);
		String latest = "saves/" + saves[saves.length - 1];

		List<String> moves = GameSaver.load(latest);
		System.out.println("Loaded " + moves.size() + " moves from " + latest);

		moveHistory.clear();
		moveHistory.loadMoves(moves);
	}

	private HBox buildStatusBar() {
		HBox bar = new HBox(10);
		bar.setStyle("-fx-padding: 8px; -fx-background-color: #555;");
		Label lbl = new Label("Turn: " + chessMatch.getCurrentPlayer());
		lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");

		if (chessMatch.getCheck()) {
			Label check = new Label("  CHECK!");
			check.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-font-weight: bold;");
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