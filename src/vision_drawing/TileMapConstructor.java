package vision_drawing;

import exodus_util.ConstructableHandled;
import flow_recording.AbstractConstructor;
import genesis_event.HandlerRelay;
import utopia.vision.generics.VisionDataType;

/**
 * TileMapConstructor is able to construct TileMaps
 * 
 * @author Mikko Hilpinen
 * @since 7.12.2014
 * @deprecated Replaced with {@link utopia.vision.resource.TileMap} and {@link VisionDataType} classes
 */
// TODO: Remove
public class TileMapConstructor extends AbstractConstructor<ConstructableHandled>
{
	// ATTRIBUTES	-------------------------------
	
	private HandlerRelay handlers;
	
	
	// CONSTRUCTOR	-------------------------------
	
	/**
	 * Creates a new tileMapConstructor
	 * @param handlers The handlers that will handle the constructed maps
	 */
	public TileMapConstructor(HandlerRelay handlers)
	{		
		this.handlers = handlers;
	}
	
	
	// IMPLEMENTED METHODS	-----------------------
	
	@Override
	protected TileMap createConstructable(String instruction)
	{
		return new TileMap(this.handlers);
	}
}
