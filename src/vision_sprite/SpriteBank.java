package vision_sprite;

import genesis_util.Vector3D;
import arc_bank.Bank;
import arc_bank.BankBank;
import arc_bank.BankBankInitializer;
import arc_bank.BankObjectConstructor;
import arc_bank.MultiMediaHolder;
import arc_bank.ResourceInitializationException;

/**
 * SpriteBank is a static collection for Sprites and banks containing sprites.
 * 
 * @author Mikko Hilpinen
 * @since 5.12.2014
 */
public class SpriteBank
{
	// CONSTRUCTOR	-------------------------------
	
	private SpriteBank()
	{
		// The constructor is hidden since the interface is static
	}
	
	
	// OTHER METHODS	---------------------------
	
	/**
	 * Initializes the sprite resources. This should be called before the gamePhases have 
	 * been initialized.
	 * @param fileName The name of the file that contains sprite data ("data/" automatically 
	 * included). The file should have the following format:<br>
	 * &bankName1<br>
	 * spriteName1#fileName#imageNumber (optional)#originX (optional)#originY (optional)#
	 * width (optional)#height (optional)<br>
	 * spriteName2#...<br>
	 * ...<br>
	 * &bankName2<br>
	 * ...<br>
	 */
	public static void initializeSpriteResources(String fileName)
	{
		MultiMediaHolder.initializeResourceDatabase(createSpriteBankBank(fileName));
	}
	
	/**
	 * Creates a new bank system that handles all the sprites introduced in the given file
	 * @param fileName The name of the file that contains sprite data ("data/" automatically 
	 * included). The file should have the following format:<br>
	 * &bankName1<br>
	 * spriteName1#fileName#imageNumber (optional)#originX (optional)#originY (optional)#
	 * width (optional)#height (optional)<br>
	 * spriteName2#...<br>
	 * ...<br>
	 * &bankName2<br>
	 * ...<br>
	 * 
	 * @return A new Bank system containing all the introduced banks
	 */
	public static BankBank<Sprite> createSpriteBankBank(String fileName)
	{
		return new BankBank<>(new BankBankInitializer<>(fileName, new SpriteBankConstructor(), 
				new SpriteConstructor()), GraphicResourceType.SPRITE);
	}
	
	/**
	 * Finds and returns a spriteBank with the given name. The bank must be active in order 
	 * for this to work.
	 * @param bankName The name of the SpriteBank
	 * @return A spriteBank with the given name
	 */
	@SuppressWarnings("unchecked")
	public static Bank<Sprite> getSpriteBank(String bankName)
	{
		return (Bank<Sprite>) MultiMediaHolder.getBank(GraphicResourceType.SPRITE, bankName);
	}
	
	/**
	 * Finds and returns a sprite from the given sprite bank. The bank must be active.
	 * @param bankName The name of the bank that contains the sprite
	 * @param spriteName The name of the sprite in the bank
	 * @return A sprite with the given name from the given bank
	 */
	public static Sprite getSprite(String bankName, String spriteName)
	{
		return getSpriteBank(bankName).get(spriteName);
	}

	
	// SUBCLASSES	-------------------------------
	
	private static class SpriteBankConstructor implements BankObjectConstructor<Bank<Sprite>>
	{
		// IMPLEMENTED METHODS	-------------------
		
		@Override
		public Bank<Sprite> construct(String line, Bank<Bank<Sprite>> bank)
		{
			Bank<Sprite> newBank = new Bank<Sprite>();
			bank.put(line, newBank);
			return newBank;
		}
	}
	
	private static class SpriteConstructor implements BankObjectConstructor<Sprite>
	{
		// IMPLEMENTED METHODS	-------------------
		
		/*
		 * spritename#filename <i>(data/ is automatically included)</i>#image 
		 * number(optional)#originx(optional, -1 means center)#originy(optional, -1 means center)
		 * #forcedWidth(optional)#forcedHeight(optional)
		 * (non-Javadoc)
		 * @see arc_bank.BankObjectConstructor#construct(java.lang.String, arc_bank.Bank)
		 */
		@Override
		public Sprite construct(String line, Bank<Sprite> bank)
		{
			String[] arguments = line.split("#");
			
			// Checks that there are enough arguments
			if (arguments.length < 2)
				throw new ResourceInitializationException("Line " + line + 
						"doensn't have enough arguments");
			
			// Parses some of the arguments
			int imgnumber = 1;
			int originx = -1;
			int originy = -1;
			Vector3D forcedDimensions = null;
			
			try
			{
				if (arguments.length > 2)
					imgnumber = Integer.parseInt(arguments[2]);
				if (arguments.length > 3)
					originx = Integer.parseInt(arguments[3]);
				if (arguments.length > 4)
					originy = Integer.parseInt(arguments[4]);
				if (arguments.length > 6)
					forcedDimensions = new Vector3D(Integer.parseInt(arguments[5]), 
							Integer.parseInt(arguments[6]));
			}
			catch(NumberFormatException nfe)
			{
				throw new ResourceInitializationException(
						"Couldn't parse all arguments at line: " + line);
			}
			
			// Creates the sprite
			Sprite newSprite = new Sprite(arguments[1], imgnumber, new Vector3D(originx, 
					originy));
			if (forcedDimensions != null)
				newSprite = newSprite.withDimensions(forcedDimensions);
			
			// And adds it to the bank
			bank.put(arguments[0], newSprite);
			
			return newSprite;
		}
	}
}
