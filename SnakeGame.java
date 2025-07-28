import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int TILE_SIZE = 20;
    private final int WIDTH = 600, HEIGHT = 400;
    private final int DELAY = 100;

    private int[] x = new int[WIDTH * HEIGHT / TILE_SIZE];
    private int[] y = new int[WIDTH * HEIGHT / TILE_SIZE];
    private int bodyParts = 3;
    private int appleX, appleY;
    private boolean running = false;
    private Timer timer;
    private char direction = 'R';

    private JFrame frame;
    private JPanel gameOverPanel;
    private JButton restartButton, exitButton;

    public SnakeGame(JFrame frame) {
        this.frame = frame;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);
        createGameOverPanel();
        startGame();
    }

    public void startGame() {
        spawnApple();
        running = true;
        bodyParts = 3;
        direction = 'R';
        gameOverPanel.setVisible(false);
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void spawnApple() {
        appleX = (int) (Math.random() * (WIDTH / TILE_SIZE)) * TILE_SIZE;
        appleY = (int) (Math.random() * (HEIGHT / TILE_SIZE)) * TILE_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] -= TILE_SIZE;
                break;
            case 'D':
                y[0] += TILE_SIZE;
                break;
            case 'L':
                x[0] -= TILE_SIZE;
                break;
            case 'R':
                x[0] += TILE_SIZE;
                break;
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
            showGameOverScreen();
        }
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            spawnApple();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            g.setColor(Color.RED);
            g.fillRect(appleX, appleY, TILE_SIZE, TILE_SIZE);
            for (int i = 0; i < bodyParts; i++) {
                g.setColor(i == 0 ? Color.GREEN : Color.YELLOW);
                g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
            }
        } else {
            gameOver(g);
        }
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2 - 30);
    }

    public void showGameOverScreen() {
        gameOverPanel.setVisible(true);
    }

    private void createGameOverPanel() {
        gameOverPanel = new JPanel();
        gameOverPanel.setLayout(new FlowLayout());
        gameOverPanel.setBounds(WIDTH / 2 - 110, HEIGHT / 2, 220, 60);
        gameOverPanel.setVisible(false);
        gameOverPanel.setOpaque(false); // Deixa transparente

        restartButton = createStyledButton("Restart", new Color(0, 180, 0), new Color(0, 255, 0));
        restartButton.addActionListener(e -> restartGame());

        exitButton = createStyledButton("Exit", new Color(180, 0, 0), new Color(255, 0, 0));
        exitButton.addActionListener(e -> System.exit(0));

        gameOverPanel.add(restartButton);
        gameOverPanel.add(exitButton);
        this.setLayout(null);
        this.add(gameOverPanel);
    }

    private JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setPreferredSize(new Dimension(100, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efeito hover (passar o mouse)
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }

    private void restartGame() {
        running = true;
        x = new int[WIDTH * HEIGHT / TILE_SIZE];
        y = new int[WIDTH * HEIGHT / TILE_SIZE];
        startGame();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R')
                    direction = 'L';
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L')
                    direction = 'R';
                break;
            case KeyEvent.VK_UP:
                if (direction != 'D')
                    direction = 'U';
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U')
                    direction = 'D';
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame(frame);
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
