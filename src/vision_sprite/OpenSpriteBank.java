package vision_sprite;

import java.util.ArrayList;

import arc_bank.MultiMediaHolder;
import arc_bank.OpenBank;


/**
 * This SpriteBank, unlike other SpriteBanks can be put into a bank. Also, 
 * the content of the SpriteBank can be defined by providing a number of 
 * commands upon the creation of the bank.
 *
 * @author Mikko Hilpinen.
 * @since 26.8.2013.
 */
public class OpenSpriteBank extends SpriteBank implements OpenBank
{
	// ATTRIBUTES	-----------------------------------------------------
	
	private ArrayList<String> commands;
	
	
	// CONSTRUCTOR	-----------------------------------------------------
	
	/**
	 * Creates a new OpenSpriteBank that will initialize itself using the 
	 * given information
	 *
	 * @param creationcommands Creation commands should follow the following 
	 * style:<br>
	 * spritename#filename <i>(data/ is automatically included)</i>#image 
	 * number(optional)#originx(optional, -1 means center)#originy(optional, -1 means center)
	 * #forcedWidth(optional)#forcedHeight(optional)
	 */
	public OpenSpriteBank(ArrayList<String> creationcommands)
	{
		// Initializes attributes
		this.commands = creationcommands;
	}
	
	
	// IMPLEMENTED METHODS	---------------------------------------------
	
	@Override
	protected void initialize()
	{
		// Creates the sprites by going through the commands
		for (int i = 0; i < this.commands.size(); i++)
		{
			String command = this.commands.get(i);
			String[] parts = command.split("#");
			
			// Checks that there are enough arguments
			if (parts.length < 2)
			{
				System.err.println("Couldn't load a sprite. Line " + command + 
						"doensn't have enough arguments");
				continue;
			}
			
			int imgnumber = 1;
			int originx = -1;
			int originy = -1;
			int width = 0;
			int height = 0;
			
			try
			{
				if (parts.length > 2)
					imgnumber = Integer.parseInt(parts[2]);
				if (parts.length > 3)
					originx = Integer.parseInt(parts[3]);
				if (parts.length > 4)
					originy = Integer.parseInt(parts[4]);
				if (parts.length > 6)
				{
					width = Integer.parseInt(parts[5]);
					height = Integer.parseInt(parts[6]);
				}
			}
			catch(NumberFormatException nfe)
			{
				System.err.println("Couldn't load a sprite. Line " + command 
						+ " contained invalid information.");
				continue;
			}
			
			createSprite(parts[1], imgnumber, originx, originy, parts[0], width, 
					height);
		}
	}
	
	
	// OTHER METHODS	-------------------------------------------------------
	
	/**
	 * Returns an SpriteBank if it has been initialized
	 *
	 * @param bankname The name of the needed bank
	 * @return The Sprite with the given name or null if no such bank exists 
	 * or if the bank is not active
	 */
	public static SpriteBank getSpriteBank(String bankname)
	{
		OpenBank maybespritebank = MultiMediaHolder.getBank(GraphicResourceType.SPRITE, bankname);
		
		if (maybespritebank instanceof SpriteBank)
			return (SpriteBank) maybespritebank;
		else
			return null;
	}
	
	/**
	 * Returns a sprite from any sprite bank that is currently active
	 * 
	 * @param bankName The name of the bank that holds the sprite
	 * @param spriteName The name of the sprite in the bank
	 * @return A sprite with the given name in the given bank
	 */
	public static Sprite getSprite(String bankName, String spriteName)
	{
		SpriteBank bank = getSpriteBank(bankName);
		
		if (bank == null)
			return null;
		
		return bank.getSprite(spriteName);
	}
}
