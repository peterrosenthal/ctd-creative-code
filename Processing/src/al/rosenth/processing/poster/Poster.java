package al.rosenth.processing.poster;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import java.util.ArrayList;

public class Poster extends PApplet {

    public int canvasWidth  = 715;
    public int canvasHeight = 1045;

    public int simulationWidth  = 16;
    public int simulationHeight = 20;
    public int[][] simulationDrawBounds = {
            {157, 225},
            {558, 825}
    };
    public int simulationDrawWidth  = 400;
    public int simulationDrawHeight = 50;

    public int startingSnakes = 60;

    public int framesPerStep = 30;

    public ArrayList<SnakeBoid> snakeBoids;
    public ArrayList<ArrayList<SnakeBoid>> snakeDiffs;

    private PFont JetBrainsMono;

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

        JetBrainsMono = createFont("res/JetBrains Mono Regular Nerd Font Complete Mono.ttf", 32, true);
    }

    public void draw() {
        background(240, 240, 240);
        DrawText();
        float t = (frameCount % framesPerStep) / (float) framesPerStep;
        /*if (frameCount % framesPerStep == 0) {
            for (SnakeBoid snake : snakeBoids) {
                snake.BoidBehaviors(snakeBoids);
                snake.Move(snakeBoids, snakeDiffs);
            }
        }*/
        for (SnakeBoid snake : snakeBoids) {
            if (frameCount % framesPerStep == 0) {
                snake.BoidBehaviors(snakeBoids);
                snake.Move(snakeBoids, snakeDiffs);
            }
            snake.Draw(constrain(t * 1.5f, 0, 1), simulationDrawBounds[0][0], simulationDrawBounds[0][1], simulationDrawBounds[1][0], simulationDrawBounds[1][1], this);
        }
        UpdateSnakesArrayList();

        if (snakeBoids.size() < 2) {
            snakeBoids.clear();
            PVector boundaries = new PVector(simulationWidth, simulationHeight);
            for (int i = 0; i < startingSnakes; i++) {
                snakeBoids.add(new SnakeBoid(boundaries));
            }
        }
    }

    public void mouseClicked() {
        if (mouseButton == LEFT) {
            snakeBoids.clear();
            snakeDiffs.get(0).clear();
            snakeDiffs.get(1).clear();
            PVector boundaries = new PVector(simulationWidth, simulationHeight);
            for (int i = 0; i < startingSnakes; i++) {
                snakeBoids.add(new SnakeBoid(boundaries));
            }
        }
        if (mouseButton == RIGHT) {
            saveFrame("pictures/screen_####.png");
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

    public void DrawText() {
        fill(0, 0, 0);
        textFont(JetBrainsMono, 96);
        textAlign(CENTER);
        text("ATLAS", canvasWidth / 2, 90);

        textFont(JetBrainsMono, 64);
        text("Creative Code", canvasWidth / 2, 150);

        textFont(JetBrainsMono, 48);
        text("Mondays + Thursdays", canvasWidth/2, 930);
        text("4:00 - 6:30", canvasWidth / 2, 1000);
    }

    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new Poster());
    }
}
