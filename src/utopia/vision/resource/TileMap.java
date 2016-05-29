package utopia.vision.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import utopia.flow.structure.Pair;
import utopia.genesis.util.Vector3D;

/**
 * A tile map contains multiple tiles positioned in a way they form a map / surface
 * @author Mikko Hilpinen
 * @since 28.5.2016
 */
public class TileMap
{
	// TODO: Mutable or immutable?
	
	// ATTRIBUTES	---------------
	
	private List<Pair<Vector3D, Tile>> tiles = new ArrayList<>();
	
	
	// CONSTRUCTOR	---------------
	
	public TileMap(Collection<? extends Pair<Vector3D, Tile>> tiles)
	{
		for (Pair<Vector3D, Tile> tile : tiles)
		{
			// TODO: Add tiles
		}
	}
}
