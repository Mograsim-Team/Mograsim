package net.mograsim.logic.core.tests;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.mograsim.logic.core.components.CoreManualSwitch;
import net.mograsim.logic.core.components.gates.CoreNotGate;
import net.mograsim.logic.core.components.gates.CoreOrGate;
import net.mograsim.logic.core.timeline.Timeline;
import net.mograsim.logic.core.timeline.Timeline.ExecutionResult;
import net.mograsim.logic.core.wires.CoreWire;

public class GUITest extends JPanel
{

	private static final long serialVersionUID = 1L;

	private static final int WIRE_DELAY = 40;
	private static final int OR_DELAY = 100;
	private static final int NOT_DELAY = 100;

	private Timeline t = new Timeline(11);

	CoreWire r = new CoreWire(t, 1, WIRE_DELAY);
	CoreWire s = new CoreWire(t, 1, WIRE_DELAY);
	CoreWire t1 = new CoreWire(t, 1, WIRE_DELAY);
	CoreWire t2 = new CoreWire(t, 1, WIRE_DELAY);
	CoreWire q = new CoreWire(t, 1, WIRE_DELAY);
	CoreWire nq = new CoreWire(t, 1, WIRE_DELAY);

	CoreManualSwitch rIn = new CoreManualSwitch(t, r.createReadWriteEnd());
	CoreManualSwitch sIn = new CoreManualSwitch(t, s.createReadWriteEnd());

	CoreOrGate or1 = new CoreOrGate(t, OR_DELAY, t2.createReadWriteEnd(), r.createReadOnlyEnd(), nq.createReadOnlyEnd());
	CoreOrGate or2 = new CoreOrGate(t, OR_DELAY, t1.createReadWriteEnd(), s.createReadOnlyEnd(), q.createReadOnlyEnd());
	CoreNotGate not1 = new CoreNotGate(t, NOT_DELAY, t2.createReadOnlyEnd(), q.createReadWriteEnd());
	CoreNotGate not2 = new CoreNotGate(t, NOT_DELAY, t1.createReadOnlyEnd(), nq.createReadWriteEnd());

	Map<CoreManualSwitch, Rectangle> switchMap = new HashMap<>();

	int height;
	int width;
	boolean sizeChanged;

	public GUITest()
	{
		addMouseListener(new MouseListener()
		{

			@Override
			public void mouseReleased(MouseEvent e)
			{
				for (Entry<CoreManualSwitch, Rectangle> dim : switchMap.entrySet())
				{
					if (dim.getValue().contains(e.getPoint()))
					{
						dim.getKey().switchFullOff();
						repaint();
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				for (Entry<CoreManualSwitch, Rectangle> dim : switchMap.entrySet())
				{
					if (dim.getValue().contains(e.getPoint()))
					{
						dim.getKey().switchFullOn();
						repaint();
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				// none
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				// none
			}

			@Override
			public void mouseClicked(MouseEvent e)
			{
				// If you want toggle buttons, use this code instead
//				for (Entry<ManualSwitch, Rectangle> dim : switchMap.entrySet()) {
//					if (dim.getValue().contains(e.getPoint())) {
//						dim.getKey().toggle();
//						repaint();
//					}
//				}
			}
		});
	}

	public Timeline getTimeline()
	{
		return t;
	}

	@Override
	public void paint(Graphics some_g)
	{
		super.paint(some_g);
		Graphics2D g = ((Graphics2D) some_g);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

		checkSizeChange();
		adaptFont(g);

		drawWire(g, r, "r", 2, 9, 4, 9);

		drawWire(g, s, "s", 2, 3, 4, 3);

		drawWire(g, t2, "t2", 5, 8.5, 6, 8.5);

		drawWire(g, t1, "t1", 5, 3.5, 6, 3.5);

		drawWire(g, q, "q", 7, 8.5, 9, 8.5);

		drawWire(g, nq, "nq", 7, 3.5, 9, 3.5);

		drawWire(g, q, "", 7.5, 8.5, 7.5, 7.5);
		drawWire(g, q, "", 7.5, 7.5, 3, 4.5);
		drawWire(g, q, "", 3, 4.5, 3, 4);
		drawWire(g, q, "q", 3, 4, 4, 4);

		drawWire(g, nq, "", 7.5, 3.5, 7.5, 4.5);
		drawWire(g, nq, "", 7.5, 4.5, 3, 7.5);
		drawWire(g, nq, "", 3, 7.5, 3, 8);
		drawWire(g, nq, "nq", 3, 8, 4, 8);

		drawSquare(g, 4, 8, "OR");
		drawSquare(g, 4, 3, "OR");

		drawSquare(g, 6, 8, "NOT");
		drawSquare(g, 6, 3, "NOT");

		drawSwitch(g, rIn, "Switch R", 0.5, 8.25, 2, 9.75);
		drawSwitch(g, sIn, "Switch S", 0.5, 2.25, 2, 3.75);

		drawString(g, "Hint: drag the cursor out of the pressed switch to keep it's state", 5, 0, 0.0, 1.0);
	}

	private void checkSizeChange()
	{
		sizeChanged = height != getHeight() || width != getWidth();
		if (sizeChanged)
		{
			height = getHeight();
			width = getWidth();
		}
	}

	private void adaptFont(Graphics g)
	{
		g.setFont(g.getFont().deriveFont(Math.min(height, width) / 40f));
	}

	@SuppressWarnings("static-method")
	private void drawString(Graphics g, String s, int x, int y, double anchorX, double anchorY)
	{
		int h = g.getFontMetrics().getAscent();
		int w = g.getFontMetrics().stringWidth(s);
		g.drawString(s, x - (int) (w * anchorX), y + (int) (h * anchorY));
	}

	private void drawWire(Graphics g, CoreWire wa, String name, double x1, double y1, double x2, double y2)
	{
		setTo(g, wa);
		g.drawLine(gX(x1), gY(y1), gX(x2), gY(y2));
		drawString(g, name, (gX(x1) + gX(x2)) / 2, (gY(y1) + gY(y2)) / 2 - 5, 0, 0);
	}

	private void drawSquare(Graphics g, int posX, int posY, String text)
	{
		int x1 = gX(posX) - 5;
		int x2 = gX(posX + 1) + 5;
		int y1 = gY(posY) - 5;
		int y2 = gY(posY + 1) + 5;

		g.setColor(Color.WHITE);
		g.fillRect(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
		setBlack(g);
		g.drawRect(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
		drawString(g, text, (x1 + x2) / 2, (y1 + y2) / 2, 0.5, 0.5);

	}

	private void drawSwitch(Graphics g, CoreManualSwitch ms, String text, double posX1, double posY1, double posX2, double posY2)
	{
		int x1 = gX(posX1) - 5;
		int x2 = gX(posX2) + 5;
		int y1 = gY(posY1) - 5;
		int y2 = gY(posY2) + 5;

		if (sizeChanged)
		{
			Rectangle r = new Rectangle(x1, y1, x2 - x1, y2 - y1);
			switchMap.put(ms, r);
		}

		g.setColor(ms.isFullOn() ? Color.getHSBColor(.3f, .5f, 1f) : Color.WHITE);
		g.fillRect(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
		setBlack(g);
		g.drawRect(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
		drawString(g, text, (x1 + x2) / 2, (y1 + y2) / 2, 0.5, 0.5);
	}

	private static void setBlack(Graphics g)
	{
		g.setColor(Color.BLACK);
	}

	private static void setTo(Graphics g, CoreWire wa)
	{
		switch (wa.getValue())
		{
		case ONE:
			g.setColor(Color.GREEN);
			break;
		case X:
			g.setColor(Color.RED);
			break;
		case Z:
			g.setColor(Color.DARK_GRAY);
			break;
		case ZERO:
			g.setColor(Color.BLACK);
			break;
		case U:
			g.setColor(Color.MAGENTA);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	private int gY(double pos)
	{
		return (int) (pos * height / 11);
	}

	private int gX(double pos)
	{
		return (int) (pos * width / 11) + 50;
	}

	public static void main(String[] args)
	{
		JFrame f = new JFrame("Test circuit 1.0.0");
		GUITest gt = new GUITest();
		f.add(gt);
		f.setSize(800, 600);
		f.setLocation(500, 400);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setVisible(true);

		long begin = System.currentTimeMillis();

		long lastFrame = begin;
		long updateT = 16;

		while (f.isVisible())
		{
			ExecutionResult er = gt.getTimeline().executeUntil(gt.getTimeline().laterThan((lastFrame - begin) * 3), lastFrame + 14);
//				if (t.hasNext()) 
//				t.executeNext();
			if (er != ExecutionResult.NOTHING_DONE)
				gt.repaint(12);
			try
			{
				Thread.sleep(Math.max(updateT - System.currentTimeMillis() + lastFrame, 0));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			lastFrame = System.currentTimeMillis();
		}
	}
}
