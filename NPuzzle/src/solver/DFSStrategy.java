package solver;
import java.util.*;

public class DFSStrategy extends SearchMethod {
	
	private LinkedList<PuzzleState> GoalsFound;

	public DFSStrategy()
	{
		code = "DFS";
		longName = "Depth-First Search";
		Frontier = new LinkedList<PuzzleState>();
		Searched = new LinkedList<PuzzleState>();
		GoalsFound = new LinkedList<PuzzleState>();
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
		// Iterative DFS.
		Frontier.push(aPuzzle.StartState);

		ArrayList<PuzzleState> newStates = new ArrayList<PuzzleState>();

		while(Frontier.size() > 0)
		{
			PuzzleState thisState = Frontier.pop();
			if(thisState.equals(aPuzzle.GoalState))
			{
				// Solution found.
				return thisState.GetPathToState();
			}
			if(!Searched.contains(thisState))
			{
				Searched.add(thisState);// state is discovered
				
				newStates = thisState.explore();// Get neighbours of state...
				for(int i = 0; i < newStates.size(); i++) // For each neighbour of thisState...
				{
					Frontier.push(newStates.get(i));
				}
			}
		}
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
