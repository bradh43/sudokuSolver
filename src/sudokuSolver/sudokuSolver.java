package sudokuSolver;

import java.util.*;
import java.io.*;


public class sudokuSolver {

	private final static int VALID_NUMBER_ARGS = 1;
	
	public static void main(String[] args) {
		System.out.println("Welcome to Sudoku Solver!");
		if(args.length != VALID_NUMBER_ARGS) {
			usuageMessage();
		} else {
			System.out.println("Loading sudoku board...");
			String file_name = args[0];
			File board_file = new File(file_name);
			Scanner file_scanner;
			try {
				file_scanner = new Scanner(board_file);
				//read in board size
				int board_size = file_scanner.nextInt();
				//read in game board
				int game_board[][] = new int[board_size][board_size];
				for(int x=0; x<board_size; x++) {
					for(int y=0; y<board_size; y++) {
						game_board[x][y] = file_scanner.nextInt();
					}
				}
				sudokuBoard board = new sudokuBoard(game_board, board_size);
				board.display();
				System.out.println("Sudoku board loaded");
				board.solve();
				System.out.println("Board Solved!");
			
			} catch (FileNotFoundException e) {
				System.out.println("Unable to load sudoku board file");
				e.printStackTrace();
			}
			
			
			
			
		}
		
		
	}
	
	public static void usuageMessage() {
		System.out.println("Invalid number of arguments.");
		System.out.println("Please run the program with one additional argument(sudokuBoard.txt)");
	}

}
