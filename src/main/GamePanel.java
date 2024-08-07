package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import entity.Player;
import object.SuperObject;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {
	// Works as game screen
	// Screen settings
	final int originalTileSize = 16; // 16x16 tile
	final int scale = 3;

	public final int tileSize = originalTileSize * scale; // 48x48 tile
	public final int maxScreenCol = 16;
	public final int maxScreenRow = 12; // ratio 4*3
	public final int screenWidth = tileSize * maxScreenCol; // 768 pixels
	public final int screenHeight = tileSize * maxScreenRow; // 576 pixels

	// World Settings
	public final int maxWorldCol = 50;
	public final int maxWorldRow = 50;

	// FPS
	int FPS = 60;

	TileManager tileM = new TileManager(this);
	KeyHandler keyH = new KeyHandler();
	Sound music = new Sound();
	Sound se = new Sound();
	public CollisionChecker cChecker = new CollisionChecker(this);
	public AssetSetter aSetter = new AssetSetter(this);
	public UI ui = new UI(this);

	// Game Time
	Thread gameThread; // Thread is something you can start and stop

	// Entity and Object
	public Player player = new Player(this, keyH);

	public SuperObject obj[] = new SuperObject[10];

	// Constructor
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true); // If set to true, all the drawing will be done in an offscreen painting buffer
		this.addKeyListener(keyH);
		this.setFocusable(true);

	}

	// Call before game starts
	public void setupGame() {
		aSetter.setObject();
		playMusic(0);
	}

	public void startGameThread() {

		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
//Sleep method
//	public void run() {
//
//		double drawInterval = 1000000000 / FPS; // 0.01666 seconds
//		double nextDrawTime = System.nanoTime() + drawInterval;
//
//		// Create game loop - core of game
//		while (gameThread != null) {
//
//			// UPDATE: update information such as character positions
//			update();
//			// Draw: draw the screen with the updated information
//			repaint();
//
//			try {
//				double remainingTime = nextDrawTime - System.nanoTime();
//				remainingTime = remainingTime / 1000000; // Must convert for the following function sleep - accepts mili
//															// not nano
//
//				if (remainingTime < 0) {
//					remainingTime = 0;
//				}
//
//				Thread.sleep((long) remainingTime); // Pauses gameloop until sleeptime is over
//
//				nextDrawTime += drawInterval;
//
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//	}

//Delta  method
	public void run() {

		double drawInterval = 1000000000 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;

		while (gameThread != null) {
			// Check current time
			currentTime = System.nanoTime();
			// How much time has passed divided by interval
			delta += (currentTime - lastTime) / drawInterval;
			// Current time becomes last time
			lastTime = currentTime;

			if (delta >= 1) {
				update();
				repaint();
				delta--;
			}

		}
	}

	public void update() {
		player.update();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		// Debug
		long drawStart = 0;
		if (keyH.checkDrawTime == true) {
			drawStart = System.nanoTime();
		}

		// Tile
		tileM.draw(g2);

		// Object
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] != null) {
				obj[i].draw(g2, this);
			}
		}

		// Player
		player.draw(g2);

		// UI
		ui.draw(g2);

		// Debug
		if (keyH.checkDrawTime == true) {
			long drawEnd = System.nanoTime();
			long passed = drawEnd - drawStart;
			g2.setColor(Color.white);
			g2.drawString("Draw Time: " + passed, 10, 400);
			System.out.println("Draw Time: " + passed);
		}

		g2.dispose();
	}

	public void playMusic(int i) {
		music.setFile(i);
		music.play();
		music.loop();
	}

	public void stopMusic() {
		music.stop();
	}

	public void playSE(int i) {
		se.setFile(i);
		se.play();
	}
}
