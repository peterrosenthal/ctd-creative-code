import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Poster extends PApplet {

    public int canvasWidth  = 500;
    public int canvasHeight = 500;

    public int simulationWidth  = 20;
    public int simulationHeight = 20;

    public int startingSnakes = 10;

    public ArrayList<SnakeBoid> snakeBoids;

    public void settings() {
        size(canvasWidth, canvasHeight);
    }

    public void setup() {
        frameRate(10);
        snakeBoids = new ArrayList<SnakeBoid>();
        PVector boundaries = new PVector(simulationWidth, simulationHeight);
        for (int i = 0; i < startingSnakes; i++) {
            //float r = 20;
            //float theta = i * (2 * PI) / startingSnakes;
            //PVector pos = new PVector(r * cos(theta) + 25, r * sin(theta) + 25);
            //snakeBoids.add(new SnakeBoid(boundaries, pos, new PVector(0, 0)));
            snakeBoids.add(new SnakeBoid(boundaries));
        }
    }

    public void draw() {
        background(0, 0, 0);
        fill(255, 255, 255);
        noStroke();
        for (SnakeBoid snake : snakeBoids) {
            snake.ChangeDirection(snakeBoids);
        }
        for (SnakeBoid snake : snakeBoids) {
            snake.CheckCollisions(snakeBoids);
            snake.Move();
            snake.Draw(this);
            //snake.Debug();
        }
    }

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new Poster());
    }
}
