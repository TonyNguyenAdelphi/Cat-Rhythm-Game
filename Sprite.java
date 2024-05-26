import javafx.scene.image.*;
import javafx.scene.canvas.*;
import javafx.geometry.*;

/*
 * Store data and methods
 * for movable images on screen in a game.
 */
public class Sprite
{
    public double x;
    public double y;
    public double width;
    public double height;
    public Image  pic;
    
    // automatic movement values in each direction.
    public double distanceX, distanceY;
    
    // constructor
    public Sprite(double _x, double _y, double _width, double _height, String fileName)
    {
        x = _x;
        y = _y;
        width = _width;
        height = _height;
        pic = new Image(fileName, width, height, false, true);
    }
    
    // move the sprite by changing the x and y coordinates
    public void move(double dx, double dy)
    {
        x = x + dx;
        y = y + dy;
    }
    
    // draw the image on a canvas using a graphics context
    public void draw(GraphicsContext context)
    {
        context.drawImage( pic, x, y );
    }
    
    // check if this sprite overlaps with another sprite (boundary rectangles)
    public boolean overlaps(Sprite other)
    {
        // represents the boundary of this sprite
        Rectangle2D rect1 = new Rectangle2D(x,y, width,height);
        
        // boundary of other sprite
        Rectangle2D rect2 = new Rectangle2D(other.x, other.y, other.width, other.height);
        
        // check if they overlap
        boolean overlap = rect1.intersects( rect2 );
        
        // return the result
        return overlap;
    }
    
    // if object passes beyond boundary of canvas (600,600)
    //   then set coordinates so it appears on other side
    public void wrap()
    {
        // right edge of canvas
        if (x > 600)
            x = -width;
            
        // left edge of canvas
        if (x < -width)
            x = 600;
            
        // bottom edge of canvas
        if (y > 600)
            y = -height;
            
        // top edge of canvas
        if (y < -height)
            y = 600;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
}