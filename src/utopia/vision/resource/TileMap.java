package utopia.vision.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import utopia.flow.structure.Pair;
import utopia.genesis.util.HelpMath;
import utopia.genesis.util.Vector3D;

/**
 * A tile map contains multiple tiles positioned in a way they form a map / surface
 * @author Mikko Hilpinen
 * @since 28.5.2016
 */
public class TileMap
{
	// ATTRIBUTES	---------------
	
	private List<Pair<Vector3D, Tile>> tiles = new ArrayList<>();
	
	
	// CONSTRUCTOR	---------------
	
	/**
	 * Creates a new tile map
	 * @param tiles The tiles in this map
	 */
	public TileMap(Collection<? extends Pair<Vector3D, Tile>> tiles)
	{
		this.tiles.addAll(tiles);
		
		// Sorts the tiles as well
		this.tiles.sort(new PositionComparator());
	}
	
	
	// ACCESSORS	----------------
	
	/**
	 * @return The tiles in this map. The returned list is a copy and changes made to it won't 
	 * affect the map
	 */
	public List<Pair<Vector3D, Tile>> getTiles()
	{
		return new ArrayList<>(this.tiles);
	}
	
	
	// OTHER METHODS	-----------
	
	/**
	 * Finds the tile that contains the provided point
	 * @param position a position in the map
	 * @return The tile at that position or null if that position is empty. Both the tile and 
	 * its position are returned.
	 */
	public Pair<Vector3D, Tile> getTileAt(Vector3D position)
	{
		// Goes through the list (from bottom right to top left) and checks possible 
		// containments
		for (Pair<Vector3D, Tile> tile : this.tiles)
		{
			if (tile.getFirst().getY() > position.getY() && 
					tile.getFirst().getX() < position.getX() && 
					HelpMath.pointIsInRange(position, tile.getFirst(), 
					tile.getFirst().plus(tile.getSecond().getSize())))
				return tile;
		}
		
		return null;
	}
	
	
	// NESTED CLASSES	-----------
	
	private static class PositionComparator implements Comparator<Pair<Vector3D, ?>>
	{
		@Override
		public int compare(Pair<Vector3D, ?> o1, Pair<Vector3D, ?> o2)
		{
			// The order is from bottom to top, from right to left
			// The y-axis is more defining
			double yDifference =  o2.getFirst().getY() - o1.getFirst().getY();
			
			if (HelpMath.areApproximatelyEqual(yDifference, 0))
				return (int) (o2.getFirst().getX() - o1.getFirst().getX());
			else
				return (int) yDifference;
		}	
	}
}
