import java.util.*;

/**
 * The Board is essentially an object consisting of a series of 2D arrays containing information
 * essential for "storage" and display of tiles placed on the board as well as the values of
 * premium square spaces on which the tiles can be placed.
 * 
 * @author BCanada 
 * @version 0
 */
public class Board
{
    // instance variables
    private int[][]     spaceType;
    private boolean[][] spacePlayed;
    private String[][]  spaceLetter; // for now, I'll use an underscore character for the blank tile 
    private int[][]     spaceValue; 

    private Tile[][] tileOnBoard;

    // TODO: For Project 4, consider converting to enumerated type (enum)
    // (I will definitely try to get to this if I can... it's easier than you think, 
    //  and enums can be quite handy!)
    public static final int NORMAL_SPACE  = 1;
    public static final int DOUBLE_LETTER = 2;
    public static final int DOUBLE_WORD   = 3;
    public static final int TRIPLE_LETTER = 4;
    public static final int TRIPLE_WORD   = 5;

    /**
     * Constructor for objects of class Board
     */
    public Board()
    {
        /* It's always good practice to initialize instance variables
         * even though you don't HAVE to (fields, unlike local variables,
         * do have initial values by default)
         * 
         * Remember that the purpose of a constructor is to initialize the object,
         * so this is probably the bes
         */

        // create the arrays
        spaceType   = new int[15][15];
        spacePlayed = new boolean[15][15];
        spaceLetter = new String[15][15];
        spaceValue  = new int[15][15];

        tileOnBoard = new Tile[15][15];

        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                
                // First, you have to assign a new tile object to each 
                // element in the tileOnBoard array. In calling the no-argument
                // constructor, all 225 boards tiles are set to be "null" or 
                // "inactive" tiles
                tileOnBoard[row][col] = new Tile(); 
            }
        }

        // TODO: Consider moving this to its own method -- something like "initializeBoard()" or something
        // We'll use a typical nested for-loop to initialize all the spaces
        // Technically you should only have to loop through the upper quadrant, right?
        for ( int row = 0; row < 8; row++ ) {
            for ( int col = 0; col < 8; col++ ) {

                // spacePlayed will be false when the game is initialized
                spacePlayed[  row   ][   col  ] = false;
                spacePlayed[14 - row][   col  ] = false;
                spacePlayed[   row  ][14 - col] = false;
                spacePlayed[14 - row][14 - col] = false;

                // Now we will have to assign specific values
                // to spaceType depending on the (row, col) position
                // 
                // NOTE: if we take advantage of "reflection symmetry" about the 4 "quadrants"
                //       of the game board, then we can reduce the number of lookups that we
                //       need to do by assigning the appropriate space multipli

                if ( (row == 0 && col == 0) || (row == 0 && col == 7) || (row == 7 && col == 0) )
                { 
                    spaceType[   row  ][   col  ] = TRIPLE_WORD;
                    spaceType[14 - row][   col  ] = TRIPLE_WORD;
                    spaceType[   row  ][14 - col] = TRIPLE_WORD;
                    spaceType[14 - row][14 - col] = TRIPLE_WORD;
                    
                    spaceLetter[  row   ][   col  ] = "3xW";
                    spaceLetter[14 - row][   col  ] = "3xW";
                    spaceLetter[   row  ][14 - col] = "3xW";
                    spaceLetter[14 - row][14 - col] = "3xW";                      
                } 
                else if ( (row > 0 && row < 5 ) && (row == col) ) 
                {
                    spaceType[   row  ][   col  ] = DOUBLE_WORD;
                    spaceType[14 - row][   col  ] = DOUBLE_WORD;
                    spaceType[   row  ][14 - col] = DOUBLE_WORD;
                    spaceType[14 - row][14 - col] = DOUBLE_WORD;
                    
                    spaceLetter[  row   ][   col  ] = "2xW";
                    spaceLetter[14 - row][   col  ] = "2xW";
                    spaceLetter[   row  ][14 - col] = "2xW";
                    spaceLetter[14 - row][14 - col] = "2xW";                      
                } 
                else if ( ( row == 0 && col == 3 ) || ( row == 3 && col == 0 ) ||
                ( row == 2 && col == 6 ) || ( row == 6 && col == 2 ) ||
                ( row == 3 && col == 7 ) || ( row == 7 && col == 3 ) ) 
                {
                    spaceType[   row  ][   col  ] = DOUBLE_LETTER;
                    spaceType[14 - row][   col  ] = DOUBLE_LETTER;
                    spaceType[   row  ][14 - col] = DOUBLE_LETTER;
                    spaceType[14 - row][14 - col] = DOUBLE_LETTER;
                    
                    spaceLetter[  row   ][   col  ] = "2xL";
                    spaceLetter[14 - row][   col  ] = "2xL";
                    spaceLetter[   row  ][14 - col] = "2xL";
                    spaceLetter[14 - row][14 - col] = "2xL";                    
                }
                else if ( ( row == 1 && col == 5 ) || ( row == 5 && col == 1 ) ||
                ( row == 6 && col == 6 ) )
                {
                    spaceType[   row  ][   col  ] = TRIPLE_LETTER;
                    spaceType[14 - row][   col  ] = TRIPLE_LETTER;
                    spaceType[   row  ][14 - col] = TRIPLE_LETTER;
                    spaceType[14 - row][14 - col] = TRIPLE_LETTER; 
                    
                    spaceLetter[  row   ][   col  ] = "3xL";
                    spaceLetter[14 - row][   col  ] = "3xL";
                    spaceLetter[   row  ][14 - col] = "3xL";
                    spaceLetter[14 - row][14 - col] = "3xL";
                }
                else 
                {
                    spaceType[   row  ][   col  ] = NORMAL_SPACE;
                    spaceType[14 - row][   col  ] = NORMAL_SPACE;
                    spaceType[   row  ][14 - col] = NORMAL_SPACE;
                    spaceType[14 - row][14 - col] = NORMAL_SPACE;
                    
                    spaceLetter[  row   ][   col  ] = "";
                    spaceLetter[14 - row][   col  ] = "";
                    spaceLetter[   row  ][14 - col] = "";
                    spaceLetter[14 - row][14 - col] = "";
                }
            }
        }

        // Lastly, initialize the center square (can be done outside the nested for loop
        spaceType[7][7] = DOUBLE_WORD;
        spaceLetter[7][7] = "2*W";
    }

    /**
     * This is a "copy constructor" specifically designed to make a COPY of the Board object
     * for "temporary use" during a given player's turn 
     * 
     * Notice that this is an OVERLOADED constructor; to use this constructor we simply call
     * 
     *     Board tempBoard = new Board ( nameOfBoardObjectToBeCopied );
     * 
     * 
     */
    public Board( Board originalBoard )  
    {
        spaceType   = new int[15][15];
        spacePlayed = new boolean[15][15];
        spaceLetter = new String[15][15];
        spaceValue  = new int[15][15];

        tileOnBoard = new Tile[15][15];

         for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {

                tileOnBoard[row][col] = originalBoard.tileOnBoard[row][col];
                
                spaceType[row][col]   = originalBoard.spaceType[row][col];
                spacePlayed[row][col] = originalBoard.spacePlayed[row][col];
                spaceLetter[row][col] = originalBoard.spaceLetter[row][col];
                spaceValue[row][col]  = originalBoard.spaceValue[row][col];
                
            }
        }
    }
    
    
    /**
     * From the CRC card for Board, obviously one of the Board's responsibilities has
     * to be to display the board itself!
     * 
     * We'll eventually implement a GUI-based approach, but for now we'll just stick with 
     * printing letters to the console (and maybe a VERY simplified GUI)
     * 
     */
    public void displayBoard()
    {
        // loop through array elements
        // print column headings
        System.out.println("     A   B   C   D   E   F   G   H   I   J   K   L   M   N   O");
        System.out.println("   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+");
        for ( int row = 0; row < 15; row++ ) {

            // print row number first (starting with 1?)
            System.out.printf( "%2d |" , row );

            for ( int col = 0; col < 15; col++ ) {
                if ( getSpaceLetter(row , col).length() == 3 ) 
                {
                    System.out.printf( "%3s|", getSpaceLetter(row , col) );
                } 
                else if ( getSpaceLetter(row , col).length() == 1 )
                {
                    System.out.printf( "[%s]|", getSpaceLetter(row , col) );
                }
                else 
                {
                    System.out.printf( "%2s |", getSpaceLetter(row , col) );
                }
                
            }

            // print newline character for current row
            System.out.println();
            
            // print row divider
            System.out.println("   +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+");    
            
        }
        
        System.out.println();
        
    }

    /**
     * Returns the visible letter played in the current space
     */
    public String getSpaceLetter(int rowIndex, int colIndex ) {      
        return spaceLetter[ rowIndex ][ colIndex ];
    }

    /**
     * Returns the visible point value of the tile played in the current space
     */
    public int getSpaceValue(int rowIndex, int colIndex ) {      
        return spaceValue[ rowIndex ][ colIndex ];
    }
    
    /**
     * Returns space types (what type of premium square) at the given location 
     * --> Needed for proper scoring of words played on the board <--
     * 
     */
    public int getSpaceType( int rowIndex, int colIndex) {  
        return spaceType[ rowIndex ][ colIndex ];
    }

    /**
     * Returns a boolean value indicating whether or not a tile has been played
     * on the board space given by the row and column coordinates
     */
    public boolean getSpacePlayed( int rowIndex, int colIndex  ) {  
        return spacePlayed[ rowIndex ][ colIndex ];
    }    
    
    /**
     * Sets the visible letter played in the current space
     */
    public void setSpaceLetter( String letter, int rowIndex, int colIndex ) {      
        spaceLetter[ rowIndex ][ colIndex ] = letter;
    }
    
    /**
     * Sets the "isPlayed" status in the current space to 'true' or 'false'
     */
    public void setSpacePlayed( boolean isPlayed, int rowIndex, int colIndex ) {  
        spacePlayed[ rowIndex ][ colIndex ] = isPlayed;
    }

    /**
     * Sets the space value at the given coordinates
     */
    public void setSpaceValue( int value, int rowIndex, int colIndex ) {  
        spaceValue[ rowIndex ][ colIndex ] = value;
    }   
    
    /**
     * Retrieves the tile on the board at the given row and column
     */
    public Tile getTileOnBoard( int rowIndex, int colIndex ) {
        return tileOnBoard[ rowIndex ][ colIndex ];
    }

    /**
     * Assigns the letter and point value of the specified tile to the visible board's 
     * spaceLetter and spaceValue at the given coordinates 
     */    
    public void setTileOnBoard( int rowIndex, int colIndex, Tile localTile ) {
        
        tileOnBoard[ rowIndex ][ colIndex ] = localTile;
        
        spaceLetter[ rowIndex ][ colIndex ] = tileOnBoard[ rowIndex ][ colIndex ].getLetter();
        spaceValue[ rowIndex ][ colIndex ]  = tileOnBoard[ rowIndex ][ colIndex ].getPointValue();
        
    } 
    
} // end class Board
