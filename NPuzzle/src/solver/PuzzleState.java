package solver;

import java.util.*;

/**
 * @author COS30019
 *
 */
public class PuzzleState implements Comparable<PuzzleState>
{
	public int[][] Puzzle;
	public PuzzleState Parent;
	public ArrayList<PuzzleState> Children;
	public int Cost;
	public int HeuristicValue;
	private int EvaluationFunction;
	public direction PathFromParent;
	
	public PuzzleState(PuzzleState aParent, direction aFromParent, int[][] aPuzzle)
	{
		Parent = aParent;
		PathFromParent = aFromParent;
		Puzzle = aPuzzle;
		Cost = Parent.Cost + 1;
		EvaluationFunction = 0;
		HeuristicValue = 0;
	}
	
	public PuzzleState(int[][] aPuzzle)
	{
		Parent = null;
		PathFromParent = null;
		Cost = 0;
		Puzzle = aPuzzle;
		EvaluationFunction = 0;
		HeuristicValue = 0;
	}
	
	public int getEvaluationFunction()
	{
		return EvaluationFunction;
	}
	
	public void setEvaluationFunction(int value)
	{
		EvaluationFunction = value;
	}
	
	public direction[] getPossibleActions()
	{
		//find where the blank cell is and store the directions.
		direction[] result;
		int[] blankLocation = {0, 0};	//dummy value to avoid errors.
		
		try
		{
			blankLocation = findBlankCell();
		}
		catch(InvalidPuzzleException e)
		{
			System.out.println("Could not find 0 tile! Aborting...");
			System.exit(1);
		}

		result = new direction[countMovements(blankLocation)];
		int thisIndex = 0;

		if(blankLocation[0] == 0)
		{
			//the blank cell is already as far left as it will go, it can move right
			result[thisIndex++] = direction.Right;
		}
		else if(blankLocation[0] == (Puzzle.length - 1))
		{
			result[thisIndex++] = direction.Left;
		}
		else
		{
			result[thisIndex++] = direction.Left;
			result[thisIndex++] = direction.Right;
		}
		
		if(blankLocation[1] == 0)
		{
			//the blank cell is already as far up as it will go, it can move down
			result[thisIndex++] = direction.Down;
		}
		else if(blankLocation[1] == (Puzzle[0].length - 1))
		{
			result[thisIndex++] = direction.Up;
		}
		else
		{
			result[thisIndex++] = direction.Up;
			result[thisIndex++] = direction.Down;
		}
		
		//System.out.println("Ran getPossibleActions. blankLocation[0] = " + blankLocation[0] + " | blankLocation[1] = " + blankLocation[1]);
		//System.out.println("Results at end: " + Arrays.deepToString(result));

		// By adding just this, the program is about 33% faster and 12% more space-efficient.
		// Note that this doesn't actually apply to REALLY small sets, but it works well on large sets.
		/*
		Results:
		No array sorting:	Size = 52497	Time = 105,352,915,500ns
		With array sorting:	Size = 46545	Time = 76,797,189,100ns
		*/
		Arrays.sort(result);

		return result;
	}
	
	private int countMovements(int[] blankLocation)
	{
		int result = 2;
		try
		{
			blankLocation = findBlankCell();
			if(blankLocation[0] != 0
				&& blankLocation[0] != (Puzzle.length - 1))
			{
				// Previously used OR (||), now uses AND (&&). Unnecessary else statement.
				result++;
			}
			if(blankLocation[1] != 0
				&& blankLocation[1] != (Puzzle[0].length - 1))
			{
				result++;
			}
		}
		catch (InvalidPuzzleException e)
		{
			// Big uh-oh
			System.out.println("countMovements ran into an error (PuzzleState.java). Aborting.");
			System.exit(1);
		}
		return result;
	}
	
	private int[] findBlankCell() throws InvalidPuzzleException
	{
		for(int i = 0; i < Puzzle.length; i++) // Loops through the current grid of tiles on the board to find where the '0' tile is.
		{
			for(int j = 0; j < Puzzle[i].length; j++)
			{
				if(Puzzle[i][j] == 0)
				{
					int[] result = {i, j};
					return result;
				}
			}
		}
		//No blank cell found?
		throw new InvalidPuzzleException(this);
	}
	
	private int[][] cloneArray(int[][] cloneMe)
	{
		int[][] result = new int[cloneMe.length][cloneMe[0].length];
		for(int i = 0; i < cloneMe.length; i++)
		{
			for(int j = 0; j < cloneMe[i].length; j++)
			{
				result[i][j] = cloneMe[i][j];
			}
		}
		return result;
	}
	
	public PuzzleState move(direction aDirection) throws CantMoveThatWayException
	{
		//Moving up moves the empty cell up (and the cell above it down)
		//first, create the new one (the one to return)
		PuzzleState result = new PuzzleState(this, aDirection, cloneArray(this.Puzzle));
		
		//now, execute the changes: move the blank cell aDirection
		//find the blankCell
		int[] blankCell = {0, 0};
		try
		{
			blankCell = findBlankCell();
		}
		catch(InvalidPuzzleException e)
		{
			System.out.println("There was an error in processing! Aborting...");
			System.exit(1);
		}
		try
		{
			// move the blank cell in the new child puzzle
			// Everything has been made to be in the order of UP, LEFT, DOWN, RIGHT, just to be sure.
			if(aDirection == direction.Up)
			{
				result.Puzzle[blankCell[0]][blankCell[1]] = result.Puzzle[blankCell[0]][blankCell[1] - 1];
				result.Puzzle[blankCell[0]][blankCell[1] - 1] = 0;
			}
			else if(aDirection == direction.Left)
			{
				result.Puzzle[blankCell[0]][blankCell[1]] = result.Puzzle[blankCell[0] - 1][blankCell[1]];
				result.Puzzle[blankCell[0] - 1][blankCell[1]] = 0;
			}
			else if(aDirection == direction.Down)
			{
				result.Puzzle[blankCell[0]][blankCell[1]] = result.Puzzle[blankCell[0]][blankCell[1] + 1];
				result.Puzzle[blankCell[0]][blankCell[1] + 1] = 0;
			}
			else	//aDirection == Right;
			{
				result.Puzzle[blankCell[0]][blankCell[1]] = result.Puzzle[blankCell[0] + 1][blankCell[1]];
				result.Puzzle[blankCell[0] + 1][blankCell[1]] = 0;
			}
			return result;
		}
		catch(IndexOutOfBoundsException ex)
		{
			throw new CantMoveThatWayException(this, aDirection);
		}
	}
	
	@Override
	public boolean equals(Object aObject) throws ClassCastException
	{
		PuzzleState aState = (PuzzleState)aObject;
		//evaluate if these states are the same (does this.Puzzle == aState.Puzzle)?
		for(int i = 0; i < Puzzle.length; i++)
		{
			for(int j = 0; j < Puzzle[i].length; j++)
			{
				if(this.Puzzle[i][j] != aState.Puzzle[i][j])
					return false;		// Stop checking as soon as we find an 
										// element that doesn't match
			}
		}
		return true;	//All elements matched? Return true;
	}

	//this is to allow the TreeSet to sort it.
	public int compareTo(PuzzleState aState)
	{
		return EvaluationFunction - aState.getEvaluationFunction();
	}
	
	public ArrayList<PuzzleState> explore()
	{
		// Populate children
		direction[] possibleMoves = getPossibleActions();
		Children = new ArrayList<PuzzleState>();
		for(int i = 0; i < possibleMoves.length; i++)
		{
			try
			{
				Children.add(move(possibleMoves[i]));
			}
			catch (CantMoveThatWayException e)
			{
				System.out.println("Explored illegal move " + possibleMoves[i] + "! Aborting...");
				System.exit(1);
			}
		}
		return Children;
	}
	
	public direction[] GetPathToState()
	{
		if(Parent != null)
		{
			// The path to here is the path to parent
			// plus parent to here
			/*
			direction[] pathToParent = Parent.GetPathToState();
			result = new direction[pathToParent.length + 1];
			for(int i = 0; i < pathToParent.length; i++)
			{
				result[i] = pathToParent[i];
			}
			result[result.length - 1] = this.PathFromParent;
			return result;
			*/
			direction[] pathToParent = Parent.GetPathToState();
			direction[] result = Arrays.copyOf(pathToParent, pathToParent.length + 1);
			result[result.length - 1] = this.PathFromParent;
			return result;
		} else
		{
			// If this is the root node, there is no path!
			direction[] result = new direction[0];
			return result;
		}
	}
}
