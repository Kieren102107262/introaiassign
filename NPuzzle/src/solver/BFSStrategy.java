package solver;
import java.util.*;

public class BFSStrategy extends SearchMethod {
	
	public BFSStrategy()
	{
		code = "BFS";
		longName = "Breadth-First Search";
		Frontier = new LinkedList<PuzzleState>();
		Searched = new LinkedList<PuzzleState>();
	}
	
	protected PuzzleState popFrontier()
	{
		// Remove an item from the fringe to be searched
		PuzzleState thisState = Frontier.pop();
		// Add it to the list of searched states, so that it isn't searched again
		Searched.add(thisState);
		
		return thisState;
	}
	
	@Override
	public direction[] Solve(nPuzzle aPuzzle)
	{
		// This method uses the fringe as a queue.
		// Therefore, nodes are searched in order of cost, with the lowest cost
		// unexplored node searched next.
		//-----------------------------------------
		
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
				// Add this state to the fringe. Will take care of duplicates.
				addToFrontier(newStates.get(i));
			}
		}
		
		// No solution found and we've run out of nodes to search :(
		return null;
	}
	
	public boolean addToFrontier(PuzzleState aState)
	{
		//if this state has been found before,
		if(Searched.contains(aState) || Frontier.contains(aState))
		{
			// Discard this duplicate.
			return false;
		}
		else
		{
			// Else put this item on the end of the queue;
			Frontier.addLast(aState);
			return true;
		}
	}

}
