package agh.ics.mds;

import agh.ics.mds.Enums.Driver;
import agh.ics.mds.Enums.Type;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import static agh.ics.mds.Enums.Type.*;
import static agh.ics.mds.Enums.Lane.*;
import static agh.ics.mds.Enums.Driver.*;
import static java.lang.Math.abs;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
    private static final long serialVersionUID = 1L;
    private Point[][] points;
    private final int size = 25;
    public Type editType = ROAD;
    public Driver carType = Driver.LEWIS_HAMILTION;
    public static int iterationNumber = 0;


    public Board(int length, int height) {
        addMouseListener(this);
        addComponentListener(this);
        addMouseMotionListener(this);
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    private void initialize(int length, int height) {
        points = new Point[length][height];

        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                points[x][y] = new Point();
                if (y == 0 || y == 1 || y == 4 || y == 5)
                    points[x][y].type = GRASS;
                if (y == 2)
                    points[x][y].lane = LEFT;
                if (y == 3)
                    points[x][y].lane = RIGHT;
            }
        }

        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                if (points[x][y].type == ROAD) {
                    for (int v = 1; v <= Point.FASTEST_VEHICLE_SPEED; v++) {
                        points[x][y].next[v - 1] = points[(x + v) % points.length][y];
                        if (y == 2)
                            points[x][y].nextOtherLane[v] = points[(x + v) % points.length][3];
                        if (y == 3)
                            points[x][y].nextOtherLane[v] = points[(x + v) % points.length][2];
                    }

                    if (y == 2)
                        points[x][y].nextOtherLane[0] = points[x][3];

                    if (y == 3)
                        points[x][y].nextOtherLane[0] = points[x][2];

                    for (int v = -1; v >= -Point.FASTEST_VEHICLE_SPEED; v--) {
                        int tmp = (x + v) % points.length;
                        tmp = tmp >= 0 ? tmp: tmp + points.length;

                        points[x][y].before[Math.abs(v) - 1] = points[tmp][y];

                        if (y == 2)
                            points[x][y].beforeOtherLane[Math.abs(v) - 1] = points[tmp][3];
                        if (y == 3)
                            points[x][y].beforeOtherLane[Math.abs(v) - 1] = points[tmp][2];
                    }
                }
            }
        }

    }

    public void iteration() {

        for (Point[] value : points) {
            for (Point point : value) {
                point.moved = false;
            }
        }
        if (iterationNumber % 3 == 2) {
            for (Point[] value : points)
                for (Point point : value)
                    point.accelerate();

            for (Point[] point : points)
                for (Point value : point)
                    value.slowing_down();

            for (Point[] point : points)
                for (Point value : point)
                    value.move();

        }
        if (iterationNumber % 3 == 1) {
            for (Point[] point : points)
                for (Point value : point)
                    value.overtaking();
        }

        if (iterationNumber % 3 == 0) {
            for (Point[] point : points)
                for (Point value : point)
                    value.returning();
        }

        iterationNumber++;
        this.repaint();
    }

    public void clear() {
        for (Point[] point : points)
            for (Point value : point) {
                value.clear();
            }
        this.repaint();
    }


    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        g.setColor(Color.GRAY);
        drawNetting(g, size);
    }

    private void drawNetting(Graphics g, int gridSpace) {
        Insets insets = getInsets();
        int firstX = insets.left;
        int firstY = insets.top;
        int lastX = this.getWidth() - insets.right;
        int lastY = this.getHeight() - insets.bottom;

        int x = firstX;
        while (x < lastX) {
            g.drawLine(x, firstY, x, lastY);
            x += gridSpace;
        }

        int y = firstY;
        while (y < lastY) {
            g.drawLine(firstX, y, lastX, y);
            y += gridSpace;
        }

        for (x = 0; x < points.length; ++x) {
            for (y = 0; y < points[x].length; ++y) {

                if (points[x][y].type == GRASS)
                    g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.7f));

                if (points[x][y].type == ROAD)
                    g.setColor(new Color(0.1f, 0.1f, 0.1f, 0.7f));

                if (points[x][y].driver == LEWIS_HAMILTION)
                    g.setColor(new Color(0, 0.63f, 0.61f, 1));

                if (points[x][y].driver == MAX_VERSTAPPEN)
                    g.setColor(new Color(0, 0.04f, 0.55f, 1));

                if (points[x][y].driver == ROBERT_KUBICA)
                    g.setColor(new Color(0.9f, 0.9f, 0.9f, 1));

                g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
            }
        }

    }

    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            if (y == 2 | y == 3) {
                points[x][y].type = CAR;
                points[x][y].driver = carType;
            }
        }
        this.repaint();
    }


    public void componentResized(ComponentEvent e) {
        int length = (this.getWidth() / size) + 1;
        int height = (this.getHeight() / size) + 1;
        initialize(length, height);
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            if (y == 2 | y == 3) {
                points[x][y].type = CAR;
                points[x][y].driver = carType;
            }
        this.repaint();
        }}

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

}
