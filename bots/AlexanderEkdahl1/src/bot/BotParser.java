/**
 * Warlight AI Game Bot
 *
 * Last update: April 02, 2014
 *
 * @author Jim van Eeden
 * @version 1.0
 * @License MIT License (http://opensource.org/Licenses/MIT)
 */

package bot;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.InputStream;

import map.Region;
import move.PlaceArmiesMove;
import move.AttackTransferMove;

public class BotParser {
	final Scanner scan;
	final Bot bot;
	BotState currentState;

	public BotParser(Bot bot) {
		this.scan = new Scanner(System.in);
		this.bot = bot;
		this.currentState = new BotState();
	}

	public void run() {
		while (scan.hasNextLine()) {
			String line = scan.nextLine().trim();
			if (line.length() == 0) {
				continue;
			}
			System.err.println("Input(" + currentState.getRoundNumber() + "): " + line);
			String[] parts = line.split(" ");
			if (parts[0].equals("pick_starting_region")) {
				currentState.setPickableStartingRegions(parts);
				Region startingRegion = bot.getStartingRegion(currentState,
						Long.valueOf(parts[1]));
				output(Integer.toString(startingRegion.getId()));
			} else if (parts[0].equals("go")) {
				String output = "";
				if (parts[1].equals("place_armies")) {
					ArrayList<PlaceArmiesMove> placeArmiesMoves = bot
							.getPlaceArmiesMoves(currentState,
									Long.valueOf(parts[2]));
					for (PlaceArmiesMove move : placeArmiesMoves)
						output = output.concat(move.getString() + ",");
				} else {
					ArrayList<AttackTransferMove> attackTransferMoves = bot
							.getAttackTransferMoves(currentState,
									Long.valueOf(parts[2]));
					for (AttackTransferMove move : attackTransferMoves)
						output = output.concat(move.getString() + ",");
				}

				if (output.length() > 0) {
					output(output);
				} else {
					output("No moves");
				}
			
			} else if (parts[0].equals("settings")) {
				// update settings
				currentState.updateSettings(parts[1], parts[2]);
			} else if (parts[0].equals("setup_map")) {
				// initial full map is given
				currentState.setupMap(parts);
			} else if (parts[0].equals("update_map")) {
				// all visible regions are given
				currentState.updateMap(parts);
			} else if (parts[0].equals("opponent_moves")) {
				// all visible opponent moves are given
				currentState.readOpponentMoves(parts);
			} else {
				System.err.printf("Unable to parse line \"%s\"\n", line);
			}
		}
	}

	private void output(String s) {
		System.err.println("Output(" + currentState.getRoundNumber() + "): " + s);
		System.out.println(s);
	}
}