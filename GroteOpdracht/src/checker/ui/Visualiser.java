package checker.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import checker.validator.Order;
import checker.validator.Solution;

public class Visualiser extends JPanel implements ComponentListener, MouseListener, ChangeListener {
	private static final long serialVersionUID = 8589186749519499054L;
	App main;
	Solution solution;
	BufferedImage image;
	JScrollPane scrollPane;
	JScrollBar sp_h;
	JScrollBar sp_v;
	int viewPortWidth = 0;
	int viewPortHeight = 0;

	int zoom = 1;

	ArrayList<VisualiserRoute> routes;

	long minX = Long.MAX_VALUE;
	long maxX = Long.MIN_VALUE;
	long minY = Long.MAX_VALUE;
	long maxY = Long.MIN_VALUE;

	boolean repos = false;

	int center_x;

	int center_y;
	int oldMaxV;
	int oldMaxH;
	private boolean textVisible;

	public Visualiser(App main) {
		this.main = main;

		this.routes = new ArrayList<>();
		this.textVisible = false;
		addMouseListener(this);
	}

	protected void paintComponent(Graphics g) {
		g.drawImage(this.image, 0, 0, null);
	}

	public void updateSize(JViewport port) {
		this.viewPortHeight = port.getHeight();
		this.viewPortWidth = port.getWidth();

		setPreferredSize(new Dimension(this.viewPortWidth * this.zoom, this.viewPortHeight * this.zoom));

		repaintBufferedImage();
	}

	public void setJScrollPane(JScrollPane sp) {
		if (this.scrollPane != null) {
			throw new Error("ScrollPane Already initiated!");
		}

		this.scrollPane = sp;
		this.sp_h = sp.getHorizontalScrollBar();
		this.sp_v = sp.getVerticalScrollBar();

		updateSize(sp.getViewport());
		sp.addComponentListener(this);

		this.sp_h.getModel().addChangeListener(this);
		this.sp_v.getModel().addChangeListener(this);
	}

	public void repaintBufferedImage() {
		if ((this.solution != null) && (this.viewPortHeight != 0) && (this.viewPortWidth != 0)) {

			if (this.image == null) {
				this.image = new BufferedImage(this.viewPortWidth * this.zoom, this.viewPortHeight * this.zoom, 1);
			} else if ((this.image.getHeight() != this.viewPortHeight * this.zoom)
					|| (this.image.getWidth() != this.viewPortWidth * this.zoom)) {
				this.image = new BufferedImage(this.viewPortWidth * this.zoom, this.viewPortHeight * this.zoom, 1);

				System.gc();

			} else {
				this.image.flush();
			}

			Graphics g = this.image.createGraphics();

			g.setColor(Color.lightGray);
			g.fillRect(0, 0, this.image.getWidth(), this.image.getHeight());

			double xF = this.viewPortWidth * this.zoom / (1.0D * this.maxX - this.minX);
			double yF = this.viewPortHeight * this.zoom / (1.0D * this.maxY - this.minY);
			long xOff = -this.minX;
			long yOff = -this.minY;

			for (VisualiserRoute r : this.routes) {
				if (r.visible) {
					r.DrawRoute(this.solution, g, xOff, yOff, xF, yF);
				}
			}

			g.setColor(Color.black);
			for (Order o : this.solution.problem.getOrders()) {
				int x = (int) (xF * (o.xCoord + xOff));
				int y = (int) (yF * (o.yCoord + yOff));

				if (o.freq == 1) {
					g.setColor(Color.black);
					g.drawRect(x - 1, y - 1, 2, 2);

					if (this.textVisible) {
						g.drawString("" + o.loc, x + 5, y + 5);
					}
					continue;
				}

				Color col;
				switch (o.freq) {
				case 2:
					col = Color.yellow;
					break;
				case 3:
					col = Color.orange;
					break;
				case 4:
					col = Color.pink;
					break;
				default:
					col = Color.black;
				}
				g.setColor(col);
				g.fillRect(x - 1, y - 1, o.freq * 2, o.freq * 2);
			}

			Order disposal = this.solution.problem.getOrder(0);

			g.setColor(Color.red);

			int x = (int) (xF * (disposal.xCoord + xOff));
			int y = (int) (yF * (disposal.yCoord + yOff));

			g.fillRect(x - 2, y - 2, 4, 4);
		}

		repaint();
	}

	public void setSolution(Solution sol) {
		this.solution = sol;
		if (sol == null) {
			this.image = null;

		} else {
			this.minX = Long.MAX_VALUE;
			this.maxX = Long.MIN_VALUE;
			this.minY = Long.MAX_VALUE;
			this.maxY = Long.MIN_VALUE;

			for (Order o : this.solution.problem.getOrders()) {
				this.minX = Math.min(this.minX, o.xCoord);
				this.maxX = Math.max(this.maxX, o.xCoord);
				this.minY = Math.min(this.minY, o.yCoord);
				this.maxY = Math.max(this.maxY, o.yCoord);
			}

			this.minY = ((this.minY - (this.maxY - this.minY) / 2));
			this.maxY = ((this.maxY + (this.maxY - this.minY) / 2));
			this.minX = ((this.minX - (this.maxX - this.minX) / 2));
			this.maxX = ((this.maxX + (this.maxX - this.minX) / 2));

			int routeId = 0;
			this.routes.clear();

			for (int d = 0; d < 5; d++) {
				for (int t = 0; t < 2; t++) {
					Order[] route = sol.solution[t][d];
					int r = 0;

					if (route.length > 1) {
						r = 1;
						Color c = ColorGenerator.getColor(routeId);
						routeId++;
						this.routes.add(new VisualiserRoute(t, d, 1, 0, c));
					}

					for (int i = 0; i < route.length; i++) {
						Order o = route[i];
						if (o.id == 0) {
							if (i + 1 < route.length) {
								r++;
								Color c = ColorGenerator.getColor(routeId);
								routeId++;
								this.routes.add(new VisualiserRoute(t, d, r, i + 1, c));
							}
						}
					}
				}
			}
			repaintBufferedImage();

			this.main.visualiser.setRoutes(this.routes);
		}
	}

	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentShown(ComponentEvent arg0) {
	}

	public void componentResized(ComponentEvent e) {
		updateSize(this.scrollPane.getViewport());
	}

	public void mouseClicked(MouseEvent me) {
		int x = me.getX();
		int y = me.getY();

		if (me.getButton() == 1) {

			if (this.zoom != 8) {

				this.zoom *= 2;
				setPreferredSize(new Dimension(this.viewPortWidth * this.zoom, this.viewPortHeight * this.zoom));
				repaintBufferedImage();
				this.scrollPane.revalidate();

				this.center_x = (x * 2);
				this.center_y = (y * 2);
				this.oldMaxV = this.sp_v.getMaximum();
				this.oldMaxH = this.sp_h.getMaximum();
				this.repos = true;

				this.sp_v.setValue(this.sp_v.getValue() + 1);
				this.sp_h.setValue(this.sp_h.getValue() + 1);
			}
		} else if (me.getButton() == 3) {
			if (this.zoom != 1) {

				this.zoom /= 2;
				setPreferredSize(new Dimension(this.viewPortWidth * this.zoom, this.viewPortHeight * this.zoom));
				repaintBufferedImage();
				this.scrollPane.revalidate();

				this.center_x = (x / 2);
				this.center_y = (y / 2);
				this.oldMaxV = this.sp_v.getMaximum();
				this.oldMaxH = this.sp_h.getMaximum();
				this.repos = true;

				this.sp_v.setValue(this.sp_v.getValue() + 1);
				this.sp_h.setValue(this.sp_h.getValue() + 1);
			}
		}

		this.main.visualiser.location.setText("zoom: " + this.zoom + "x");
		repaint();
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void stateChanged(ChangeEvent arg0) {
		int w = this.sp_h.getMaximum();
		int h = this.sp_v.getMaximum();

		if ((this.repos) && (this.oldMaxH != w) && (this.oldMaxV != this.sp_v.getMaximum())) {

			this.repos = false;
			int x = Math.max(0, Math.min(this.center_x - this.viewPortWidth / 2, w));
			int y = Math.max(0, Math.min(this.center_y - this.viewPortHeight / 2, h));

			this.sp_h.setValue(x);
			this.sp_v.setValue(y);
		}
	}

	public void setTextVisible(boolean textVisible) {
		this.textVisible = textVisible;
	}
}
