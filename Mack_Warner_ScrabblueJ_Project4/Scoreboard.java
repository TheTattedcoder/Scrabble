
/**
 * Does what you think it does -- displays the current score for each player
 * 
 * Also computes the final score for each player when the game-over condition has been reached
 * 
 * @author BCanada
 * @version 0
 */
public class Scoreboard
{
    // fields 
    // this array is just a reference to the existing array of current player objects   
    private Player[] playerArray;

    /**
     * Constructor for objects of class Scoreboard
     */
    public Scoreboard( Player[] playerArray)
    {
        // remember to use the this keyword to disambiguate local vs. instance variables
        this.playerArray = playerArray;
    }

    /**
     * Display all players' current scores as of the end of the most recent turn
     */
    public void displayScores() 
    {
        // NOTE: Here's another simple but effective use of the enhanced for loop
        //       (all arrays are inherently iterable!)
        for ( Player player : playerArray ) {

            System.out.println( "Player #" + player.getPlayerNumber() + " (" + player.getPlayerName() + ") Score: " + 
                player.getPlayerScore() + " points");
        }
    }

    /**
     * After the first player to finish has played his or her last tile, go around to the other players and
     * sum up the values of their tiles. 
     * 
     * Add that subtotal to the finishing player's score while subtracting the
     * same amount from the current non-finishing player.
     * 
     * Get final scores, declare winner, and end the game!
     * 
     * and boom goes the dynamite
     * 
     */
    public void computeFinalScores( int firstPlayerToFinish ) 
    {
        int firstPlayerToFinishBonusPoints = 0;
        int bestScore = 0;
        int bestScorePlayerNumber = 0;
        
        for ( Player player : playerArray ) {

            if ( player.getPlayerNumber() != firstPlayerToFinish ) {

                // sum up the point values of all tiles left on the current player's rack
                int rackTotalPointValue = player.getPlayerRackTotalPointValue();
                player.updatePlayerScore( -rackTotalPointValue );

                // accumulate bonus points to be added to first finishing player's score
                firstPlayerToFinishBonusPoints += rackTotalPointValue;
            
                System.out.println( "Player #" + player.getPlayerNumber() + " (" + player.getPlayerName() + ") Final Score: " + 
                    player.getPlayerScore() + " points");
                    
                if ( player.getPlayerScore() > bestScore ) {
                    bestScore = player.getPlayerScore();
                    bestScorePlayerNumber = player.getPlayerNumber();
                }
            }
        }
        
        int firstPlayerIndex = firstPlayerToFinish - 1;
        
        playerArray[ firstPlayerIndex ].updatePlayerScore( firstPlayerToFinishBonusPoints );

        System.out.println( "Player #" + playerArray[ firstPlayerIndex ].getPlayerNumber() 
                                       + " (" + playerArray[ firstPlayerIndex ].getPlayerName() + ") Final Score: " 
                                       + playerArray[ firstPlayerIndex ].getPlayerScore() + " points");

        if ( playerArray[ firstPlayerIndex ].getPlayerScore() > bestScore ) 
        {
             bestScore = playerArray[ firstPlayerIndex ].getPlayerScore();
             bestScorePlayerNumber = playerArray[ firstPlayerIndex ].getPlayerNumber();
        }    
        
        // print out the winner!!!
        System.out.println( "\n ** WINNER: Player #" + bestScorePlayerNumber + "**" );
        
    } // end method computeFinalScores
    
} // end class Scoreboard
