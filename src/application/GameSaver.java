package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GameSaver {

	private static final String SAVE_FOLDER = "saves/";

	public static void save(List<String> moves) {
		try {
			new File(SAVE_FOLDER).mkdirs();

			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
			String filename = SAVE_FOLDER + "game_" + timestamp + ".txt";

			PrintWriter pw = new PrintWriter(new FileWriter(filename));
			for (String move : moves) {
				pw.println(move);
			}
			pw.close();
			System.out.println("Game saved: " + filename);

		} catch (IOException e) {
			System.err.println("Error saving: " + e.getMessage());
		}
	}

	public static List<String> load(String filename) {
		List<String> moves = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				moves.add(line);
			}
			br.close();
		} catch (IOException e) {
			System.err.println("Error loading: " + e.getMessage());
		}
		return moves;
	}

	public static String[] getSavedGames() {
		File folder = new File(SAVE_FOLDER);
		if (!folder.exists())
			return new String[0];

		return folder.list((dir, name) -> name.endsWith(".txt"));
	}
}