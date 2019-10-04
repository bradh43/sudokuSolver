package sudokuSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class sudokuBoard {
	
	private int board_size;
	private square game_board[][];
	private final int BLANK_SQUARE = 0;

	public sudokuBoard(int board_size) {
		super();
		this.board_size = board_size;
		//generate blank game board
		square blank_game_board[][] = new square[board_size][board_size];
		for(int x=0; x<board_size; x++) {
			for(int y=0; y<board_size; y++) {
				blank_game_board[x][y] = new square(board_size, BLANK_SQUARE);
			}
		}
		
		this.game_board = blank_game_board;
	}
	
	
	public sudokuBoard(int[][] game_board, int board_size) {
		super();
		this.board_size = board_size;
		square load_game_board[][] = new square[board_size][board_size];
		for(int x=0; x<board_size; x++) {
			for(int y=0; y<board_size; y++) {
				load_game_board[x][y] = new square(board_size, game_board[x][y]);
			}
		}
		this.game_board = load_game_board;
	}
	
	public void display() {
		String row_border = "+";
		for(int i=0; i < Math.sqrt((double)board_size); i++) {
			for(int j=0; j < Math.sqrt((double)board_size); j++) {
				row_border+="---";
			}
			row_border += "+";
		}
			
		System.out.println(row_border);
		for(int x=0; x<board_size; x++) {
			System.out.print("|");
			for(int y=0; y<board_size; y++) {
				int square = this.game_board[x][y].getValue();
				if(square != BLANK_SQUARE) {
					System.out.print(" "+square+" ");
				} else {
					System.out.print("   ");
				}
				if((y+1)% Math.sqrt((double)board_size) == 0){
					System.out.print("|");
				}
			}
			System.out.println("");
			if((x+1)% Math.sqrt((double)board_size) == 0){
				System.out.println(row_border);
			}

		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void solve() {
		int previous_hash = this.game_board.hashCode();
		while(!boardSolved()) {
			this.updateBoardPossibleValue();
			this.updateBoardPerception();
			this.updateSubset();
			display();
			if(previous_hash == this.game_board.hashCode()) {
				this.forceChain();
			}
			previous_hash = this.game_board.hashCode();
		}
	}
	

	
	public int getGameBoardSize() {
		return this.board_size;
	}
	
	public void updateBoardPossibleValue() {
		this.updateRowPossibleValue();
		this.updateColPossibleValue();
		this.updateBoxPossibleValue();
	}
	
	public void updateBoardPerception() {
		this.updateRowPerception();
		this.updateColPerception();
		this.updateBoxPerception();
	}
	
	private void updateSubset() {
		this.updateColSubset();
		this.updateRowSubset();
		this.updateBoxSubset();

	}
	
	private void forceChain() {
		square old_game_board[][] = this.game_board;
		
		for(int i=0; i <this.board_size; i++) {
			for(int j=0; j<this.board_size; j++) {
				
				if(!this.game_board[i][j].isSolved()) {
					List<Integer> number_subset = this.game_board[i][j].getNumberSubset();
					if(number_subset.size()==2) {
						for(int a=0; a<2; a++) {
							this.game_board[i][j].setValue(number_subset.get(a));
							this.solve();
							//restore the game board
							this.game_board = old_game_board;
						}
					}
				}
			}
		}
	}
	
	private void updateRowSubset() {
		//loop through each row
		for(int row=0; row<this.board_size; row++) {
			List<List<Integer>> number_set = new ArrayList<>();
			//loop through each square in col
			for(int y=0; y<this.board_size; y++) {
				//check if the piece is empty
				if(!this.game_board[row][y].isSolved()) {
					number_set.add(this.game_board[row][y].getNumberSubset());
				}	
				
			}
			//loop through the possible set sizes
			for(int n=2; n<=Math.sqrt(board_size); n++) {
				
				//for each set check each possible subset of size n to see if it occurs exactly n times
				for(List<Integer> set : number_set) {
					//get all the combinations of sets
					List<List<Integer>> set_combination = combination(set,n);
					//check the combinations of sets vs the other sets
					for(List<Integer> possible_set : set_combination) {
						
						int occurrence = 0;
						boolean hidden_subset = true;
						int possible_index = 0;
						//check all the number sets to get the number of occurrence
						for(int a=0; a<number_set.size(); a++) {
							int match = 0;
							List<Integer> other_set = number_set.get(a);
							for(int b=0; b<other_set.size(); b++) {
								for(int c=0; c<possible_set.size(); c++) {
									if(possible_set.get(c) == other_set.get(b)) {
										match++;
									}
								}
							}
							if(match==n) {
								occurrence++;
							} else if(match!=0) {
								hidden_subset = false;
							}
						}
						if(hidden_subset && occurrence==n) {
							//anywhere the hidden subset occurred remove all the other possibilities
							for(int y=0; y<this.board_size; y++) {

								if(!this.game_board[row][y].isSolved()) {
									List<Integer> number_subset = this.game_board[row][y].getNumberSubset();
									for(int number : number_subset) {
										if(number == possible_set.get(0)) {
											Stack<Integer> taken_value = new Stack<Integer>();
											int index = 0;
											for(int num : number_subset) {
												if(index<n && num==possible_set.get(index)) {
													index++;
												} else {
													taken_value.push(num);
												}
											}
											if(this.game_board[row][y].updatePossibleNumber(taken_value)) {
												this.updateBoardPossibleValue();
											}
										}
									}
								}
									
							}
							
						}

					}
					
				}
			}
		}
	}
	
	private void updateColSubset() {
		//loop through each col 
		for(int col=0; col<this.board_size; col++) {
			List<List<Integer>> number_set = new ArrayList<>();
			//loop through each square in col
			for(int x=0; x<this.board_size; x++) {
				//check if the piece is empty
				if(!this.game_board[x][col].isSolved()) {
					number_set.add(this.game_board[x][col].getNumberSubset());
				}	
				
			}
			//loop through the possible set sizes
			for(int n=2; n<=Math.sqrt(board_size); n++) {
				
				//for each set check each possible subset of size n to see if it occurs exactly n times
				for(List<Integer> set : number_set) {
					//get all the combinations of sets
					List<List<Integer>> set_combination = combination(set,n);
					//check the combinations of sets vs the other sets
					for(List<Integer> possible_set : set_combination) {
						
						int occurrence = 0;
						boolean hidden_subset = true;
						int possible_index = 0;
						//check all the number sets to get the number of occurrence
						for(int a=0; a<number_set.size(); a++) {
							int match = 0;
							List<Integer> other_set = number_set.get(a);
							for(int b=0; b<other_set.size(); b++) {
								for(int c=0; c<possible_set.size(); c++) {
									if(possible_set.get(c) == other_set.get(b)) {
										match++;
									}
								}
							}
							if(match==n) {
								occurrence++;
							} else if(match!=0) {
								hidden_subset = false;
							}
						}
						if(hidden_subset && occurrence==n) {
							//anywhere the hidden subset occurred remove all the other possibilities
							for(int x=0; x<this.board_size; x++) {

								if(!this.game_board[x][col].isSolved()) {
									List<Integer> number_subset = this.game_board[x][col].getNumberSubset();
									for(int number : number_subset) {
										if(number == possible_set.get(0)) {
											Stack<Integer> taken_value = new Stack<Integer>();
											int index = 0;
											for(int num : number_subset) {
												if(index<n && num==possible_set.get(index)) {
													index++;
												} else {
													taken_value.push(num);
												}
											}
											if(this.game_board[x][col].updatePossibleNumber(taken_value)) {
												this.updateBoardPossibleValue();
											}
										}
									}
								}	
								
							}
														
						}

					}
					
				}
			}
		}
	}
	
	private void updateBoxSubset() {
		//loop through each corner
		for(int x=0; x<board_size; x+=Math.sqrt(board_size)) {
			for(int y=0; y<board_size; y+=Math.sqrt(board_size)) {
				List<List<Integer>> number_set = new ArrayList<>();
				//loop through each square in box
				for(int row=x; row<x+Math.sqrt(board_size); row++) {
					for(int col=y; col<y+Math.sqrt(board_size); col++) {
						//check if the piece is empty
						if(!this.game_board[row][col].isSolved()) {
							number_set.add(this.game_board[row][col].getNumberSubset());
	
						}
						
					}
				}
				//loop through the possible set sizes
				for(int n=2; n<=Math.sqrt(board_size); n++) {
					
					//for each set check each possible subset of size n to see if it occurs exactly n times
					for(List<Integer> set : number_set) {
						//get all the combinations of sets
						List<List<Integer>> set_combination = combination(set,n);
						//check the combinations of sets vs the other sets
						for(List<Integer> possible_set : set_combination) {
							
							int occurrence = 0;
							boolean hidden_subset = true;
							int possible_index = 0;
							//check all the number sets to get the number of occurrence
							for(int a=0; a<number_set.size(); a++) {
								int match = 0;
								List<Integer> other_set = number_set.get(a);
								for(int b=0; b<other_set.size(); b++) {
									for(int c=0; c<possible_set.size(); c++) {
										if(possible_set.get(c) == other_set.get(b)) {
											match++;
										}
									}
								}
								if(match==n) {
									occurrence++;
								} else if(match!=0) {
									hidden_subset = false;
								}
							}
							if(hidden_subset && occurrence==n) {
								//anywhere the hidden subset occurred remove all the other possibilities
								for(int row=x; row<x+Math.sqrt(board_size); row++) {
									for(int col=y; col<y+Math.sqrt(board_size); col++) {
										if(!this.game_board[row][col].isSolved()) {
											List<Integer> number_subset = this.game_board[row][col].getNumberSubset();
											for(int number : number_subset) {
												if(number == possible_set.get(0)) {
													Stack<Integer> taken_value = new Stack<Integer>();
													int index = 0;
													for(int num : number_subset) {
														if(index<n && num==possible_set.get(index)) {
															index++;
														} else {
															taken_value.push(num);
														}
													}
													if(this.game_board[row][col].updatePossibleNumber(taken_value)) {
														this.updateBoardPossibleValue();
													}
												}
											}
										}
									}
								}		
							}
						}
					}
				}
			}
		}
	}
	
	 public List<List<Integer>> combination(List<Integer> number_list, int k) {
        List<List<Integer>> result = new ArrayList<>();
       
        getCombination(number_list.size()-1, k, 0, result, new ArrayList<>(), number_list);
        
        
        return result;
    }

    public void getCombination(int n, int k, int start_index, List<List<Integer>> result, List<Integer> partial_list, List<Integer> number_list) {
        if (k == partial_list.size()) {
            result.add(new ArrayList<>(partial_list));
            return;
        }
        for (int i = start_index; i <= n; i++) {
            partial_list.add(number_list.get(i));
            getCombination(n, k, i + 1, result, partial_list, number_list);
            partial_list.remove(partial_list.size() - 1);
        }
    }

	
	private void updateRowPerception() {
		for(int row=0; row<this.board_size; row++) {
			for(int y=0; y<this.board_size; y++) {
				if(!(this.game_board[row][y].isSolved())) {
					//check blank spot for a value that is only possible in that position
					boolean[] possible_number = this.game_board[row][y].getPossibleNumber();
					//check all possible numbers
					for(int number=1; number<=this.board_size; number++) {
						//check if number is possible
						if(possible_number[number-1]) {
							//check other blank squares if target is impossible at that location
							boolean target_number_possible = false;
							for(int i=0; (i<this.board_size) && !target_number_possible; i++) {
								if(!(this.game_board[row][y].isSolved())) {
									//make sure it is not the target
									if(i!=y) {
										//make sure piece doesn't already exist
										if(this.game_board[row][i].getValue()==number) {
											target_number_possible = true;
										}
										if(!this.game_board[row][i].isSolved()) {
											if(this.game_board[row][i].isNumberPossible(number)) {
												target_number_possible = true;
											}
										} 
									}
								} else {
									target_number_possible = true;

								}
								
								
							}
							if(!target_number_possible) {
								this.game_board[row][y].setValue(number);
								display();
								updateBoardPossibleValue();
							}
						}
					}
				}
			}
			
		}
	}
	
	private void updateColPerception() {
		for(int col=0; col<this.board_size; col++) {
			for(int x=0; x<this.board_size; x++) {
				if(!(this.game_board[x][col].isSolved())) {
					//check blank spot for a value that is only possible in that position
					boolean[] possible_number = this.game_board[x][col].getPossibleNumber();
					//check all possible numbers
					for(int number=1; number<=this.board_size; number++) {
						//check if number is possible
						if(possible_number[number-1]) {
							//check other blank squares if target is impossible at that location
							boolean target_number_possible = false;
							for(int i=0; (i<this.board_size) && !target_number_possible; i++) {
								if(!(this.game_board[x][col].isSolved())) {

									if(i!=x) {
										//make sure piece doesn't already exist
										if(this.game_board[i][col].getValue()==number) {
											target_number_possible = true;
										} else {
											if(!this.game_board[i][col].isSolved()) {
												if(this.game_board[i][col].isNumberPossible(number)) {
													target_number_possible = true;
												}
											} 
										}
										
									}
								} else {
									target_number_possible = true;

								}
								
								
							}
							if(!target_number_possible) {
								this.game_board[x][col].setValue(number);
								display();
								updateBoardPossibleValue();
							}
						}
					}
				}
			}
			
		}
	}


	private void updateBoxPerception() {
		//loop through each corner
		for(int x=0; x<board_size; x+=Math.sqrt(board_size)) {
			for(int y=0; y<board_size; y+=Math.sqrt(board_size)) {
				//System.out.print("\nCorner ("+x+","+y+"): ");
				//loop through each square in box
				for(int row=x; row<x+Math.sqrt(board_size); row++) {
					for(int col=y; col<y+Math.sqrt(board_size); col++) {
						//check if the piece is empty
						if(!this.game_board[row][col].isSolved()) {
							//check blank spot for a value that is only possible in that position
							boolean[] possible_number = this.game_board[row][col].getPossibleNumber();
							//check all possible numbers in each 
							for(int number=1; number<=this.board_size; number++) {
								//check if number is possible
								if(possible_number[number-1]) {

									//check other blank squares if target is impossible at that location
									boolean target_number_possible = false;
									for(int i=x; (i<x+Math.sqrt(board_size)) && !target_number_possible; i++) {
										for(int j=y; (j<y+Math.sqrt(board_size)) && !target_number_possible; j++) {
											if(!this.game_board[row][col].isSolved()) {
												if(!((row == i) && (col == j))){
													//check if blank
													if(!this.game_board[i][j].isSolved()) {
														//check if number not possible in that location
														if(this.game_board[i][j].isNumberPossible(number)) {
															target_number_possible = true;
														}
													} else {
														//make sure piece doesn't already exist if piece is not empty
														if(this.game_board[i][j].getValue()==number) {
															target_number_possible = true;
														}
													}
													
													
												} 
												
											} else {
												
												target_number_possible = true;

											}
											
											
										}
									}
									
									
									if(!target_number_possible) {
										this.game_board[row][col].setValue(number);
										display();

										updateBoardPossibleValue();
									}
								}
							}
						}
					}
				}
			}
		}
	}


	
	private void updateRowPossibleValue() {
		for(int row=0; row<this.board_size; row++) {
			Stack<Integer> taken_value = new Stack<Integer>();
			for(int y=0; y<this.board_size; y++) {
				if(this.game_board[row][y].isSolved()) {
					taken_value.push(this.game_board[row][y].getValue());
				} 
			}
			for(int y=0; y<this.board_size; y++) {
				if(!this.game_board[row][y].isSolved()) {
					if(this.game_board[row][y].updatePossibleNumber(taken_value)) {
						updateBoardPossibleValue();
					}
				}
			}
		}
		
	}
	
	private void updateColPossibleValue() {
		for(int col=0; col<this.board_size; col++) {
			Stack<Integer> taken_value = new Stack<Integer>();
			for(int x=0; x<this.board_size; x++) {
				if(this.game_board[x][col].isSolved()) {
					taken_value.push(this.game_board[x][col].getValue());
				}
			}
			for(int x=0; x<this.board_size; x++) {
				if(!this.game_board[x][col].isSolved()) {
					if(this.game_board[x][col].updatePossibleNumber(taken_value)) {
						updateBoardPossibleValue();
					}
				}
			}
		}
		
	}
	
	private void updateBoxPossibleValue() {
		for(int x=0; x<board_size; x+=Math.sqrt(board_size)) {
			for(int y=0; y<board_size; y+=Math.sqrt(board_size)) {
				Stack<Integer> taken_value = new Stack<Integer>();
				for(int row=x; row<x+Math.sqrt(board_size); row++) {
					for(int col=y; col<y+Math.sqrt(board_size); col++) {
						if(this.game_board[row][col].isSolved()) {
							taken_value.push(this.game_board[row][col].getValue());
						} 
					}
				}
				for(int row=x; row<x+Math.sqrt(board_size); row++) {
					for(int col=y; col<y+Math.sqrt(board_size); col++) {
						if(!this.game_board[row][col].isSolved()) {
							if(this.game_board[row][col].updatePossibleNumber(taken_value)) {
								updateBoardPossibleValue();
							}

						}
					}
				}
			}
		}
		
	}
	
	private boolean boardSolved() {
		boolean board_solved = true;
		for(int x=0; (x<this.board_size) && board_solved; x++) {
			for(int y=0; (y<this.board_size) && board_solved; y++) {
				if(!this.game_board[x][y].isSolved()) {
					board_solved = false;
				}
			}
		}
		return board_solved;
	}

}
