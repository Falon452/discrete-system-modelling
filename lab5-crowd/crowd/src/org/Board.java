package org;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private static final long serialVersionUID = 1L;
	private Point[][] points;
	private int size = 10;
	public CellTypes editType= CellTypes.AIR;
    public Neighbourhood neighbourhood = Neighbourhood.Moore;
    private int iterationNumber = 0;


	public Board(int length, int height) {
		addMouseListener(this);
		addComponentListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
		setOpaque(true);
	}

	public void iteration() {
        for (int x = 1; x < points.length - 1; ++x)
            for (int y = 1; y < points[x].length - 1; ++y) {
                points[x][y].blocked = false;
                points[x][y].blockedSmoke = false;
            }

		for (int x = 1; x < points.length - 1; ++x)
			for (int y = 1; y < points[x].length - 1; ++y) {

                if (points[x][y].smoked) {
                    if (iterationNumber % 2 == 0)
                        points[x][y].move();
                } else {
                    points[x][y].move();
                }
            }

        if (iterationNumber % 4 == 3) {
            for (int x = 1; x < points.length - 1; ++x)
                for (int y = 1; y < points[x].length - 1; ++y)
                    points[x][y].moveSmoke();
        }

        if (iterationNumber % 18 == 17) {
            for (int x = 1; x < points.length - 1; ++x)
                for (int y = 1; y < points[x].length - 1; ++y) {
                    points[x][y].moveFire();
                }
            clear();  // calculate field
        }

        iterationNumber += 1;
		this.repaint();
	}

	public void clear() {
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y) {
				points[x][y].clear();
			}
		calculateField();
		this.repaint();
	}

	private void initialize(int length, int height) {
		points = new Point[length][height];

		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y)
				points[x][y] = new Point();

		for (int x = 1; x < points.length-1; ++x) {
			for (int y = 1; y < points[x].length-1; ++y) {
                switch (neighbourhood) {
                    case Moore -> initializeMoore(x, y);
                    case vonNeuman -> initializeVonNeuman(x, y);
                }
			}
		}	
	}

    public void initializeNewNeighbours() {
        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y)
                points[x][y].clearNeighbours();

        for (int x = 1; x < points.length-1; ++x) {
            for (int y = 1; y < points[x].length-1; ++y) {
                switch (neighbourhood) {
                    case Moore -> initializeMoore(x, y);
                    case vonNeuman -> initializeVonNeuman(x, y);
                }
            }
        }
    }
	
	private void calculateField(){
        ArrayList<Point> toCheck = new ArrayList<Point>();
        // set static field of exits to 0
        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y)
                if (points[x][y].type == CellTypes.EXIT) {
                    points[x][y].staticField = 0;
                    toCheck.addAll(points[x][y].neighbors);
                }

        while (!toCheck.isEmpty()) {
            Point p = toCheck.get(0);
            if (p.calcStaticField()) {
                toCheck.addAll(p.neighbors);
            }
            toCheck.remove(0);
        }

	}
    private void initializeMoore(Integer x, Integer y) {
        points[x][y].addNeighbor(points[x-1][y-1]);
        points[x][y].addNeighbor(points[x-1][y]);
        points[x][y].addNeighbor(points[x-1][y+1]);
        points[x][y].addNeighbor(points[x][y-1]);
        points[x][y].addNeighbor(points[x][y+1]);
        points[x][y].addNeighbor(points[x+1][y-1]);
        points[x][y].addNeighbor(points[x+1][y]);
        points[x][y].addNeighbor(points[x+1][y+1]);
    }

    private void initializeVonNeuman(Integer x, Integer y) {
        points[x][y].addNeighbor(points[x][y-1]);
        points[x][y].addNeighbor(points[x][y+1]);
        points[x][y].addNeighbor(points[x-1][y]);
        points[x][y].addNeighbor(points[x+1][y]);
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

		for (x = 1; x < points.length-1; ++x) {
			for (y = 1; y < points[x].length-1; ++y) {
				if(points[x][y].type== CellTypes.AIR){
					float staticField = points[x][y].staticField;
					float intensity = staticField/100;

					if (intensity > 1.0) {
						intensity = 1.0f;
					}

					g.setColor(new Color(1.0f - intensity,1.0f - intensity,1.0f - intensity ));
				}

                if (points[x][y].smoked) {
                    g.setColor(new Color(0f, 0f, 0f));
                }
                if (points[x][y].type==CellTypes.WALL){
					g.setColor(new Color(1.0f, 0.0f, 0.0f, 0.7f));
				}
				if (points[x][y].type==CellTypes.EXIT){
					g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.7f));
				}
				if (points[x][y].isPedestrian){
					g.setColor(new Color(0.0f, 0.0f, 1.0f, 0.7f));
				}
                if (points[x][y].type == CellTypes.FIRE) {
                    g.setColor(new Color(0.88f, 0.34f, 0.13f, 1f));
                }

				g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
			}
		}

	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
			if(editType==CellTypes.PERSON){
				points[x][y].isPedestrian=true;
			}
			else{
				points[x][y].type= editType;
                if (editType == CellTypes.FIRE) {
                    points[x][y].smoked = true;
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
			if(editType==CellTypes.PERSON){
				points[x][y].isPedestrian=true;
			}
			else{
				points[x][y].type= editType;
                if (editType == CellTypes.FIRE) {
                    points[x][y].smoked = true;
                }
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
