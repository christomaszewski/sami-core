package sami.area;

import sami.path.Location;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nbb
 */
public class Area2D implements Serializable {

    protected List<Location> points = new ArrayList<Location>();

    public Area2D() {
    }

    public Area2D(List<Location> points) {
        this.points = points;
    }

    public List<Location> getPoints() {
        return points;
    }

    public String toString() {
        return "Area2D [" + (points != null ? points.toString() : "null");
    }
}
