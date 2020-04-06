package solver;

import java.util.*;

public class GreedyBestFirstStrategy extends SearchMethod
{
	
	public GreedyBestFirstStrategy()
	{
		code = "GBFS";
		longName = "Greedy Best-First Search";
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
		//keep searching the fringe until it's empty.
		//Items are "popped" from the fringe in order of lowest heuristic value.
		
		// Put the start state in the Fringe to get explored.
		addToFrontier(aPuzzle.StartState);

		ArrayList<PuzzleState> newStates = new ArrayList<PuzzleState>();

		while(Frontier.size() > 0)
		{
			// Get the next item off the fringe
			PuzzleState thisState = popFrontier();
			
			// Is it the goal state?
			if(thisState.equals(aPuzzle.GoalState))
			{
				// We have found a solution! return it!
				return thisState.GetPathToState();
			}
			// This isn't the goal, just explore the node
			newStates = thisState.explore();
			
			for(int i = 0; i < newStates.size(); i++)
			{
				PuzzleState newChild = newStates.get(i);
				// Add this state to the fringe if possible. Will take care of duplicates.
				if(addToFrontier(newChild))
				{
					//then, work out it's heuristic value
					newChild.HeuristicValue = HeuristicValue(newStates.get(i), aPuzzle.GoalState);
					newChild.setEvaluationFunction(newChild.HeuristicValue);
				}
				
			}
			
			// Sort the fringe by it's Heuristic Value. The PuzzleComparator uses each nodes EvaluationFunction
			// to determine a node's value, based on another. The sort method uses this to sort the Fringe.
			Collections.sort(Frontier, new PuzzleComparator());
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
