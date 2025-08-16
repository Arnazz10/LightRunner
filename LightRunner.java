import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class LightRunner extends JPanel implements ActionListener, KeyListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;
    private static final int PLAYER_SIZE = 30;
    private static final int OBSTACLE_WIDTH = 20;
    private static final int OBSTACLE_HEIGHT = 30;
    private static final int GROUND_Y = HEIGHT - 50;
    
    private Timer timer;
    private boolean gameRunning = true;
    private boolean gameOver = false;
    
    // Player
    private int playerX = 100;
    private int playerY = GROUND_Y - PLAYER_SIZE;
    private int playerVelocityY = 0;
    private boolean onGround = true;
    
    // Obstacles
    private ArrayList<Rectangle> obstacles;
    private Random random;
    
    // Game stats
    private int score = 0;
    private int highScore = 0;
    
    public LightRunner() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(25, 25, 35));
        setFocusable(true);
        addKeyListener(this);
        
        obstacles = new ArrayList<>();
        random = new Random();
        
        timer = new Timer(16, this); // ~60 FPS
        timer.start();
        
        spawnObstacle();
    }
    
    private void spawnObstacle() {
        int x = WIDTH + random.nextInt(200);
        int y = GROUND_Y - OBSTACLE_HEIGHT;
        obstacles.add(new Rectangle(x, y, OBSTACLE_WIDTH, OBSTACLE_HEIGHT));
    }
    
    private void update() {
        if (!gameRunning) return;
        
        // Update player physics
        if (!onGround) {
            playerVelocityY += 1; // Gravity
        }
        playerY += playerVelocityY;
        
        // Ground collision
        if (playerY >= GROUND_Y - PLAYER_SIZE) {
            playerY = GROUND_Y - PLAYER_SIZE;
            playerVelocityY = 0;
            onGround = true;
        }
        
        // Update obstacles
        for (int i = obstacles.size() - 1; i >= 0; i--) {
            Rectangle obstacle = obstacles.get(i);
            obstacle.x -= 5; // Move left
            
            // Remove obstacles that are off screen
            if (obstacle.x + obstacle.width < 0) {
                obstacles.remove(i);
                score++;
                if (score % 5 == 0) {
                    spawnObstacle();
                }
            }
            
            // Collision detection
            if (playerX < obstacle.x + obstacle.width &&
                playerX + PLAYER_SIZE > obstacle.x &&
                playerY < obstacle.y + obstacle.height &&
                playerY + PLAYER_SIZE > obstacle.y) {
                gameOver = true;
                gameRunning = false;
                if (score > highScore) {
                    highScore = score;
                }
            }
        }
        
        // Spawn new obstacles
        if (obstacles.size() < 3) {
            spawnObstacle();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw gradient background
        GradientPaint gradient = new GradientPaint(0, 0, new Color(25, 25, 35), 0, HEIGHT, new Color(45, 45, 65));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        
        // Draw ground
        g2d.setColor(new Color(60, 60, 80));
        g2d.fillRect(0, GROUND_Y, WIDTH, HEIGHT - GROUND_Y);
        
        // Draw player
        g2d.setColor(new Color(100, 150, 255));
        g2d.fillRoundRect(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE, 8, 8);
        
        // Draw player eyes
        g2d.setColor(Color.WHITE);
        g2d.fillOval(playerX + 8, playerY + 8, 4, 4);
        g2d.fillOval(playerX + 18, playerY + 8, 4, 4);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(playerX + 9, playerY + 9, 2, 2);
        g2d.fillOval(playerX + 19, playerY + 9, 2, 2);
        
        // Draw obstacles
        g2d.setColor(new Color(255, 100, 100));
        for (Rectangle obstacle : obstacles) {
            g2d.fillRoundRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height, 4, 4);
        }
        
        // Draw score
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Score: " + score, 20, 30);
        g2d.drawString("High Score: " + highScore, 20, 55);
        
        // Draw game over screen
        if (gameOver) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 40));
            String gameOverText = "GAME OVER";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (WIDTH - fm.stringWidth(gameOverText)) / 2;
            g2d.drawString(gameOverText, textX, HEIGHT / 2 - 20);
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            String restartText = "Press R to restart";
            fm = g2d.getFontMetrics();
            textX = (WIDTH - fm.stringWidth(restartText)) / 2;
            g2d.drawString(restartText, textX, HEIGHT / 2 + 20);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && onGround && gameRunning) {
            playerVelocityY = -15;
            onGround = false;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            restart();
        }
    }
    
    private void restart() {
        gameRunning = true;
        gameOver = false;
        playerX = 100;
        playerY = GROUND_Y - PLAYER_SIZE;
        playerVelocityY = 0;
        onGround = true;
        obstacles.clear();
        score = 0;
        spawnObstacle();
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Light Runner");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new LightRunner());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
