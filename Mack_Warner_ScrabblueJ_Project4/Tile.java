
/**
 * Contains instructions for initializing as well as setting the letter and point value
 * of a tile object 
 * 
 * @author BCanada
 * @version 0
 */
public class Tile
{
    // instance variables - replace the example below with your own
    // link list
    private int pointValue;
    private String letter;

    /**
     * No-argument constructor for objects of class Tile
     */
    public Tile()
    {
        // initialise instance variables
        setLetter( null );
        setPointValue( 0 );
    }

    /**
     * No-argument constructor for objects of class Tile
     */
    public Tile( String letter, int pointValue)
    {
        // initialise instance variables
        setLetter( letter );
        setPointValue( pointValue );
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void setPointValue(int pointValue_local )
    {
        pointValue = pointValue_local;
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public int getPointValue() 
    {
        return pointValue;
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public void setLetter(String letter_local )
    {
        letter = letter_local;
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public String getLetter() 
    {
        return letter;
    }

    public String toString()
    {
        return "" + letter + pointValue;
    }
}
