import java.util.*;
import java.io.File;
import java.io.*;
/**
 * This class is responsible for controlling the setup and overall flow of the Scrabble game, although much of the 
 * game logic itself is in the Player class... you were not required to implement the game this way. 
 * 
 * @author BCanada
 * @version 0
 */
public class Game
{
    // fields -- see the individual classes for details
    private Board gameBoard;
    private Player[] gamePlayer;
    private TileBag gameTileBag;
    private Scoreboard gameScoreboard;
    private List<String> dictionaryList;

    // other variables & static fields
    private int numberOfPlayers;
    private static boolean firstMoveOfGame;
    private static int rowOfLastTilePlacementAttempt;
    private static int colOfLastTilePlacementAttempt;
    private static boolean wordPlacementAcross;
    private static boolean wordPlacementDown;

    // constant, single copy of Scanner object
    private static final Scanner gameInput = new Scanner( System.in );
    private Scanner file;
    private String word;
    private Tile letter;

    /**
     * Constructor for objects of class Game... most of the initialization logic is in the resetGame() method,
     * and the startGameLoop method basically gets the current game underway :-)
     */
    public Game()
    {
        resetGame();
        startGameLoop();
        String[] dictionary = {file.next()};
        dictionaryList = Arrays.asList(dictionary);
    }
    public void readFile()
    {
        File file = new File("E:\\ScrabblueJ_ProfCanadaSolution_noGUI\\ScrabbleDictionary_Complete.txt");

        try {

            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
               
                System.out.println(dictionaryList);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
   /**
     * sets OR resets the current game to an initial "fresh" state
     * 
     * NOTE: I've designed the method this way so that if a player wants to play again, they can do
     *       so without the BlueJ interface....
     * 
     */
    public void resetGame()
    {     
        clearScreen();
        showTitleScreen();
        gameBoard = new Board();
        gameTileBag = new TileBag();

        firstMoveOfGame = true;

        // TODO: For project 4, implement exception handling here
        boolean continueLoop = true;
        do{
            try{
                System.out.print( "How many players? Enter 2, 3, or 4: ");
                numberOfPlayers = gameInput.nextInt(); 
                if(numberOfPlayers != 2 && numberOfPlayers != 3 && numberOfPlayers != 4 )
                {

                    System.out.println( " Try again? Please enter a valid number. ");

                }else{
                    continueLoop = false;
                }
            }catch(InputMismatchException e){
                gameInput.next();
                System.out.println("Enter an Integer");
                continue;
            }
        }while( continueLoop );

        gamePlayer = new Player[ numberOfPlayers ];
        gameScoreboard = new Scoreboard( gamePlayer );
        // IMPORTANT: Note that each player's constructor is passed a COPY of the value of the 
        // reference to the current gameBoard and gameTileBag objects. 
        for ( int i = 0 ; i < numberOfPlayers ; i++ ) {

            System.out.print( "Enter a name for Player" + (i + 1) + ": " );
            String playerName = gameInput.next();

            gamePlayer[ i ] = new Player( (i + 1) , playerName, gameBoard, gameTileBag, gameScoreboard ); 

        }

    }

    /**
     * Prints out the game's "Title Screen" -- this would have been a huge hit in 1979 :-)
     */
    public void showTitleScreen() {
        System.out.println();
        System.out.println(" .--------.--------.--------.--------.--------.--------.--------.--------.--------.--------. ");
        System.out.println(" | @@@@   |  @@@   | @@@    |  @@    | @@@    | @@@    | @      | @  @   | @@@@   |    @   |\\ ");
        System.out.println(" | @      | @      | @  @   | @  @   | @  @   | @  @   | @      | @  @   | @      |    @   | |");
        System.out.println(" | @@@@   | @      | @@@    | @@@@   | @@@    | @@@    | @      | @  @   | @@@@   |    @   | |");
        System.out.println(" |    @   | @      | @ @    | @  @   | @  @   | @  @   | @      | @  @   | @      | @  @   | |");
        System.out.println(" | @@@@   |  @@@   | @  @   | @  @   | @@@    | @@@    | @@@@   | @@@@   | @@@@   |  @@    | |");
        System.out.println(" |      1 |      3 |      1 |      1 |      3 |      3 |      1 |      1 |      1 |      8 | | ");        
        System.out.println(" |________|________|________|________|________|________|________|________|________|________| |");
        System.out.println("  \\________\\________\\________\\________\\________\\________\\________\\________\\________\\________\\|");
        System.out.println();
    }

    /**
     * This method initiates the game loop, but most of the logic of what happens is in the Player class
     */
    public void startGameLoop()
    {
        boolean continueGameLoop = true;

        // continue playing the game as long as there are no conditions for ending the game
        // (again, I've put most of this logic in the Player class)
        do 
        {
            // Start cycling through players
            for ( int i = 0 ; i < numberOfPlayers ; i++ )
            {
                gamePlayer[i].startTurn();     
            }

        } while ( continueGameLoop );

    }   

    /**
     * Method for checking to see if current move is legal, based on:
     * 
     * 1) was there already a tile placed at that position
     * 2) if this is not the first tile placed for this turn, are all subsequent tiles
     *    in the same row or the same column? (i.e., no zig-zagging, etc.)
     * 
     */
    public static boolean checkForLegalMove( Board referenceToGameBoard, int row , int col, int numberOfTilesPlayed ) {

        if ( referenceToGameBoard.getSpacePlayed( row, col ) == false ) 
        {
            if ( numberOfTilesPlayed == 0 ) {
                wordPlacementAcross = false;
                wordPlacementDown = false;
                rowOfLastTilePlacementAttempt = row;
                colOfLastTilePlacementAttempt = col;
                return true;
            }
            else if ( numberOfTilesPlayed == 1 )
            {
                // NOTE: We infer an assumed direction of word being played based on 2nd tile placement
                if ( row == rowOfLastTilePlacementAttempt ) {
                    wordPlacementAcross = true;
                    wordPlacementDown = false;
                    return true;
                } 
                else if ( col == colOfLastTilePlacementAttempt ) {
                    wordPlacementDown = true;
                    wordPlacementAcross = false;
                    return true;
                }
            }
            else if ( numberOfTilesPlayed > 1 )
            {
                if ( wordPlacementAcross == true && row == rowOfLastTilePlacementAttempt ) {
                    return true;
                }
                else if ( wordPlacementDown == true && col == colOfLastTilePlacementAttempt ) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * get method to retrieve the boolean "flag" variable telling us whether we have
     * actually made the first move of the game (yes, this is necessary for checking the
     * legality of tile and word placement on the board).... 
     * 
     * ...but it's also necessary because it helps to preserve encapsulation of the data
     * 
     */
    public static boolean getFirstMoveOfGameStatus() {
        return firstMoveOfGame;
    }

    /**
     * here's the corresponding set method that is really only used once :-)
     */
    public static void setFirstMoveOfGameStatus( boolean status ) {
        firstMoveOfGame = status;
    }

    /**
     * Method to end the game (and exit with normal status).
     */
    public static void end()
    {
        System.out.println("**************************");
        System.out.println("*                        *");
        System.out.println("* Thank you for playing! *");
        System.out.println("*                        *");
        System.out.println("**************************");

        // NOTE: All this does is introduce a short pause (100 milliseconds) to allow the 
        //       above message to print to the screen before the program actually exits
        //       (updating the console display takes time, and the program will exit before that happens)
        try {
            Thread.sleep(100);
        } catch( InterruptedException e ) {
            // do nothing
        } 

        // By convention, an exit code of 0 means the program exits normally      
        System.exit( 0 );
    }

    /**
     * Clears the screen. NOTE: May be specific to BlueJ and/or the current OS.
     */
    public static void clearScreen()
    {
        System.out.print('\u000C');
    }

}
