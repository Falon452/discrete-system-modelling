import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
    private static final long serialVersionUID = 1L;
    private Point[][] points;
    private boolean[][] isBack;
    private int size = 25;
    public int editType = 0;
    protected int leftLane;
    protected int rightLane;

    public Board(int length, int height) {
        addMouseListener(this);
        addComponentListener(this);
        addMouseMotionListener(this);
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    private void initialize(int length, int height) {
        this.points = new Point[length][height];
        this.isBack = new boolean[length][height];
        this.leftLane = (points[0].length / 2) - 1;
        this.rightLane = (points[0].length / 2);
        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                this.points[x][y] = new Point();
                if(y == leftLane || y == rightLane ){
                    points[x][y].type = 0;
                }else{
                    points[x][y].type = 5;
                }
            }
        }
        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                if(y == leftLane || y == rightLane){
                    if(y == leftLane){
                        points[x][y].right = points[x][y+1];
                        points[x][y].left = null;
                    }else{
                        points[x][y].left = points[x][y-1];
                        points[x][y].right = null;
                    }
                    if(x == points.length - 1){
                        points[x][y].next = points[0][y];
                    }else{
                        points[x][y].next = points[x+1][y];
                    }
                }
            }
        }
    }


    boolean canOvertake(int x, int y){
        return y == rightLane && points[x][y].acc < points[x][y].maxAcc
                && isBackFree(x, leftLane, 7) && isBackFree(x, rightLane, 7)
                && isNextFree(x, leftLane, points[x][y].acc);
        //wez sprawdz te newralgiczne punkty <= >= itd
    }

    boolean canGoBack(int x, int y){
        return y == leftLane
                && isBackFree(x, leftLane, 7) && isBackFree(x, rightLane, 7)
                && isNextFree(x, rightLane, points[x][y].acc);
    }


    public void iteration() {
//      preparing cars for moving
        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                points[x][y].moved = false;
                isBack[x][y] = false;
                if(points[x][y].isACar() && points[x][y].acc < points[x][y].maxAcc){
                    points[x][y].acc ++;
                }
            }
        }
//      moving cars
        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                if (points[x][y].isACar() && !points[x][y].moved && canGoBack(x, y)) {
                    points[x][y].goBack();
                    isBack[x][y] = true;
                }
            }
        }
        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                if (points[x][y].isACar() && !points[x][y].moved && canOvertake(x, y) && !isBack[x][y]) {
                    points[x][y].overtake();
                }
            }
        }
        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                if (points[x][y].isACar() && !points[x][y].moved) {
                    moveForward(x, y);
                }
            }
        }
        this.repaint();
    }


    void moveForward(int x, int y){
        int counter = 0;
        for (int i = 1; i <= points[x][y].acc; i++){
            if(isNextFree(x, y, i)) {
                counter++;
            }
            else{
                break;
            }
        }
        if(x + counter >= points.length - 1){
            points[x][y].next = points[counter - (points.length - 1 - x)][y];
        }else{
            points[x][y].next = points[x+counter][y];
        }
        points[x][y].move(counter);
    }


    boolean isNextFree(int x, int y, int cnt){
        if(x + cnt < points.length && points[x+cnt][y].type == 0) {
            return true;
        }else if(x + cnt < points.length && points[x+cnt][y].type != 0){
            return false;
        }else return x + cnt >= points.length && points[cnt - (points.length - 1 - x)][y].type == 0;
    }


    boolean isBackFree(int x, int y, int cnt) {
        for (int i = 1; i < cnt; i++) {
            if(!isPrevFree(x, y, i)){
                return false;
            }
        }
        return true;
    }

    boolean isPrevFree(int x, int y, int cnt) {
        if (x - cnt >= 0 && points[x - cnt][y].type == 0) {
            return true;
        } else if (x - cnt >= 0 && points[x - cnt][y].type != 0) {
            return false;
        } else return x - cnt < 0 && points[points.length - cnt + x][y].type == 0;
    }

    public void clear() {
        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y) {
                if(y == points[x].length / 2 || y ==  - 1 + (points[x].length / 2)){
                    points[x][y].clear();
                }
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
                switch(points[x][y].type){
                    case 0: g.setColor(Color.WHITE); break;
                    case 1: g.setColor(Color.YELLOW); break;
                    case 2: g.setColor(Color.BLUE); break;
                    case 3: g.setColor(Color.RED); break;
                    case 5: g.setColor(Color.GREEN); break;

                }
                g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
            }
        }

    }

    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            if(editType == 0){
                points[x][y].clicked();
            }
            else {
                points[x][y].type = editType;
                switch(points[x][y].type){
                    case 1: points[x][y].maxAcc = 3; points[x][y].acc = 3; break;
                    case 2: points[x][y].maxAcc = 5; points[x][y].acc = 5; break;
                    case 3: points[x][y].maxAcc = 7; points[x][y].acc = 7; break;
                }
            }
            this.repaint();
        }
    }

    public void componentResized(ComponentEvent e) {
        int dlugosc = (this.getWidth() / size) + 1;
        int wysokosc = (this.getHeight() / size) + 1;
        initialize(dlugosc, wysokosc);
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            if(editType == 0){
                points[x][y].clicked();
            }
            else {
                points[x][y].type = editType;
            }
            this.repaint();
        }
    }

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
