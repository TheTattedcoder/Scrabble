import java.util.*;

/**
 * Write a description of class Rack here.
 * 
 * TODO: For project 4, declare this so it implements the Comparable interface
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Rack
{
    // instance variables - replace the example below with your own
    private Tile[] tileOnRack;
    private ArrayList< Tile > tileOnRackList;
    // constant, single copy of Random object used for all rack objects
    private static final Random randomNumberGenerator = new Random();

    /**
     * Constructor for objects of class Rack
     */
    public Rack()
    {
        // initialise instance variables
        tileOnRack = new Tile[7];
        tileOnRackList = new ArrayList< Tile >(7);

        // fill rack with "null" tiles; will be populated with letter tiles later
        Arrays.fill( tileOnRack, 0,  7,  new Tile() );
    }

    /**
     * COPY constructor for objects of class Rack
     */
    public Rack( Rack originalRack )
    {
        // initialise instance variables
        tileOnRack = new Tile[7];

        // fill rack with "null" tiles; will be populated with letter tiles later
        for (int i = 0; i < 7; i++ ) {
            tileOnRack[i] = originalRack.getTileOnRack( i );
        }
    }
    
    /**
     * Enables access to individual tiles on the player's rack
     * TODO: For Project 4, update as needed for appropriate Collection class
     */
    public Tile getTileOnRack( int rackPosition ) {
        return tileOnRack[ rackPosition ];
    }
    
    /**
     * Assigns new value to tile on Rack
     * TODO: For Project 4, update as needed for appropriate Collection class
     */
    public void setTileOnRack( int rackPosition, Tile tile ) {  
        tileOnRack[ rackPosition ] = tile;
    } 
    
    /**
     * Sort tiles on rack (no built-in sorting method used)
     */
    public void sortTilesOnRack( boolean sortAscending )
    {        
        int insert; // temporary variable to hold element to insert

        // loop over (arraylength - 1) elements
        for (int next = 1; next < tileOnRack.length; next++)
        {
            // store value in current element
            Tile insertedTile = getTileOnRack( next );

            // initialize location to place element
            int moveItem = next;

            // search for place to put current element
            // Use the String method (s.compareTo(t) > 0) 
            while (moveItem > 0 
               && tileOnRack[ moveItem - 1 ].getLetter().compareTo( insertedTile.getLetter() ) > 0 )
            {
                // shift element right one slot
                setTileOnRack( moveItem, getTileOnRack( moveItem - 1 ) );
                moveItem--;
            } // end while

            setTileOnRack( moveItem , insertedTile ); // place inserted element
            
        } // end for
        
        if ( sortAscending == false ) 
        {
            // Descending sort; simply swap the first and last tiles and make your way "inward"
            swapTilesWithinRack( tileOnRack, 0, 6 );
            swapTilesWithinRack( tileOnRack, 1, 5 );
            swapTilesWithinRack( tileOnRack, 2, 4 );
        }
            
    } // end method sortTilesOnRack  
    
    /**
     * Randomly shuffle the positions of all tiles on the current player's rack. 
     * 
     * NOTE: Could also have used Collections.shuffle( Arrays.asList(a) ), but I chose
     *       instead to write out my own shuffle method here
     *  
     * TODO: For Project 4, replace this with Collections.shuffle( list ) if using an ArrayList
     *       or LinkedList object to represent the rack of tiles
     */
    public void shuffleTilesOnRack()
    {
        // pre-generate a random number
        randomNumberGenerator.nextInt();
        
        // Consider pre-sorting so that all null tiles are at the end
        int numTilesOnRack = tileOnRack.length;
        
        for (int fromHere = 0; fromHere < numTilesOnRack ; fromHere++) {

            // this guarantees 
            int toThere = fromHere + randomNumberGenerator.nextInt( numTilesOnRack - fromHere);
            
            swapTilesWithinRack( tileOnRack, fromHere, toThere );
        }

    }     
    
    public void collectionShuffleTilesOnRack()
    {
        Collections.shuffle(tileOnRackList);
    }
    
    /**
     * Helper method for swapping positions of Tiles within the current Player's Rack
     * Remember that the "value" being passed is a copy of the reference to the original
     * array; thus, any changes here will be reflected in the original array as well.
     */
    private void swapTilesWithinRack( Tile[] tileArray, int fromPosition, int toPosition ) 
    {
        Tile tempTile = tileArray[ fromPosition ];
        tileArray[ fromPosition ] = tileArray[ toPosition ];
        tileArray[ toPosition ] = tempTile;
    }
    
} // end class Rack
