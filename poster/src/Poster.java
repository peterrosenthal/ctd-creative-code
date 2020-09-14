import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Poster extends PApplet {

    public int canvasWidth  = 715;
    public int canvasHeight = 1045;

    public int simulationWidth  = 16;
    public int simulationHeight = 8;
    public int[][] simulationDrawBounds = {
            {100, 100},
            {500, 300}
    };
    public int simulationDrawWidth  = 400;
    public int simulationDrawHeight = 50;

    public int startingSnakes = 50;

    public int framesPerStep = 15;

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
            snake.Draw(constrain(t * 1.5f, 0, 1), simulationDrawBounds[0][0], simulationDrawBounds[0][1], simulationDrawBounds[1][0], simulationDrawBounds[1][1], this);
        }
    }

    public void mouseClicked() {
        snakeBoids.clear();
        snakeDiffs.get(0).clear();
        snakeDiffs.get(1).clear();
        PVector boundaries = new PVector(simulationWidth, simulationHeight);
        for (int i = 0; i < startingSnakes; i++) {
            snakeBoids.add(new SnakeBoid(boundaries));
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
