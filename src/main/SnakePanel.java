package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

public class SnakePanel extends JPanel implements Runnable {

    Thread gameLoop;
    Random random = new Random();
    static ArrayList<Point> snake = new ArrayList<>();
    static ArrayList<Point> treats = new ArrayList<>();
    static int[][] grid = new int[30][30];
    Point head = new Point();
    static int heading, counter, health;
    static boolean loop, gameOver;

    public SnakePanel() {

        reset();
        drawGameScreen();

        InputMap[] inputMaps = new InputMap[]{
                this.getInputMap(JComponent.WHEN_FOCUSED),
                this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT),
                this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW),
        };
        for (InputMap i : inputMaps) {
            i.put(KeyStroke.getKeyStroke("UP"), "up");
            i.put(KeyStroke.getKeyStroke("DOWN"), "down");
            i.put(KeyStroke.getKeyStroke("LEFT"), "left");
            i.put(KeyStroke.getKeyStroke("RIGHT"), "right");
            i.put(KeyStroke.getKeyStroke("control G"), "grid");
        }
        this.getActionMap().put("up", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (heading != 3) {
                    heading = 1;
                }
            }
        });
        this.getActionMap().put("down", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (heading != 1) {
                    heading = 3;
                }
            }
        });
        this.getActionMap().put("left", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (heading != 2) {
                    heading = 4;
                }
            }
        });
        this.getActionMap().put("right", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (heading != 4) {
                    heading = 2;
                }
            }
        });
        this.getActionMap().put("grid", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.print("\n===========================================================");
                for (int i = 0; i < grid.length; i++) {
                    System.out.println("");
                    for (int j = 0; j < grid[i].length; j++) {
                        System.out.print(grid[j][i] + " ");
                    }
                }
            }
        });
        if (loop) {
            gameLoop = new Thread(this);
            gameLoop.start();
            System.out.println("thread");
            loop = false;
        }
    }

    public void reset() {

        head.setLocation(280, 280);
        heading = 1;
        health = 70;
        gameOver = false;
        loop = true;
        snake.clear();
        treats.clear();
        counter = 0;
        do {
            addTreat();
            addTreat();
        } while (treats.isEmpty());
    }

    public void drawGameScreen() {

        this.removeAll();
        this.setPreferredSize(new Dimension(600, 620));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
    }

    public void step() {
        if (snake.size() > 0) {
            if (grid[head.x / 20][head.y / 20] != 2) {
                grid[snake.get(0).x / 20][snake.get(0).y / 20] = 0;
                snake.remove(0);
                health--;
            } else if (grid[head.x / 20][head.y / 20] == 2){
                treats.removeIf(p -> p.equals(head));
                addTreat();
                health = 70;
                counter = 1;
            }
        }
        if (!isGameOver()) {
            grid[head.x / 20][head.y / 20] = 1;
            snake.add(head);
        }
    }

    public void drawSnake(Graphics2D g) {
        for (Point p : snake) {
            if (health < 10) {
                g.setColor(Color.RED);
            } else if (health < 20) {
                g.setColor(new Color(255, 144, 0));
            } else if (health < 30) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.GREEN);
            }
            g.fillRect(p.x, p.y, 20, 20);
            g.setColor(Color.BLACK);
            g.drawRect(p.x, p.y, 20, 20);
        }
    }

    public void addTreat() {

        int x = random.nextInt(30) * 20;
        int y = random.nextInt(30) * 20;

        if (grid[x / 20][y / 20] == 0) {
            grid[x / 20][y / 20] = 2;
            treats.add(new Point(x, y));
        } else {
            addTreat();
        }
    }

    public void drawTreats(Graphics2D g) {

        if (counter % 50 == 0) {
            if (treats.size() > 1) {
                grid[treats.get(0).x / 20][treats.get(0).y / 20] = 0;
                treats.remove(0);
                addTreat();
            }
        }
        for (Point p : treats) {
            g.setColor(Color.RED);
            g.fillRect(p.x, p.y, 20, 20);
            g.setColor(Color.BLACK);
            g.drawRect(p.x, p.y, 20, 20);
            g.fillRect(p.x + 8, p.y, 4, 4);
        }
        for (Point p : treats) {
            Graphics2D gg = (Graphics2D) g.create();
            gg.setColor(new Color(0, 130, 0));
            gg.rotate(180, p.x + 14, p.y -3);
            gg.fillOval(p.x + 12, p.y -6, 5, 9);
            gg.dispose();
        }
        counter++;
    }

    public void drawScore(Graphics2D g) {

        g.setColor(Color.WHITE);
        g.fillRect(0, 600, 600, 20);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Times New Roman", Font.BOLD, 19));
        String score = "Score: " + (snake.size() - 1) * 10;
        g.drawString(score, 10, 616);
        String hp = "Health: " + health;
        g.drawString(hp, 505, 615);
    }

    public boolean isGameOver() {

        if (health <= 0) {
            gameOver = true;
            return true;
        }
        for (Point p : snake) {
            if (p.equals(head)) {
                gameOver = true;
                return true;
            }
        }
        return false;
    }

    public void drawEndScreen() {

        JLayeredPane mainPanel = new JLayeredPane();
        mainPanel.setBounds(0, 0, 620, 620);
        mainPanel.setLayout(null);
        add(mainPanel);

        JPanel outerPanel = new JPanel();
        outerPanel.setBounds(0, 0, 620, 620);
        outerPanel.setBackground(Color.BLACK);
        outerPanel.setLayout(null);
        mainPanel.add(outerPanel, JLayeredPane.DEFAULT_LAYER);

        JLabel gameOverLabel = new JLabel();
        gameOverLabel.setBounds(134, 150, 400, 50);
        gameOverLabel.setText("GAME OVER");
        gameOverLabel.setFont(new Font("Times New Roman", Font.BOLD, 50));
        gameOverLabel.setForeground(Color.WHITE);
        mainPanel.add(gameOverLabel, JLayeredPane.PALETTE_LAYER);

        JButton retry = new JButton();
        retry.setBounds(195, 320, 200, 50);
        retry.setText("Retry");
        retry.setFont(new Font("Times New Roman", Font.BOLD, 30));
        retry.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLoop.interrupt();
                reset();
                drawGameScreen();
                repaint();
            }
        });
        mainPanel.add(retry, JLayeredPane.PALETTE_LAYER);

        InputMap[] inputMaps = new InputMap[]{
                mainPanel.getInputMap(JComponent.WHEN_FOCUSED),
                mainPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT),
                mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW),
        };
        for (InputMap i : inputMaps) {
            i.put(KeyStroke.getKeyStroke("SPACE"), "retry");
        }
        mainPanel.getActionMap().put("retry", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                gameLoop.interrupt();
                reset();
                drawGameScreen();
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (gameOver) {
            drawEndScreen();
        } else {
            drawSnake(g);
            drawTreats(g);
            drawScore(g);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(120);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            head = new Point(head.x, head.y);
            switch (heading) {
                case 0:
                    continue;
                case 1:
                    if (head.y < 20) {
                        head.y = 580;
                    } else {
                        head.y -= 20;
                    }
                    break;
                case 2:
                    if (head.x > 560) {
                        head.x = 0;
                    } else {
                        head.x += 20;
                    }
                    break;
                case 3:
                    if (head.y > 560) {
                        head.y = 0;
                    } else {
                        head.y += 20;
                    }
                    break;
                case 4:
                    if (head.x < 20) {
                        head.x = 580;
                    } else {
                        head.x -= 20;
                    }
            }
            step();
            repaint();
        }
    }
}
