import java.util.*;

/**
 * The TileBag class contains instructions needed for initializing the Tile objects used in the game
 * and also provides encapsulation methods (public get & set methods) for accessing the tiles stored in 
 * the tileBag object instantiated from this class
 * 
 * @author BCanada
 * @version 0
 */
public class TileBag
{
    // data field
    private Tile[] tileInBag;

    // instance variable
    private int numberOfBagTilesRemaining; 
    
    /**
     * Constructor for objects of class TileBag
     */
    public TileBag()
    {
        // initialise instance variables
        tileInBag = new Tile[100]; 
        
        numberOfBagTilesRemaining = 100;
        
        // Note the use of the Arrays class method "fill"
        // Much more efficient than a loop!!
        Arrays.fill( tileInBag, 0,  9,  new Tile( "A", 1 ) );
        Arrays.fill( tileInBag, 9,  11, new Tile( "B", 3 ) );
        Arrays.fill( tileInBag, 11, 13, new Tile( "C", 3 ) );
        Arrays.fill( tileInBag, 13, 17, new Tile( "D", 2 ) );
        Arrays.fill( tileInBag, 17, 29, new Tile( "E", 1 ) );
        Arrays.fill( tileInBag, 29, 31, new Tile( "F", 4 ) );
        Arrays.fill( tileInBag, 31, 34, new Tile( "G", 2 ) );
        Arrays.fill( tileInBag, 34, 36, new Tile( "H", 4 ) );
        Arrays.fill( tileInBag, 36, 45, new Tile( "I", 1 ) );
        Arrays.fill( tileInBag, 45, 46, new Tile( "J", 8 ) );  
        Arrays.fill( tileInBag, 46, 47, new Tile( "K", 5 ) );  
        Arrays.fill( tileInBag, 47, 51, new Tile( "L", 1 ) );
        Arrays.fill( tileInBag, 51, 53, new Tile( "M", 3 ) );
        Arrays.fill( tileInBag, 53, 59, new Tile( "N", 1 ) );
        Arrays.fill( tileInBag, 59, 67, new Tile( "O", 1 ) );
        Arrays.fill( tileInBag, 67, 69, new Tile( "P", 3 ) );
        Arrays.fill( tileInBag, 69, 70, new Tile( "Q", 10 ) );  
        Arrays.fill( tileInBag, 70, 76, new Tile( "R", 1 ) );
        Arrays.fill( tileInBag, 76, 80, new Tile( "S", 1 ) );
        Arrays.fill( tileInBag, 80, 86, new Tile( "T", 1 ) );
        Arrays.fill( tileInBag, 86, 90, new Tile( "U", 1 ) );
        Arrays.fill( tileInBag, 90, 92, new Tile( "V", 4 ) );
        Arrays.fill( tileInBag, 92, 94, new Tile( "W", 4 ) );
        Arrays.fill( tileInBag, 94, 95, new Tile( "X", 8 ) );  
        Arrays.fill( tileInBag, 95, 97, new Tile( "Y", 4 ) );
        Arrays.fill( tileInBag, 97, 98, new Tile( "Z", 10 ) );    
        Arrays.fill( tileInBag, 98, 100, new Tile( "_", 0 ) );   
    }
    
    /**
     * retrieves the tile in the TileBag object at the specified index 
     * (used in exchanging tiles between the tileBag and the rack)
     */
    public Tile getTileInBag( int index ) {
        return tileInBag[ index ];
    }

    /**
     * updates the tile in the TileBag object at the specified index 
     * (used in exchanging tiles between the tileBag and the rack)
     */
    public void setTileInBag( int index, Tile tile ) {
        tileInBag[index] = tile;
    }    
    
    /**
     * TODO: May wish to delete... at present I don't think this is used, but I'll hold onto it for now
     */
    public void printTilesInBag() {
        System.out.println( "Current tiles in tileBag: ");
        System.out.println( "------------------------- ");
        
        for ( int i = 0 ; i < 100 ; i++ ) {
            System.out.printf("tileBagIndex = %d, tileLetter = %s, tilePointValue = %d\n", 
                i, tileInBag[i].getLetter(), tileInBag[i].getPointValue() );
        }
        
    }

    /**
     * Counts the number of playable (non-null) tiles remaining in the tileBag object
     */
    public int getNumberOfTilesInBag() {

        int count = 0;
        
        for ( int i = 0 ; i < 100 ; i++ ) 
        {
            if ( tileInBag[i].getLetter() != null )
            {
                count++;
            }
        }
        
        return count;
    }
    
} // end class TileBag
