package me.letsgo00.crappybird;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;

public class Main {

    JFrame frame = new JFrame("CrappyBird");

    public static void main(String[] args) {
        new Main();
    }

    public Main(){
        DrawPane p = new DrawPane();
        Thread thread = new Thread(p);
        thread.start();
        frame.setContentPane(p);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()){
                    case 32:
                        p.up();
                        break;
                    case 10:
                        p.start();
                        break;
                    default:
                        System.out.println(e.getKeyCode());
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
        frame.setVisible(true);
    }

    private class DrawPane extends JPanel implements Runnable{
        int x = 60, y = 200;
        float gravity = 0.25f;
        float velocity = 0.0f;
        float lift = 12;

        int score = 0;

        boolean isRunning = false;

        LinkedList<Pipe> pipes = new LinkedList<>();

        public DrawPane(){
            isRunning = true;
        }

        public void paint(Graphics g){
            g.clearRect(0,0,getWidth(), getHeight());
            velocity += gravity;
            velocity *= 0.9;
            y += velocity;

            if(y >= getHeight() - 10){
                y = getHeight() - 10;
                velocity = 0;
            }
            if(y < 0){
                y = 0;
                velocity = 0;
            }
            g.setColor(new Color(156,39,176));
            g.fillRoundRect(x,y,15,15,10,10);
            g.setColor(new Color(63, 81, 181));
            generatePipes(g);
            drawPoints(g);
        }

        void generatePipes(Graphics g){
            if(pipes.size() > 0 && pipes.getLast().getX() < getWidth() - 100){
                pipes.addLast(new Pipe(getWidth() + 5, getHeight()));
            }

            if(pipes.size() == 0){
                pipes.addLast(new Pipe(getWidth() + 5, getHeight()));
            }

            for (Pipe p : ((LinkedList<Pipe>) pipes.clone())){
                if (p.getX() < 0)
                    pipes.remove(p);
                if(p.getX() == 60){
                    if(this.y > p.getHeight() && this.y < p.getHeight() + p.getGap() && isRunning){
                        score++;
                    }
                    else{
                        //Game over
                        end(g);
                    }
                }
                p.paint(g);
                if(!isRunning)
                    end(g);
                p.move(-0.5f);
            }
        }

        void drawPoints(Graphics g){
            g.setColor(new Color(0,150,136));
            g.setFont(new Font("TimesRoman", Font.BOLD, 16));
            g.drawString("Score: " + score,20,20);
        }

        public void start(){
            if(isRunning) return;
            isRunning = true;
            score = 0;
            pipes.clear();
        }

        void end(Graphics g){
            isRunning = false;
            g.setColor(new Color(255,193,7));
            g.fillRect(getWidth() / 2 - 80, getHeight() / 2 - 65, 170, 70);
            g.setColor(new Color(233,30,99));
            g.setFont(new Font("TimesRoman", Font.BOLD, 25));
            g.drawString("GAME OVER", getWidth() / 2 - 70, getHeight() / 2 - 30);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 15));
            g.drawString("Press ENTER to start", getWidth() / 2 - 70, getHeight() / 2);
            g.setColor(Color.red);
        }

        void debug(Graphics g){
            g.setColor(Color.red);
            g.drawString("Height: " + getHeight(),20,20);
            g.drawString("Width: " + getWidth(),20,40);
        }

        public void up(){
            velocity -= lift;
        }

        @Override
        public void run() {
            while (true){
                if(isRunning) repaint();
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Pipe{
        float x = 0;
        int height = 0;
        int gap = 70;
        int thickness = 10;
        int frameHeight = 0;

        public Pipe(int x, int frameHeight){
            this.x = x;
            this.frameHeight = frameHeight;
            height = new Random().nextInt(x-30);
        }

        public void paint(Graphics g){
            if(x < 0) return;
            g.fillRect((int) x,0, thickness, height);
            g.fillRect((int) x,height + gap, thickness, frameHeight);
        }

        public void move(float amount){
            x += amount;
        }

        public float getX() {
            return x;
        }

        public int getHeight() {
            return height;
        }

        public int getGap() {
            return gap;
        }
    }
}
