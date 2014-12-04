package vision_sprite;

import java.util.ArrayList;

import arc_bank.OpenBank;
import arc_bank.OpenBankHolder;
import arc_resource.Resource;

/**
 * This class holds numerous OpenSpriteBanks and provides them to the objects 
 * that need them. The holder loads the banks using a specific file
 *
 * @author Mikko Hilpinen & Unto Solala.
 * @since 26.8.2013.
 */
public class OpenSpriteBankHolder extends OpenBankHolder
{
	// CONSTRUCTOR	-----------------------------------------------------
	
	/**
	 * Creates and initializes new OpenSpriteBankHolder. The content is loaded 
	 * using the given file.
	 *
	 * @param filename A file that shows information about what banks to create 
	 * (data/ automatically included). 
	 * The file should be written as follows:<p>
	 * 
	 * &bankname<br>
	 * spritename#filename <i>(data/ is automatically included)</i>#image 
	 * number(optional)#originx(optional, -1 means center)#originy(optional, -1 means center)
	 * anotherspritename#...<br>
	 * ...<br>
	 * &anotherbankname<br>
	 * ...<br>
	 * * this is a comment
	 */
	public OpenSpriteBankHolder(String filename)
	{
		super(filename, true);
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------------------
	
	@Override
	protected OpenBank createBank(ArrayList<String> commands)
	{
		return new OpenSpriteBank(commands);
	}
	
	@Override
	public Resource getHeldResourceType()
	{
		return GraphicResource.SPRITE;
	}
	
	
	// OTHER METHODS	--------------------------------------------------

	/**
	 * Looks for the OpenBank matching the given bankName and if it is found,
	 * casts it into OpenSpriteBank and returns it. If not found, returns null.
	 * 
	 * @param bankName	The OpenBank which is needed.
	 * @return Returns the needed OpenBank, if it is found and casts it into
	 * OpenSpriteBank. If not found, return null.
	 */
	public OpenSpriteBank getOpenSpriteBank(String bankName)
	{
		OpenBank maybeOpenSpriteBank = getBank(bankName);
		
		if(maybeOpenSpriteBank instanceof OpenSpriteBank)
			return (OpenSpriteBank) maybeOpenSpriteBank;
		else
			return null;
	}
	
	
}
