package sudokuSolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class square {
	private int value;
	private char display;

	private boolean possible_number[];
	private int board_size;
	private final int BLANK_SQUARE = 0;
	private boolean solved;
	
	public square(int board_size, int value) {
		super();
		this.board_size = board_size;
		this.value = value;
		if(value == BLANK_SQUARE) {
			this.display = ' ';
			this.possible_number = new boolean[board_size];
			for(int i=0; i<board_size; i++) {
				this.possible_number[i] = true;
			}
			this.solved = false;
		} else {
			this.display = (char)value;
			this.solved = true;
		}
		
	}
	
	public char getDisplay(){
		return this.display;
	}
	
	public int getValue(){
		return this.value;
	}
	
	public void setValue(int value) {
		this.value = value;
		this.solved = true;
		this.display = (char)value;
	}
	
	public boolean isSolved() {
		return this.solved;
	}
	
	public boolean[] getPossibleNumber() {
		return possible_number;
	}
	
	//method that eliminates used value as a possible number
	//returns true if it is certain the piece is known
	public boolean updatePossibleNumber(Stack<Integer> taken_value) {
		int possible_number_count = 0;
		int certain_value = -1;
		Iterator<Integer> taken_iterator = taken_value.iterator();

		while(taken_iterator.hasNext()) {
			int taken = taken_iterator.next();
			this.possible_number[taken-1] = false;
		}
		
		//uncomment to make faster stopping possible number count if bigger than 2
		//for(int i=0; (i <board_size) && (possible_number_count<2);i++) {
		for(int i=0; i <board_size;i++) {

			if(this.possible_number[i]) {
				possible_number_count++;
				certain_value = i+1;
			}
		}
		//System.out.print(possible_number_count+" Possible ");
		if(possible_number_count == 1) {
			this.setValue(certain_value);
			return true;
		} else {
			return false;
		}
	}
	
	public int getPossibleNumberCount() {
		int possible_number_count = 0;

		for(int i=0; i <board_size;i++) {

			if(this.possible_number[i]) {
				possible_number_count++;
			}
		}
		return possible_number_count;
	}

	public boolean isNumberPossible(int number) {
		return possible_number[number-1];
	}
	
	public List<Integer> getNumberSubset(){
		List<Integer> number_subset = new ArrayList<>();
		for(int i=0; i <board_size;i++) {
			if(this.possible_number[i]) {
				number_subset.add(i+1);
			}
		}
		return number_subset;
	}
	
	
}
