import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * A class that describes the game interface
 * @author Dmitriy Stepanov
 */
public class Snake extends JFrame {
	private static final long FRAME_TIME = 1000L / 50L;
	private static final int MIN_SNAKE_LENGTH = 5;
	private static final int MAX_DIRECTIONS = 3;

	private final Field board;
	private final SidePanel side;
	private Random random;
	private Clock logicTimer;

	private boolean isNewGame;
	private boolean isGameOver;
	private boolean isPaused;

	private LinkedList<Point> snake;
	private LinkedList<Direction> directions;

	private int score;
	private int fruitsEaten;
	private int nextFruitScore;

	/**
	 * Constructor - creating a new snake
	 * @see Snake#Snake()
	 */
	private Snake() {
		super("Snake");
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);

		Image windowIcon = null;
		try {
			windowIcon = ImageIO.read(Snake.class.getResource("/snake.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		setIconImage(windowIcon);
		this.board = new Field(this);
		this.side = new SidePanel(this);
		add(board, BorderLayout.CENTER);
		add(side, BorderLayout.EAST);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
					case KeyEvent.VK_W:
					case KeyEvent.VK_UP:
						if(!isPaused && !isGameOver) {
							if(directions.size() < MAX_DIRECTIONS) {
								Direction last = directions.peekLast();
								if(last != Direction.South && last != Direction.North) {
									directions.addLast(Direction.North);
								}
							}
						}
						break;
					case KeyEvent.VK_S:
					case KeyEvent.VK_DOWN:
						if(!isPaused && !isGameOver) {
							if(directions.size() < MAX_DIRECTIONS) {
								Direction last = directions.peekLast();
								if(last != Direction.North && last != Direction.South) {
									directions.addLast(Direction.South);
								}
							}
						}
						break;
					case KeyEvent.VK_A:
					case KeyEvent.VK_LEFT:
						if(!isPaused && !isGameOver) {
							if(directions.size() < MAX_DIRECTIONS) {
								Direction last = directions.peekLast();
								if(last != Direction.East && last != Direction.West) {
									directions.addLast(Direction.West);
								}
							}
						}
						break;
					case KeyEvent.VK_D:
					case KeyEvent.VK_RIGHT:
						if(!isPaused && !isGameOver) {
							if(directions.size() < MAX_DIRECTIONS) {
								Direction last = directions.peekLast();
								if(last != Direction.West && last != Direction.East) {
									directions.addLast(Direction.East);
								}
							}
						}
						break;
					case KeyEvent.VK_P:
						if(!isGameOver) {
							isPaused = !isPaused;
							logicTimer.setPaused(isPaused);
						}
						break;
					case KeyEvent.VK_ENTER:
						if(isNewGame || isGameOver) {
							resetGame();
						}
						break;
				}
			}
		});
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void startGame() {
		this.random = new Random();
		this.snake = new LinkedList<>();
		this.directions = new LinkedList<>();
		this.logicTimer = new Clock(9.0f);
		this.isNewGame = true;
		logicTimer.setPaused(true);
		while(true) {
			long start = System.nanoTime();
			logicTimer.update();
			if(logicTimer.hasElapsedCycle()) {
				updateGame();
			}
			board.repaint();
			side.repaint();
			long delta = (System.nanoTime() - start) / 1000000L;
			if(delta < FRAME_TIME) {
				try {
					Thread.sleep(FRAME_TIME - delta);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void updateGame() {
		TileType collision = updateSnake();
		if(collision == TileType.Fruit) {
			fruitsEaten++;
			score += nextFruitScore;
			spawnFruit();
		} else if(collision == TileType.SnakeBody) {
			isGameOver = true;
			logicTimer.setPaused(true);
		} else if(nextFruitScore > 10) {
			nextFruitScore--;
		}
	}

	private TileType updateSnake() {
		Direction direction = directions.peekFirst();
		Point head = new Point(snake.peekFirst());
		switch(direction) {
			case North:
				head.y--;
				break;
			case South:
				head.y++;
				break;
			case West:
				head.x--;
				break;
			case East:
				head.x++;
				break;
		}
		if(head.x < 0 || head.x >= Field.COL_COUNT || head.y < 0 || head.y >= Field.ROW_COUNT) {
			return TileType.SnakeBody;
		}
		TileType old = board.getTile(head.x, head.y);
		if(old != TileType.Fruit && snake.size() > MIN_SNAKE_LENGTH) {
			Point tail = snake.removeLast();
			board.setTile(tail, null);
			old = board.getTile(head.x, head.y);
		}
		if(old != TileType.SnakeBody) {
			board.setTile(snake.peekFirst(), TileType.SnakeBody);
			snake.push(head);
			board.setTile(head, TileType.SnakeHead);
			if(directions.size() > 1) {
				directions.poll();
			}
		}
		return old;
	}

	private void resetGame() {
		this.score = 0;
		this.fruitsEaten = 0;
		this.isNewGame = false;
		this.isGameOver = false;
		Point head = new Point(Field.COL_COUNT / 2, Field.ROW_COUNT / 2);
		snake.clear();
		snake.add(head);
		board.clearBoard();
		board.setTile(head, TileType.SnakeHead);
		directions.clear();
		directions.add(Direction.North);
		logicTimer.reset();
		spawnFruit();
	}

	public boolean isNewGame() {
		return isNewGame;
	}
	public boolean isGameOver() {
		return isGameOver;
	}
	public boolean isPaused() {
		return isPaused;
	}

	private void spawnFruit() {
		this.nextFruitScore = 100;
		int index = random.nextInt(Field.COL_COUNT * Field.ROW_COUNT - snake.size());
		int freeFound = -1;
		for(int x = 0; x < Field.COL_COUNT; x++) {
			for(int y = 0; y < Field.ROW_COUNT; y++) {
				TileType type = board.getTile(x, y);

				if(type == null || type == TileType.Fruit) {
					if(++freeFound == index) {
						board.setTile(x, y, TileType.Fruit);
						break;
					}
				}
			}
		}
	}

	public int getScore() {
		return score;
	}
	public int getFruitsEaten() {
		return fruitsEaten;
	}
	public int getNextFruitScore() {
		return nextFruitScore;
	}
	public Direction getDirection() {
		return directions.peek();
	}

	public static void main(String[] args) {
		Snake snake = new Snake();
		snake.startGame();
	}
}
