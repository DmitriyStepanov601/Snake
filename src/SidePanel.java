import javax.swing.*;
import java.awt.*;

/**
 * A class that describes the game's statistics and controls
 * @author Dmitriy Stepanov
 */
public class SidePanel extends JPanel {
	private static final Font LARGE_FONT = new Font("Tahoma", Font.BOLD, 20);
	private static final Font MEDIUM_FONT = new Font("Tahoma", Font.BOLD, 16);
	private static final Font SMALL_FONT = new Font("Tahoma", Font.BOLD, 12);
	private final Snake game;

	/**
	 * Constructor - creating a new side panel
	 * @param game - screen game
	 * @see SidePanel#SidePanel(Snake)
	 */
	public SidePanel(Snake game) {
		this.game = game;
		setPreferredSize(new Dimension(300, Field.ROW_COUNT * Field.TILE_SIZE));
		setBackground(Color.BLACK);
	}
	
	private static final int STATISTICS_OFFSET = 150;
	private static final int CONTROLS_OFFSET = 320;
	private static final int MESSAGE_STRIDE = 30;
	private static final int SMALL_OFFSET = 30;
	private static final int LARGE_OFFSET = 50;
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.setFont(LARGE_FONT);
		g.drawString("Snake Game",
				getWidth() / 2 - g.getFontMetrics().stringWidth("Snake Game") / 2, 50);
		g.setFont(MEDIUM_FONT);
		g.drawString("Statistics", SMALL_OFFSET, STATISTICS_OFFSET);
		g.drawString("Controls", SMALL_OFFSET, CONTROLS_OFFSET);
		g.setFont(SMALL_FONT);
		int drawY = STATISTICS_OFFSET;
		g.drawString("Total Score: " + game.getScore(), LARGE_OFFSET, drawY += MESSAGE_STRIDE);
		g.drawString("Fruit Eaten: " + game.getFruitsEaten(), LARGE_OFFSET, drawY += MESSAGE_STRIDE);
		g.drawString("Fruit Score: " + game.getNextFruitScore(), LARGE_OFFSET, drawY += MESSAGE_STRIDE);
		drawY = CONTROLS_OFFSET;
		g.drawString("Move Up: W / Up Arrow", LARGE_OFFSET, drawY += MESSAGE_STRIDE);
		g.drawString("Move Down: S / Down Arrow", LARGE_OFFSET, drawY += MESSAGE_STRIDE);
		g.drawString("Move Left: A / Left Arrow", LARGE_OFFSET, drawY += MESSAGE_STRIDE);
		g.drawString("Move Right: D / Right Arrow", LARGE_OFFSET, drawY += MESSAGE_STRIDE);
		g.drawString("Pause Game: P", LARGE_OFFSET, drawY += MESSAGE_STRIDE);
	}
}