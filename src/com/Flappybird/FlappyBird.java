package com.Flappybird;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlappyBird extends JFrame implements ActionListener {

    private Timer timer;
    private int delay = 10;

    private int birdY = 150;
    private int birdVelocity = 0;

    private List<Pipe> pipes;

    private boolean isGameOver = false;
    private boolean spacePressed = false;

    public FlappyBird() {
        setTitle("Flappy Bird");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && !isGameOver) {
                    spacePressed = true;
                    startGame();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    spacePressed = false;
                }
            }
        });

        pipes = new ArrayList<>();

        timer = new Timer(delay, this);

        // Do not start the timer initially
    }

    private void startGame() {
        timer.start();
    }

    private void generatePipes() {
        pipes.clear();
        Random random = new Random();
        int spaceBetweenPipes = 200;

        for (int i = 0; i < 5; i++) {
            int pipeX = 400 + i * 300;
            int pipeHeight = random.nextInt(300) + 50;
            pipes.add(new Pipe(pipeX, 0, pipeHeight));
            pipes.add(new Pipe(pipeX, pipeHeight + spaceBetweenPipes, getHeight() - pipeHeight - spaceBetweenPipes));
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            handleBirdJump();
            moveBird();
            movePipes();
            checkCollision();
            repaint();
        }
    }

    private void handleBirdJump() {
        if (spacePressed) {
            birdJump();
        }
    }

    private void birdJump() {
        birdVelocity = -5;
    }

    private void moveBird() {
        birdY += birdVelocity;
        birdVelocity += 1;
    }

    private void movePipes() {
        if (!pipes.isEmpty()) {
            for (Pipe pipe : pipes) {
                pipe.move();
            }

            if (pipes.get(0).getX() + pipes.get(0).getWidth() <= 0) {
                generatePipes();
            }
        }
    }

    private void checkCollision() {
        for (Pipe pipe : pipes) {
            if (pipe.intersects(100, birdY, 30, 30)) {
                gameOver();
                return;
            }
        }

        if (birdY >= getHeight() - 30 || birdY <= 0) {
            gameOver();
        }
    }

    private void gameOver() {
        isGameOver = true;
        timer.stop();
        int option = JOptionPane.showOptionDialog(this, "Game Over! Play again?", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            System.exit(0);
        }
    }

    private void restartGame() {
        isGameOver = false;
        birdY = 150;
        birdVelocity = 0;
        generatePipes();
        timer.restart();
    }

    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.BLUE);
        g.fillRect(100, birdY, 30, 30);

        g.setColor(Color.GREEN);
        for (Pipe pipe : pipes) {
            g.fillRect(pipe.getX(), pipe.getY(), pipe.getWidth(), pipe.getHeight());
        }

        Toolkit.getDefaultToolkit().sync();
    }

    public static void main(String[] args) {
        FlappyBird game = new FlappyBird();
        game.setVisible(true);
    }

    private static class Pipe {
        private int x, y, width, height;
        private static final int PIPE_SPEED = 2;

        public Pipe(int x, int y, int height) {
            this.x = x;
            this.y = y;
            this.width = 50;
            this.height = height;
        }

        public void move() {
            x -= PIPE_SPEED;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public boolean intersects(int birdX, int birdY, int birdWidth, int birdHeight) {
            return birdX + birdWidth > x && birdX < x + width && birdY + birdHeight > y && birdY < y + height;
        }
    }
}