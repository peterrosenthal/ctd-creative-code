import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Poster extends PApplet {

    public int canvasWidth  = 500;
    public int canvasHeight = 500;

    public int simulationWidth  = 20;
    public int simulationHeight = 20;
    public int simulationDrawWidth  = 400;
    public int simulationDrawHeight = 400;

    public int startingSnakes = 20;

    public int framesPerStep = 20;

    public ArrayList<SnakeBoid> snakeBoids;

    public void settings() {
        size(canvasWidth, canvasHeight);
    }

    public void setup() {
        snakeBoids = new ArrayList<SnakeBoid>();
        PVector boundaries = new PVector(simulationWidth, simulationHeight);
        for (int i = 0; i < startingSnakes; i++) {
            snakeBoids.add(new SnakeBoid(boundaries));
        }
    }

    public void draw() {
        background(0, 0, 0);
        fill(255, 255, 255);
        noStroke();

        float t = (frameCount % framesPerStep) / (float) framesPerStep;
        if (frameCount % framesPerStep == 0) {
            for (SnakeBoid snake : snakeBoids) {
                snake.BoidBehaviors(snakeBoids);
                snakeBoids = snake.Move(snakeBoids);
            }
        }
        for (SnakeBoid snake : snakeBoids) {
            snake.Draw(constrain(t * 1.5f, 0, 1), simulationDrawWidth, simulationDrawHeight, this);
        }
    }

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new Poster());
    }
}
