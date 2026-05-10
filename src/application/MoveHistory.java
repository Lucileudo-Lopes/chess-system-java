package application;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

public class MoveHistory {

	private List<String> moves = new ArrayList<>();
	private ListView<String> listView;
	private int moveNumber = 1;

	public MoveHistory() {
		listView = new ListView<>();
		listView.setPrefWidth(180);
		listView.setPrefHeight(640);
		listView.setStyle("-fx-background-color: #222; -fx-control-inner-background: #222;");
	}

	public VBox getPanel() {
		Label title = new Label("Moves");
		title.setStyle(
				"-fx-font-size: 14px; " + "-fx-font-weight: bold; " + "-fx-text-fill: white; " + "-fx-padding: 5px;");

		VBox panel = new VBox(5, title, listView);
		panel.setStyle("-fx-background-color: #333; -fx-padding: 5px;");
		return panel;
	}

	public List<String> getMoves() {
		return moves;
	}

	public void addMove(String piece, String source, String target, boolean isCapture) {
		String action = isCapture ? "x" : "-";

		String notation = piece + source + action + target;

		moves.add(notation);

		if (moves.size() % 2 == 1) {
			listView.getItems().add(moveNumber + ". " + notation);
		} else {
			int lastIndex = listView.getItems().size() - 1;
			String lastItem = listView.getItems().get(lastIndex);
			listView.getItems().set(lastIndex, lastItem + "   " + notation);
			moveNumber++;
		}

		listView.scrollTo(listView.getItems().size() - 1);
	}

	public void loadMoves(List<String> moves) {
		for (String move : moves) {
			listView.getItems().add(move);
		}
		listView.scrollTo(listView.getItems().size() - 1);
	}

	public void clear() {
		moves.clear();
		listView.getItems().clear();
		moveNumber = 1;
	}
}