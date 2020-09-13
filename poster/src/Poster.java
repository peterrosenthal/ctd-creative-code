import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Poster extends PApplet {

    public int canvasWidth  = 600;
    public int canvasHeight = 600;

    public int simulationWidth  = 20;
    public int simulationHeight = 20;
    public int simulationDrawWidth  = 500;
    public int simulationDrawHeight = 500;

    public int startingSnakes = 40;

    public int framesPerStep = 20;

    public ArrayList<SnakeBoid> snakeBoids;
    public ArrayList<ArrayList<SnakeBoid>> snakeDiffs;

    public void settings() {
        size(canvasWidth, canvasHeight);
    }

    public void setup() {
        snakeBoids = new ArrayList<>();
        snakeDiffs = new ArrayList<>();
        snakeDiffs.add(new ArrayList<>());
        snakeDiffs.add(new ArrayList<>());
        PVector boundaries = new PVector(simulationWidth, simulationHeight);
        for (int i = 0; i < startingSnakes; i++) {
            snakeBoids.add(new SnakeBoid(boundaries));
        }
    }

    public void draw() {
        background(0, 0, 0);

        float t = (frameCount % framesPerStep) / (float) framesPerStep;
        if (frameCount % framesPerStep == 0) {
            for (SnakeBoid snake : snakeBoids) {
                snake.BoidBehaviors(snakeBoids);
                snake.Move(snakeBoids, snakeDiffs);
            }
        }
        UpdateSnakesArrayList();
        for (SnakeBoid snake : snakeBoids) {
            snake.Draw(constrain(t * 1.5f, 0, 1), simulationDrawWidth, simulationDrawHeight, this);
        }
    }

    public void UpdateSnakesArrayList() {
        for (SnakeBoid snake : snakeDiffs.get(0)) {
            snakeBoids.remove(snake);
        }
        for (SnakeBoid snake : snakeDiffs.get(1)) {
            snakeBoids.add(snake);
        }
        snakeDiffs.get(0).clear();
        snakeDiffs.get(1).clear();
    }

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new Poster());
    }
}
