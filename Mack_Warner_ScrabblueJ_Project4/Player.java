import java.util.*;

/**
 * The Player class is responsible for taking a turn, manipulating tiles on the rack (sorting, shuffling), 
 * playing words by placing tiles on the board, exchanging tiles between the rack and the tileBag, and
 * passing the turn onto the next player.
 * 
 * Arguably, some of the methods in this class might belong in other classes... I may look into this later on.
 * 
 * @author BCanada
 * @version 0
 */
public class Player
{
    // instance variables

    private int playerNumber;
    private String playerName;
    private int playerScore;
    private int currentTurnScore; 
    private Game game;
    private Rack playerRack; 
    private Rack recallRack; // serves as a backup copy of the player rack in case tiles are recalled

    // IMPORTANT: The following are the player's "views" of the objects in question
    // It's not that the player "has" a board or "has" a tilebag, but the player does have
    // access to these objects, as implemented through the player's "view" of the objects
    private Board playerViewOfBoard;   // this is a reference to the current game's board object
    private TileBag playerViewOfTileBag; // this is a reference to the current game's tileBag object
    private Scoreboard playerViewOfScoreboard; // this is a reference to the current game's scoreboard object

    // tempBoard serves as a temporary place to store tiles placed by the player
    private Board tempBoard; 

    // constant, single copy of Scanner object used for all players
    private static final Scanner playerInput = new Scanner( System.in );

    // TODO: For project 4, implement both a stack and a queue as "history" data structures, as discussed in class
    private Stack<Tile> currentTilesPlaced = new Stack<Tile>();
    private Stack<Integer> currentTilesPlacedRow = new Stack<Integer>();
    private Stack<Integer> currentTilesPlacedCol = new Stack<Integer>();
    private Stack<Integer> currentTilesPlacedRackPos = new Stack<Integer>();

    /**
     * Constructor for objects of class Player
     */
    public Player( int playerNumber, String playerName, Board playerViewOfBoard, TileBag playerViewOfTileBag, Scoreboard playerViewOfScoreboard )
    {
        // initialise instance variables
        this.playerNumber        = playerNumber; 
        this.playerName          = playerName; 

        // create the player's view of (reference to) the gameBoard and the gameTileBag arrays
        this.playerViewOfBoard      = playerViewOfBoard;
        this.playerViewOfTileBag    = playerViewOfTileBag;
        this.playerViewOfScoreboard = playerViewOfScoreboard; 

        // initialize tile Rack for this player
        playerRack = new Rack();
        setupRack();

        // initialize player score
        playerScore = 0;

    }

    /**
     * NOT USED: no-arg constructor for Player. Necessary? Probably not, but I'll leave it here just in case.
     */
    public Player()
    {
        // not used
    }

    /**
     * Start turn for the current player
     */
    public void startTurn()
    {
        // reset instance variable
        currentTurnScore = 0;

        // initialize local variables
        String choice = null;
        int turnStatus = 0;

        do {
            Game.clearScreen();
            playerViewOfBoard.displayBoard();
            playerViewOfScoreboard.displayScores();
            displayRack();        
            displayMainPlayerMenu();

            // TODO: For Project 4, implement basic exception handling
            //       to handle instances when the user doesn't type the right character(s)
            do{
                try{
                    choice = playerInput.nextLine();
                    if( !choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("4") 
                    && !choice.equals("5") && !choice.equals("Q"))
                    {
                        System.out.println( "Input valid selection");
                        continue;
                    }
                }catch(Exception e){
                    playerInput.next();
                    continue;
                }
            }while(!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("4") 
            && !choice.equals("5") && !choice.equals("Q"));

            System.out.print("\nYou have selected ");
            switch( choice )
            {
                case "1":
                System.out.println( "1) Start placing letters on board" );
                turnStatus = playWord();
                break;
                case "2":
                System.out.println( "2) Sort tiles on rack A -> Z" );
                playerRack.sortTilesOnRack( true );
                break;
                case "3":
                System.out.println( "3) Sort tiles on rack Z -> A" );
                playerRack.sortTilesOnRack( false );
                break;
                case "4":
                System.out.println( "4) Randomly shuffle tiles" );
                playerRack.shuffleTilesOnRack();
                break;
                case "5":
                if ( playerViewOfTileBag.getNumberOfTilesInBag() > 0 ) {
                    System.out.println( "5) Exchange tiles & pass turn" );
                } else {
                    System.out.println( "5) Pass turn" );
                }
                passTurn();
                break;
                case "Q":
                System.out.println( "Q) Quit  " );
                // call method
                confirmQuit();
                break;
            }  

            // return turnStatus from playWord()method:
            // ----------------------------------------
            // turnStatus 0 means allow turn to expire normally (points are awarded)
            // turnStatus 1 means forfeit turn (no points awarded b/c of successful challenge or failed dictionary lookup)
            // turnStatus 2 repeat main menu for current player (no points are awarded because tiles were recalled)           
            // turnStatus 3 means nothing left to do, so end the game and compute final scores 
            if ( turnStatus == 1 ) {
                // artificially reset choice 
                choice = "0"; 
                currentTurnScore = 0;
            }
            else if ( turnStatus == 2 ) 
            {
                currentTurnScore = 0;
            }
            else if ( turnStatus == 0 ) 
            {
                // award points normally
                updatePlayerScore( currentTurnScore );
            }
            else if ( turnStatus == 3 ) 
            {
                // award points normally, but then call finalScoring method
                // which will go around and add up
                updatePlayerScore( currentTurnScore );

                // pass playerNumber as the "first player to finish" to the computeFinalScores method
                playerViewOfScoreboard.computeFinalScores( getPlayerNumber() );

                // OK *now* end the game
                Game.end();
            }

        } while ( !choice.equals("1") && !choice.equals("5") );
    }

    /**
     * Displays the current options available to the player at the start of his/her turn.
     */
    public void displayMainPlayerMenu() 
    {
        System.out.println();
        System.out.println( "+====================================+ " );
        System.out.println( "| MAIN PLAYER MENU                   | " );
        System.out.println( "| 1) Start placing letters on board  | " );
        System.out.println( "| 2) Sort tiles on rack A -> Z       | " );
        System.out.println( "| 3) Sort tiles on rack Z -> A       |  It is currently " );
        System.out.println( "| 4) Randomly shuffle tiles          |  Player " + playerNumber + "'s turn" );
        if ( playerViewOfTileBag.getNumberOfTilesInBag() > 0 ) {
            System.out.println( "| 5) Exchange tiles & pass turn      | " );
        } else {
            System.out.println( "| 5) Pass turn (no tiles left)       | " );
        }
        System.out.println( "| Q) Quit                            | " );
        System.out.println( "+====================================+ " );
        System.out.println();
        System.out.print( "Type your choice (1-5 or Q) and then press ENTER: " );

    }   

    public void printStacks( Stack<Tile> s1, Stack<Integer> s2, Stack<Integer> s3, Stack<Integer> s4 )
    {
        // isEmpty -- built-in method
        if ( s1.isEmpty() )
        {
            System.out.println( "No tiles have been placed yet." );
        }
        else
        {
            //System.out.printf("Tile: %s\t BoardRow: %s\t BoardCol: %s\t RackPos: %s\n", s);
            System.out.println( s1 );
            System.out.println( s2 );
            System.out.println( s3 );
            System.out.println( s4 );

        }
    }

    /**
     * Method for enabling player to place tiles and form words... the ESSENCE of the game, amirite?!?
     */
    public int playWord()
    {
        int recallStatus = 0; 

        //do {
        // Copy the current player's rack into the recallRack in case the player needs to 
        // recall all tiles following an illegal move or successful challenge by another player
        // (note that this uses the copy constructor for the Rack class)
        recallRack = new Rack( playerRack );

        // declare local variables
        int selectedRackTileIndex = 0;
        int selectedColIndex = 0;
        int selectedRowIndex = 0;

        boolean moveIsLegal = true;    

        int numberOfTilesPlayed = 0;

        // need to check to see how many tiles can actually be played
        // (this is part of the endgame scenario)
        int numberOfNonNullTilesAvailable = 7;

        for ( int i = 0; i < 7; i++ ) {

            if ( playerRack.getTileOnRack( selectedRackTileIndex ).getLetter() == null ) {
                numberOfNonNullTilesAvailable--;
            }

        }

        // We can use a temporary copy of the CURRENT game board to keep track of the tiles 
        // that are played as well as all words formed during the current player's move               
        tempBoard = new Board( playerViewOfBoard ); 

        // for keeping track of the positions of the last two tiles placed
        // (this is used for inferring the "direction" of tile placement -- no zig-zag moves allowed)
        int[][] placedTilePosition = { { 0, 0 }, 
                { 0, 0 }, 
                { 0, 0 }, 
                { 0, 0 },                                         
                { 0, 0 }, 
                { 0, 0 }, 
                { 0, 0 } };

        do {          
            Game.clearScreen(); 
            tempBoard.displayBoard(); 
            playerViewOfScoreboard.displayScores();
            displayRack(); 

            System.out.println("History of tiles placed during current turn:");
            printStacks(currentTilesPlaced, currentTilesPlacedRow, currentTilesPlacedCol,
                currentTilesPlacedRackPos);

            String tempLine = "";

            do {
                System.out.println();
                System.out.println( "Enter the rack position (0-6) of the tile you want to place, or enter -1 if finished, " );
                System.out.print( "or you may enter -2 to recall ALL tiles and return to the main menu: " );

                // TODO: For project 4, implement exception handling to ensure that the proper data type is entered
                //       ( here, you would check for an InputMismatchException )
                do{
                    try{
                        selectedRackTileIndex = playerInput.nextInt();
                        if(selectedRackTileIndex != 0 && selectedRackTileIndex != 1 && selectedRackTileIndex != 2 && selectedRackTileIndex != 3 && 
                        selectedRackTileIndex != 4 && selectedRackTileIndex != 5 && selectedRackTileIndex != 6
                        && selectedRackTileIndex != -1 && selectedRackTileIndex != -2)
                        {
                            System.out.println( "Input valid data type");
                            continue;
                        }
                    }catch(Exception e){
                        playerInput.next();
                        continue;
                    }
                }while(selectedRackTileIndex != 0 && selectedRackTileIndex != 1 && selectedRackTileIndex != 2 && selectedRackTileIndex != 3 && 
                selectedRackTileIndex != 4 && selectedRackTileIndex != 5 && selectedRackTileIndex != 6
                && selectedRackTileIndex != -1 && selectedRackTileIndex != -2);

                if ( selectedRackTileIndex == -1  || selectedRackTileIndex == -2 ) {
                    break; // as in break out of the inner do...while loop
                }

                // If selectedRackTileIndex is out of bounds, loop until the player enters a valid rack position
                while ( ( ( selectedRackTileIndex < -2 ) || ( selectedRackTileIndex > 6 ) ) 
                ||
                ( playerRack.getTileOnRack( selectedRackTileIndex ).getLetter() == null ) )
                {
                    System.out.println( "You have entered an invalid or empty rack position -- Please try again.");
                    System.out.println( "Enter the rack position (0-6) of the tile you want to place, or enter -1 if finished, " );
                    System.out.print( "or you may enter -2 to recall ALL tiles and return to the main menu: " );
                    selectedRackTileIndex = playerInput.nextInt();
                }        

                if ( playerRack.getTileOnRack( selectedRackTileIndex ).getLetter().equals("_") ) {
                    String tempblankLetter = "_";
                    System.out.println( "\nWhat letter would you like to choose for your blank tile?" );
                    System.out.print( "\nPlease type a single *lowercase* letter from a to z, followed by the ENTER key: " );

                    // TODO: Implement exception handling here to ensure a lowercase letter is entered
                    playerInput.nextLine(); // <-- for whatever reason, this extra nextLine() method call is needed (to clear out the buffer?)
                    do{
                        try{
                            tempblankLetter = playerInput.nextLine();
                            tempblankLetter.toLowerCase();
                        }catch(Exception e){
                            playerInput.next();
                            continue;
                        }
                    }while(tempblankLetter == playerInput.nextLine());

                    playerRack.getTileOnRack( selectedRackTileIndex ).setLetter( tempblankLetter );

                }

                System.out.println( "\nYou have selected the following tile:\n" );
                System.out.println( "+---+" );
                System.out.printf( "| %s |\n", playerRack.getTileOnRack( selectedRackTileIndex ).getLetter() );
                System.out.printf( "| %2s|\n", playerRack.getTileOnRack( selectedRackTileIndex ).getPointValue() );
                System.out.println( "+---+\n" );

                System.out.println( "If this is not the tile you wanted to place, please enter X to choose another tile." );
                System.out.println( "Otherwise, to place the tile on the board, enter the column LETTER and row NUMBER,"  );
                System.out.print( "separated by a space (e.g. as in 'A 0' or 'F 7'): ");

                // TODO: For project 4, implement exception handling!        
                // converts the letter char to an int and substracts 65 to get the proper index value
                // e.g., (int)("A".charAt(0)) = 65  
                tempLine = playerInput.next();

                //System.out.println( "tempLine = " + tempLine );

            } while ( tempLine.equals("X") );

            // now that we're outside of the do...while loop, we can place the tile or exit the outer do...while loop entirely
            if ( selectedRackTileIndex < 0 ) {
                continue; // as in skip to the end of the current iteration (though we may as well break I suppose)
            }
            //else if ( selectedRackTileIndex == -2 ) {
            //    break; // break out of the current loop
            //}

            selectedColIndex = (int)( tempLine.charAt(0) ) - 65;
            selectedRowIndex = playerInput.nextInt(); // TODO: may want to subtract 1, but can deal with this in later version of the game

            // Check to make sure the tile isn't already placed on another previously placed tile
            // (placed BEFORE OR DURING the current player's turn)
            if ( Game.checkForLegalMove( tempBoard, selectedRowIndex, selectedColIndex, numberOfTilesPlayed ) ) 
            {
                placeTileOnBoard( selectedRackTileIndex, selectedRowIndex, selectedColIndex );
                tempBoard.setSpacePlayed( true , selectedRowIndex, selectedColIndex );

                // TODO: For project 4, consider making this a STACK of tile positions (like a "tile placement history file")
                placedTilePosition[ numberOfTilesPlayed ][ 0 ] = selectedRowIndex;
                placedTilePosition[ numberOfTilesPlayed ][ 1 ] = selectedColIndex;

                numberOfTilesPlayed++;
            }
            else 
            {
                // TODO: For project 4, consider re-implementing this using exception handling 
                //       (i.e., define a "custom" exception with special error message)

                System.err.println( "\nBased on the tiles currently on the board, you can't place a tile at that position." );
                System.err.println( "Make sure all tiles placed during this turn are placed linearly (in the same row or column) ");
                System.err.println( "and also make sure you are not attempting to 'over-write' tiles already placed on the board." );
                System.out.println();
                System.out.println( "Please press ENTER and try placing your tile again." );
                playerInput.nextLine();
                playerInput.nextLine(); // not sure why, but TWO calls to nextLine are needed for this to work (maybe to clear the buffer?)
            }

        } while ( selectedRackTileIndex > -1 ); // i.e., continue the loop as long as valid tiles are being selected 

        // TODO: For project 4, implement the tile recall such that the player can recall
        //       one tile at a time instead of all at once.
        if ( selectedRackTileIndex == -2 ) {
            System.out.println( "Recalling tiles to rack and returning to main menu. Please press ENTER to continue." );
            playerInput.nextLine();
            playerInput.nextLine();
            playerRack = new Rack( recallRack );
            recallStatus = 1; // exit playWord(), but continue player turn by returning to the main menu
            return recallStatus;
        }

        // check tile placement preconditions (see method's Javadoc comment for details)
        // otherwise, if that test "passes," then attempt to score all words
        if ( false == checkTilePlacementPreconditions( placedTilePosition, numberOfTilesPlayed ) ) {
            System.out.println( "Word placement violated game rules. Recalling tiles to rack and returning to main menu..." );
            System.out.println( "Please press ENTER to continue." );
            playerInput.nextLine();
            playerInput.nextLine();
            playerRack = new Rack( recallRack );
            recallStatus = 1; // exit playWord(), but continue player turn by returning to the main menu
        }
        else if ( false == scoreAllWords( placedTilePosition, numberOfTilesPlayed ) ) {
            System.out.println( "Word is either not in dictionary or it was successfully challenged.");
            System.out.println( "Recalling tiles to rack and forfeiting turn. Please press ENTER to continue." );
            playerInput.nextLine();
            playerInput.nextLine();
            playerRack = new Rack( recallRack );
            recallStatus = 2; // forfeit turn
        }
        else {

            System.out.println( "\nPress ENTER to continue." );
            playerInput.nextLine(); 
            playerInput.nextLine();     

            // prepare for endgame if all letters have been drawn from the tile bag
            // AND one of the players has used up his or her last tile! 
            if ( ( numberOfTilesPlayed == numberOfNonNullTilesAvailable ) 
            && 
            ( playerViewOfTileBag.getNumberOfTilesInBag() == 0 ) ) 
            {
                recallStatus = 3; // END THE GAME ALREADY!!!
            } 
            else 
            {
                recallStatus = 0; // complete turn normally

                // Assume that, by now, the current move is legal, and we can now "commit" the tempBoard tiles
                // to the actual game board object (i.e., playerViewOfBoard now incorporates tempBoard)
                updateGameBoard();

                // retrieve tiles from the tileBag to refill the current player's rack, 
                // as long as enough tiles are left in the bag (see method for details)
                refillRack();

                if ( Game.getFirstMoveOfGameStatus() )  
                {
                    Game.setFirstMoveOfGameStatus( false );
                }
            }

        } // end multi-way if/else statement

        return recallStatus;
    } // end method playWord

    /**
     * retrieves the current player's name
     */
    public String getPlayerName()
    {
        return playerName;
    }

    /**
     * retrieves the current player's number
     */
    public int getPlayerNumber()
    {
        return playerNumber;
    }

    /**
     * Retrieves the current player's score
     */
    public int getPlayerScore() {
        return playerScore;
    }

    /**
     * Alters the current player's score by the given amount at the end of the player's turn
     */
    public void updatePlayerScore( int finalTurnScore ) {
        playerScore += finalTurnScore;
    }

    /**
     * Increment the current player's POTENTIAL score for the current turn
     * (points will be negated if turn is forfeited or tiles are otherwise recalled)
     */
    public void updateCurrentTurnScore( int scoreIncrement ) {
        currentTurnScore += scoreIncrement;
    }

    /**
     * A "helper" method that returns FALSE:
     * 
     * 1) ...if it is the FIRST move of the game and the center space is NOT occupied 
     * 2) ...if it is NOT the first move of the game and none of the currently placed tiles
     *    are horizontally or vertically adjacent to a previously played tile (i.e., according to playerViewOfBoard)
     * 
     * If the method returns FALSE, then the calling method will display an error message, 
     * recall all played tiles to the rack, and restore the board back to the last playerViewOfBoard
     * 
     */
    private boolean checkTilePlacementPreconditions( int[][] placedTilePosition, int numberOfTilesPlayed )
    {
        boolean status = false;

        for ( int i = 0 ;  i < numberOfTilesPlayed ; i++ ) 
        {
            int startRow = placedTilePosition[i][0];
            int startCol = placedTilePosition[i][1];

            if ( Game.getFirstMoveOfGameStatus() ) {

                // check to see if any tile has been placed on the "star" space
                // (the center space)
                if ( startRow == 7 && startCol == 7 ) {
                    status = true;
                }

            } 
            else // otherwise, assume that this is NOT the first move of the game...
            {
                // ...and that we have to check for adjacent tiles from previous turns,
                //    making sure we stay within the bounds of the board!
                if ( (startRow < 14 && playerViewOfBoard.getSpacePlayed(startRow + 1, startCol))
                ||
                (startRow > 0  && playerViewOfBoard.getSpacePlayed(startRow - 1, startCol)) 
                ||
                (startCol < 14 && playerViewOfBoard.getSpacePlayed(startRow, startCol + 1)) 
                ||
                (startCol > 0  && playerViewOfBoard.getSpacePlayed(startRow, startCol - 1)) ) 
                {
                    status = true;
                }

            } // end if-else statement

        } // end for loop

        return status;
    } // end method checkTilePlacementPreconditions

    /**
     * A "helper" method (hence private visibility) for checking and scoring words played during the current turn
     * 
     * NOTE: this method returns FALSE if any word is not in the dictionary or is otherwise challenged
     *       successfully by another player
     *       
     * TODO: This method is rather long -- consider breaking this up into *at least two* methods.
     * 
     */
    private boolean scoreAllWords( int[][] placedTilePosition, int numberOfTilesPlayed ) 
    {                
        boolean status = false;

        int numWordsPlayedThisTurn = 0;
        int potentialBingoBonus = 50;

        // A maximum of EIGHT (8) words of length >= 2 can be formed on any given player's turn
        String[] wordPlayed         = new String[8];
        int[]    wordPlayedScore    = new int[8];
        int[]    wordPlayedStartRow = new int[8];
        int[]    wordPlayedEndRow   = new int[8];
        int[]    wordPlayedStartCol = new int[8];
        int[]    wordPlayedEndCol   = new int[8];

        for ( int i = 0 ;  i < numberOfTilesPlayed ; i++ ) 
        {

            int startRow = placedTilePosition[i][0];
            int startCol = placedTilePosition[i][1];

            // check for horizontal word (must be at least 2 chars long)
            int currentHorizontalWordRow      = startRow;
            int currentHorizontalWordFirstCol = startCol;

            // look for tiles to the left until you reach a null tile OR the left edge of the board
            // (Note that getSpacePlayed returns true or false)
            while ( currentHorizontalWordFirstCol >= 0 && 
            tempBoard.getSpacePlayed( currentHorizontalWordRow, currentHorizontalWordFirstCol ) ) {
                currentHorizontalWordFirstCol--;
            }

            // reset starting column position one to the right and then 
            // temporarily set the ending column position to the starting column position
            int currentHorizontalWordLastCol = ++currentHorizontalWordFirstCol;

            // look for tiles to the right until you reach a null tile OR the left edge of the board
            // (Note that getSpacePlayed returns true or false)
            while ( currentHorizontalWordLastCol < 15 && 
            tempBoard.getSpacePlayed( currentHorizontalWordRow, currentHorizontalWordLastCol ) ) {
                currentHorizontalWordLastCol++;
            }

            // reset ending column position one to the left
            currentHorizontalWordLastCol--;

            // Now create a tempString (StringBuilder class) and then build the current horizontal word 
            // ** For information on StringBuilder please see http://docs.oracle.com/javase/7/docs/api/java/lang/StringBuilder.html
            // ** also see http://stackoverflow.com/questions/4645020/when-to-use-stringbuilder-in-java
            StringBuilder tempHorizontalWord = new StringBuilder("");
            int tempHorizWordScore = 0;
            int tempHorizLetterScoreMultiplier = 1;
            int tempHorizWordScoreMultiplier = 1;

            for ( int col = currentHorizontalWordFirstCol; col <= currentHorizontalWordLastCol; col++ ) {

                // if the space being checked had not been previously played (according to playerViewOfBoard, NOT tempBoard )
                // then we can use its premium square type, if any
                if ( !playerViewOfBoard.getSpacePlayed( currentHorizontalWordRow, col ) ) {

                    switch ( playerViewOfBoard.getSpaceType( currentHorizontalWordRow, col ) ) 
                    {
                        case Board.NORMAL_SPACE: 
                        tempHorizLetterScoreMultiplier = 1;
                        break;
                        case Board.DOUBLE_LETTER: 
                        tempHorizLetterScoreMultiplier = 2;
                        break;
                        case Board.DOUBLE_WORD: 
                        tempHorizWordScoreMultiplier *= 2; // multiply in case the word crosses both DW and TW premium squares
                        break;
                        case Board.TRIPLE_LETTER: 
                        tempHorizLetterScoreMultiplier = 3;
                        break;
                        case Board.TRIPLE_WORD: 
                        tempHorizWordScoreMultiplier *= 3; // multiply in case the word crosses both DW and TW premium squares
                        break;
                    } // end switch

                } // end if  

                tempHorizontalWord.append( tempBoard.getSpaceLetter( currentHorizontalWordRow, col ) );

                // add this letter's contribution to the current word score
                tempHorizWordScore += ( tempHorizLetterScoreMultiplier * tempBoard.getSpaceValue( currentHorizontalWordRow, col ) );

            } // next horizontal col

            // multiply current word score by final multiplier value
            tempHorizWordScore *= tempHorizWordScoreMultiplier;

            // Now check for vertical word (must be at least 2 chars long)
            int currentVerticalWordCol      = startCol;
            int currentVerticalWordFirstRow = startRow;

            // look for tiles above until you reach a null tile OR the top edge of the board
            // (Note that getSpacePlayed returns true or false)
            while ( currentVerticalWordFirstRow >= 0 && 
            tempBoard.getSpacePlayed( currentVerticalWordFirstRow, currentVerticalWordCol ) ) {
                currentVerticalWordFirstRow--;
            }

            // reset starting row position one to the below and then 
            // temporarily set the ending row position to the starting row position
            int currentVerticalWordLastRow = ++currentVerticalWordFirstRow;

            while ( currentVerticalWordLastRow < 15 && 
            tempBoard.getSpacePlayed( currentVerticalWordLastRow, currentVerticalWordCol ) ) 
            {
                currentVerticalWordLastRow++;
            }

            // reset ending column position one to the left
            currentVerticalWordLastRow--;

            // Now create a tempString (StringBuilder class) and then build the current vertical word 
            // ** For information on StringBuilder please see http://docs.oracle.com/javase/7/docs/api/java/lang/StringBuilder.html
            // ** also see http://stackoverflow.com/questions/4645020/when-to-use-stringbuilder-in-java
            StringBuilder tempVerticalWord = new StringBuilder("");
            int tempVertWordScore = 0;
            int tempVertLetterScoreMultiplier = 1;
            int tempVertWordScoreMultiplier = 1;

            for ( int row = currentVerticalWordFirstRow; row <= currentVerticalWordLastRow; row++ ) {

                // if the space being checked had not been previously played (according to playerViewOfBoard, NOT tempBoard )
                // then we can use its premium square type, if any
                if (!playerViewOfBoard.getSpacePlayed( row, currentVerticalWordCol ) ) {

                    switch ( playerViewOfBoard.getSpaceType( row, currentVerticalWordCol ) ) 
                    {
                        case Board.NORMAL_SPACE: 
                        tempVertLetterScoreMultiplier = 1;
                        break;
                        case Board.DOUBLE_LETTER: 
                        tempVertLetterScoreMultiplier = 2;
                        break;
                        case Board.DOUBLE_WORD: 
                        tempVertWordScoreMultiplier *= 2; // multiply in case the word crosses both DW and TW premium squares
                        break;
                        case Board.TRIPLE_LETTER: 
                        tempVertLetterScoreMultiplier = 3;
                        break;
                        case Board.TRIPLE_WORD: 
                        tempVertWordScoreMultiplier *= 3; // multiply in case the word crosses both DW and TW premium squares
                        break;
                    } // end switch

                } // end if  

                tempVerticalWord.append( tempBoard.getSpaceLetter( row, currentVerticalWordCol ) );

                // add this letter's contribution to the current word score
                tempVertWordScore += ( tempVertLetterScoreMultiplier * tempBoard.getSpaceValue( row, currentVerticalWordCol ) );

            } // next horizontal col

            // multiply current word score by final multiplier value
            tempVertWordScore *= tempVertWordScoreMultiplier;        

            // assuming all words are in the dictionary, now check to see if they
            // have already been accounted for by an earlier "pass" through the tiles
            if ( tempHorizontalWord.length() >= 2 ) {

                // check to see if tempHorizontalWord has already been accounted for during the current player's turn
                boolean wordAlreadyAccountedFor = false;  

                // TODO: diagnostic messages to be deleted
                //System.out.println("numWordsPlayedThisTurn = " + numWordsPlayedThisTurn );

                if ( numWordsPlayedThisTurn > 0 ) {
                    for ( int wordPlayedCounter = 0; wordPlayedCounter < numWordsPlayedThisTurn; wordPlayedCounter++ ) {

                        if ( ( wordPlayed[ wordPlayedCounter ].equals( tempHorizontalWord.toString() ) )
                        &&
                        ( wordPlayedStartRow[ wordPlayedCounter ] == currentHorizontalWordRow ) &&
                        ( wordPlayedEndRow[ wordPlayedCounter ]   == currentHorizontalWordRow ) &&
                        ( wordPlayedStartCol[ wordPlayedCounter ] == currentHorizontalWordFirstCol ) &&
                        ( wordPlayedEndCol[ wordPlayedCounter ]   == currentHorizontalWordLastCol  ) ) 
                        {
                            wordAlreadyAccountedFor = true;
                        }
                    }
                }

                if ( !wordAlreadyAccountedFor ) {

                    // check to see if word is in the dictionary; if not, go ahead and return false
                    if ( !inDictionary( tempHorizontalWord.toString() ) ) {
                        System.out.println( tempHorizontalWord + " is not in the dictionary!" );
                        return false;
                    }

                    System.out.printf( "Horizontal word played: %s \n", tempHorizontalWord );
                    System.out.printf( "Score: %d \n", tempHorizWordScore );

                    // record the actual word -- use the toString() method to convert from StringBuilder to String!
                    wordPlayed[numWordsPlayedThisTurn]         = tempHorizontalWord.toString();

                    // record the played word's score
                    wordPlayedScore[numWordsPlayedThisTurn]    = tempHorizWordScore;

                    // record the played word's "coordinates"
                    wordPlayedStartRow[numWordsPlayedThisTurn] = currentHorizontalWordRow;
                    wordPlayedEndRow[numWordsPlayedThisTurn]   = currentHorizontalWordRow;
                    wordPlayedStartCol[numWordsPlayedThisTurn] = currentHorizontalWordFirstCol;
                    wordPlayedEndCol[numWordsPlayedThisTurn]   = currentHorizontalWordLastCol;

                    // increment the number of words played
                    numWordsPlayedThisTurn++;

                    // update potential score for current turn
                    updateCurrentTurnScore( tempHorizWordScore );

                    // set return status to true if all words are OK
                    status = true;

                    // TODO: diagnostic messages to be deleted

                }

            } // end if ( tempHorizontalWord.length() >= 2 )

            // Now check vertical word
            if ( tempVerticalWord.length() >= 2 ) {

                // check to see if tempHorizontalWord has already been accounted for during the current player's turn
                boolean wordAlreadyAccountedFor = false;  

                if ( numWordsPlayedThisTurn > 0 ) {
                    for ( int wordPlayedCounter = 0; wordPlayedCounter < numWordsPlayedThisTurn; wordPlayedCounter++ ) {

                        if ( ( wordPlayed[ wordPlayedCounter ].equals( tempVerticalWord.toString() ) )
                        &&
                        ( wordPlayedStartRow[ wordPlayedCounter ] == currentVerticalWordCol ) &&
                        ( wordPlayedEndRow[ wordPlayedCounter ]   == currentVerticalWordCol ) &&
                        ( wordPlayedStartCol[ wordPlayedCounter ] == currentVerticalWordFirstRow ) &&
                        ( wordPlayedEndCol[ wordPlayedCounter ]   == currentVerticalWordLastRow  ) ) 
                        {
                            wordAlreadyAccountedFor = true;
                        }
                    }
                }

                if ( !wordAlreadyAccountedFor ) {

                    // check to see if word is in the dictionary; if not, go ahead and return false
                    if ( !inDictionary( tempVerticalWord.toString() ) ) {
                        //System.out.println( tempVerticalWord + " is  not in the dictionary " );
                        return false;
                    }

                    System.out.printf( "Vertical word played: %s \n", tempVerticalWord );
                    System.out.printf( "Score: %d \n", tempVertWordScore );

                    // record the actual word -- use the toString() method to convert from StringBuilder to String!
                    wordPlayed[numWordsPlayedThisTurn]         = tempVerticalWord.toString();

                    // record the played word's score
                    wordPlayedScore[numWordsPlayedThisTurn]    = tempVertWordScore;

                    // record the played word's "coordinates"
                    wordPlayedStartRow[numWordsPlayedThisTurn] = currentVerticalWordCol;
                    wordPlayedEndRow[numWordsPlayedThisTurn]   = currentVerticalWordCol;
                    wordPlayedStartCol[numWordsPlayedThisTurn] = currentVerticalWordFirstRow;
                    wordPlayedEndCol[numWordsPlayedThisTurn]   = currentVerticalWordLastRow;

                    // increment the number of words played
                    numWordsPlayedThisTurn++;

                    // update potential score for current turn
                    updateCurrentTurnScore( tempVertWordScore );

                    // set return status to true if all words are OK
                    status = true;

                    // TODO: diagnostic messages to be deleted

                }

            } // end if ( tempVerticalWord.length() >= 2 )

        } // repeat for next value of i < numberOfTilesPlayed (end of outer for loop)

        if ( numberOfTilesPlayed == 7 ) {
            // add 50 points to score if all 7 tiles were played
            System.out.println( "\nAll 7 tiles played -- Bingo bonus awarded: +50 points!" );
            updateCurrentTurnScore( potentialBingoBonus );
        }            

        // the method will return TRUE if all words were in the dictionary
        return status;
    } // end private method scoreAllWords

    /**
     * Method to check to see if word is in the dictionary (or challenged by another player)
     * NOTE: this is not fully implemented -- this is a TODO for Project 4!!
     */
    public boolean inDictionary( String queryWord ) {

        // for Project 1, we'll simply implement this as an opportunity for other players to challenge the word
        // TODO: But this is where you might want to initiate the dictionary search!
        
        System.out.println( "If another player has successfully challenged this word, please enter" );
        System.out.println( "the number (1-4) of that player now. Otherwise, please enter 0: " );

        int choice = playerInput.nextInt();
        if ( choice > 0 ) {
            
            return false; 
        } else {
            return true;
        }
    }

    /**
     * Method for allowing the current player to exchange selected tiles on the rack with tiles
     * from the tileBag.
     * 
     * Realistically, this method SHOULD allow for the possibility that anything the player
     * places in the bag could be what is immediately taken out... but the possibility of that happening
     * is typically "remote enough" that we can do a simple tile exchange between the rack and the tilebag
     * and that should suffice for our purposes.
     * 
     */
    public void passTurn()
    {
        // local variables
        int selectedRackTileIndex = 0;
        int numberOfTilesToExchange = 0;

        System.out.println("-------------------------------------------------------------------------- ");
        System.out.println("Enter the rack positions of all tiles you want to exchange, with        ");
        System.out.println("  each tile position index followed by a single space. Be sure to       ");
        System.out.println("  include a -1 as a signal that you have finished entering the numbers. ");
        System.out.println();
        System.out.println("For example: if you want to exchange the tiles at positions             ");
        System.out.println("  1, 4, and 5, please type the space-delimited number sequence          ");
        System.out.println();
        System.out.println("             1 4 5 -1 ");
        System.out.println();
        System.out.println("  followed by the ENTER key.");
        System.out.println();
        System.out.println("If you want to pass your turn without exchanging tiles,                 ");
        System.out.println("  simply type -1 followed by the ENTER key.                             ");
        System.out.println("-------------------------------------------------------------------------- ");
        System.out.println("Enter rack tile positions here (don't forget to include -1 when finished): ");

        selectedRackTileIndex = playerInput.nextInt();

        // use a sentinel-controlled while loop
        while ( selectedRackTileIndex != -1 ) {

            exchangeTile( selectedRackTileIndex );   
            selectedRackTileIndex = playerInput.nextInt();  

            numberOfTilesToExchange++;

            if ( numberOfTilesToExchange > playerViewOfTileBag.getNumberOfTilesInBag() ) {
                System.out.println("There aren't enough tiles left for a complete exchange.");
                selectedRackTileIndex = -1;
            }
        }

        System.out.println("Exchange (if any) is complete. Press ENTER to proceed to the next player's turn. ");
        playerInput.nextLine();
        playerInput.nextLine(); 
    }

    /**
     * Updates the game board with all changes made during the current player's turn
     * ( in other words, this method is used to finally "commit" the player's moves to the board )
     */
    public void updateGameBoard() 
    {
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {

                playerViewOfBoard.setSpacePlayed( tempBoard.getSpacePlayed( row, col ), row, col );

                playerViewOfBoard.setTileOnBoard(  row, col, tempBoard.getTileOnBoard( row, col) );

                playerViewOfBoard.setSpaceLetter( tempBoard.getSpaceLetter( row, col), row, col);
                playerViewOfBoard.setSpaceValue( tempBoard.getSpaceValue( row, col), row, col);

            }
        }
    }

    /**
     * Retrieves seven tiles from the tileBag and places them on the rack
     * (But strictly speaking we are swapping out the seven "null" tiles initially on the
     *  rack with seven lettered tiles from the tilebag)
     */
    public void setupRack()
    {
        for ( int i = 0; i < 7; i++ ) {
            exchangeTile( i );
        }
    }    

    /**
     * Retrieves tiles, as needed, from the tileBag and places them on the rack
     * (takes place after a player's successful move) 
     */
    public void refillRack()
    {
        int numberOfTilesToRefill = Math.min( playerViewOfTileBag.getNumberOfTilesInBag(), 7 );

        for ( int i = 0; i < numberOfTilesToRefill; i++ ) {

            if ( playerRack.getTileOnRack(i).getLetter() == null ) {
                exchangeTile( i );
            }

        }
    }

    /**
     * Displays the current player's tile rack in an easy-to-read format familiar to Scrabble players.
     * (Also displays the number of tiles remaining in the tile bag.)
     */
    public void displayRack()
    {
        System.out.println();
        //System.out.println(          "Current rack for Player " + playerNumber + " (" + playerName + ")" );
        System.out.println( "Position:  0   1   2   3   4   5   6  " );        
        System.out.println( "         +---+---+---+---+---+---+---+ ");

        System.out.print(   " Letter: ");

        for ( int i = 0; i < 7; i++ ) {
            String currentLetter = playerRack.getTileOnRack( i ).getLetter();
            System.out.printf( "| %s ", ( currentLetter == null ? " " : currentLetter ) );
        }

        System.out.print("|\tThere are " + playerViewOfTileBag.getNumberOfTilesInBag() + " bag tiles\n");

        System.out.print(   "  Value: ");

        for ( int i = 0; i < 7; i++ ) {
            String currentLetter = playerRack.getTileOnRack( i ).getLetter();
            System.out.printf( "| %2s", 
                ( currentLetter == null ? "  " : playerRack.getTileOnRack( i ).getPointValue() ) );
        }

        System.out.print("|\tavailable for exchange.\n");
        System.out.println( "         +---+---+---+---+---+---+---+");

        if ( playerViewOfTileBag.getNumberOfTilesInBag() == 0 )
        {
            System.out.println( "\n** CAUTION: There are NO tiles remaining in the bag. **" );
            System.out.println( "The game will end as soon as one player has used his or her last tile. Good luck!\n" );
        }
    } 

    /**
     * gets the current point value of playerRack (needed for the endgame)
     */
    public int getPlayerRackTotalPointValue() {

        int rackTotalPointValue = 0;

        for ( int i = 0; i < 7; i++ ) {
            rackTotalPointValue += playerRack.getTileOnRack( i ).getPointValue();  
        }

        return rackTotalPointValue;
    }

    /**
     * Draw tile from bag and place on Rack in position i (exchanging with a "null" tile on the Rack)
     */
    public void exchangeTile( int rackPosition  )
    {
        // generic swap method 
        // -------------------
        // temp = x;
        // x = y;
        // y = temp;

        boolean randomTileIsNull = true;
        int randomTileNumber = 0;

        // This while loop prevents the drawing of any "null" tiles from the tile bag
        while ( randomTileIsNull )
        {        
            // generate random tilebag index position for drawing
            randomTileNumber = (int)( Math.random() * 100 );

            if ( playerViewOfTileBag.getTileInBag( randomTileNumber ).getLetter() != null )
            {
                randomTileIsNull = false;  
            }
        } 

        // Create a "temporary tile" (a "local object") for swapping purposes

        // Most efficient approach is to create a new tile, and pass in the letter 
        //   and point value from whatever Tile on the board will be swapped 
        //   ** You should NOT attempt to do object assignments using = operator, 
        //      as it MAY produce unexpected results! **

        Tile tempTile = new Tile( playerViewOfTileBag.getTileInBag( randomTileNumber ).getLetter(),
                playerViewOfTileBag.getTileInBag( randomTileNumber ).getPointValue() );

        // Alternatively, could use the set-and-get approach, as it also ensures we are working with copies
        //   of the object data, and *not* data from copies of references to the same object.
        //   It's important to understand this distinction from simply using the = (equals( operator!!
        /*
         * Tile tempTile = new Tile(); 
         * tempTile.setLetter( playerViewOfTileBag.getTileInBag( randomTileNumber ).getLetter() ); 
         * tempTile.setPointValue( playerViewOfTileBag.getTileInBag( randomTileNumber ).getPointValue() );
         */

        // equivalent to "x = y;" in the generic swap algorithm 
        playerViewOfTileBag.setTileInBag( randomTileNumber, playerRack.getTileOnRack( rackPosition ) );

        // equivalent to "y = temp;" in the generic swap algorithm 
        playerRack.setTileOnRack( rackPosition, tempTile );

    }   

    /**
     * Take tile from rack position i and place on board at coordinates ( boardRow , boardCol )
     */
    public void placeTileOnBoard( int rackPosition, int boardRow, int boardCol )
    {       
        // Create a "temporary tile" (a "local object") for swapping purposes

        // Most efficient approach is to create a new tile, and pass in the letter 
        //   and point value from whatever Tile on the board will be swapped 
        //   ** You should NOT attempt to do object assignments using = operator, 
        //      as it MAY produce unexpected results! **

        Tile tempTile = new Tile( tempBoard.getTileOnBoard( boardRow, boardCol ).getLetter(),
                tempBoard.getTileOnBoard( boardRow, boardCol ).getPointValue() );

        // Alternatively, could use the set-and-get approach, as it also ensures we are working with copies
        //   of the object data, and *not* data from copies of references to the same object.
        //   It's important to understand this distinction from simply using the = (equals( operator!!
        /*
        Tile tempTile = new Tile();                         
        tempTile.setLetter( tempBoard.getTileOnBoard( boardRow, boardCol ).getLetter() );
        tempTile.setPointValue( tempBoard.getTileOnBoard( boardRow, boardCol ).getPointValue() );
         */

        // equivalent to "x = y;" in the generic swap algorithm 
        tempBoard.setTileOnBoard( boardRow, boardCol, playerRack.getTileOnRack( rackPosition ) );

        currentTilesPlaced.push( playerRack.getTileOnRack( rackPosition ) );
        currentTilesPlacedRow.push(boardRow);
        currentTilesPlacedCol.push(boardCol);
        currentTilesPlacedRackPos.push(rackPosition);

        // equivalent to "y = temp;" in the generic swap algorithm 
        playerRack.setTileOnRack( rackPosition , tempTile );

    }   
    /**
     * Pop off the information from each of the tile placement history stacks and use
     * that information to remove the tile from the board and return it to the original
     * position on the rack
     * 
     * Essentially this method is going to be "placeTileOnBoard" but acting in reverse
     * 
     */
    //     public void undoLastTilePlacement()
    //     {
    //         // Retrieve the data you need
    //         Tile ?theTileItself?                             = currentTilesPlaced.pop( );
    //         int ?theBoardRowYouAreGettingTheTileFrom?        = currentTilesPlacedRow.pop( );
    //         int ?theBoardColumnYouAreGettingTheTileFrom?     = currentTilesPlacedCol.pop( );
    //         int ?thePositionOnTheRackWhereYouWillPutTheTile? = currentTilesPlacedRackPos.pop( );
    //         
    // Now use the info above to restore the popped tile back onto the rack
    // and make sure that the board is "clear" at that original boardrow, boardcol position

    /**
     * Confirm the quit command
     */
    public void confirmQuit()
    {
        String choice = null;

        System.out.print("Are you sure you want to quit? Type Y or N followed by the ENTER key: ");

        choice = playerInput.nextLine();

        if ( choice.equals("Y") ) 
        {
            Game.end();
        }
    }

} // end class Player
