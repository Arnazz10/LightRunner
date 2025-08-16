import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SpaceShooter extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private boolean left, right, space;
    private int playerX = 250;
    private final int playerWidth = 40;
    private final int playerHeight = 20;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();

    public SpaceShooter() {
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        timer = new Timer(15, this);
        timer.start();

        for (int i = 0; i < 5; i++) {
            enemies.add(new Enemy(100 * i + 50, 30));
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GREEN);
        g.fillRect(playerX, 350, playerWidth, playerHeight);

        g.setColor(Color.YELLOW);
        for (Bullet b : bullets) {
            g.fillRect(b.x, b.y, 5, 10);
        }

        g.setColor(Color.RED);
        for (Enemy e : enemies) {
            g.fillRect(e.x, e.y, e.size, e.size);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (left && playerX > 0) playerX -= 5;
        if (right && playerX < getWidth() - playerWidth) playerX += 5;
        if (space) bullets.add(new Bullet(playerX + playerWidth / 2, 340));
        space = false;

        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            b.y -= 10;
            if (b.y < 0) bullets.remove(i);
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy e1 = enemies.get(i);
            for (int j = 0; j < bullets.size(); j++) {
                Bullet b = bullets.get(j);
                if (new Rectangle(e1.x, e1.y, e1.size, e1.size).intersects(new Rectangle(b.x, b.y, 5, 10))) {
                    enemies.remove(i);
                    bullets.remove(j);
                    break;
                }
            }
        }

        repaint();
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) left = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) right = true;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) space = true;
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) left = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) right = false;
    }

    public void keyTyped(KeyEvent e) {}

    static class Bullet {
        int x, y;
        Bullet(int x, int y) { this.x = x; this.y = y; }
    }

    static class Enemy {
        int x, y, size = 30;
        Enemy(int x, int y) { this.x = x; this.y = y; }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Space Shooter");
        SpaceShooter game = new SpaceShooter();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
