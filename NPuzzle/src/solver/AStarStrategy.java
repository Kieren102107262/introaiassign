package solver;

import java.util.*;

public class AStarStrategy extends SearchMethod
{
	
	public AStarStrategy()
	{
		code = "AStar";
		longName = "A* Search";
		Frontier = new LinkedList<PuzzleState>();
		Searched = new LinkedList<PuzzleState>();
	}
	
	public boolean addToFrontier(PuzzleState aState)
	{
		//We only want to add the new state to the fringe if it doesn't exist
		// in the fringe or the searched list.
		if(Searched.contains(aState) || Frontier.contains(aState))
		{
			return false;
		}
		else
		{
			Frontier.add(aState);
			return true;
		}
	}
	
	public direction[] Solve(nPuzzle aPuzzle)
	{
		addToFrontier(aPuzzle.StartState);
		ArrayList<PuzzleState> newStates = new ArrayList<PuzzleState>();

		// GScore will equal the state's HeuristicValue
		// fScore will equal the state's EvaluationFunction

		aPuzzle.StartState.HeuristicValue = 0;
		aPuzzle.StartState.setEvaluationFunction(HeuristicValue(aPuzzle.StartState, aPuzzle.GoalState));

		while(Frontier.size() > 0)
		{
			// Get the item with the lowest heuristic
			Collections.sort(Frontier, new PuzzleComparator());
			PuzzleState thisState = popFrontier();
			
			if(thisState.equals(aPuzzle.GoalState))
			{
				return thisState.GetPathToState();
			}
			Frontier.remove(thisState);

			newStates = thisState.explore();
			for(int i = 0; i < newStates.size(); i++) // For each neighbour of the current state...
			{
				PuzzleState newChild = newStates.get(i);
				int tentative_gScore = thisState.HeuristicValue + HeuristicValue(thisState, newChild);
				if(tentative_gScore < newChild.HeuristicValue)
				{
					// Work out it's heuristic value
					newChild.HeuristicValue = tentative_gScore;
					newChild.setEvaluationFunction(newChild.HeuristicValue + HeuristicValue(newChild, aPuzzle.GoalState));
					addToFrontier(newChild);
				}
			}
		}
		
		//no more nodes and no path found?
		return null;
	}
	
	protected PuzzleState popFrontier()
	{
		//remove a state from the top of the fringe so that it can be searched.
		PuzzleState lState = Frontier.pollFirst();
		
		//add it to the list of searched states so that duplicates are recognised.
		Searched.add(lState);
		
		return lState;
	}
	
	private int HeuristicValue(PuzzleState aState, PuzzleState goalState)
	{
		//find out how many elements in aState match the goalState
		//return the number of elements that don't match
		int heuristic = 0;
		for(int i = 0; i < aState.Puzzle.length; i++)
		{
			for(int j = 0; j < aState.Puzzle[i].length; j++)
			{
				if(aState.Puzzle[i][j] != goalState.Puzzle[i][j])
					heuristic++;
			}
		}
		
		return heuristic;
	}
	
}
