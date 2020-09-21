// This sketch makes a meatballs clock using a marching squares algorithm.

package al.rosenth.peter.clock;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Clock extends PApplet {

    public PVector canvasSize = new PVector(500, 500);
    public PVector resolution = new PVector(100, 100);

    public float spiralScale    = 19;
    public float rotationSpeed  = -0.00004f;
    public float wiggleAmount   = 0.03f;
    public float wiggleSpeed    = 0.00025f;
    public float hourDotScale   = 18;
    public float minuteDotScale = 16;
    public float secondDotScale = 14.2f;
    public float threshold      = 0.9f;

    // color scheme
    public int yellow    = 0xffffc857;
    public int lightBlue = 0xff177e89;
    public int red       = 0xffdb3a34;
    public int darkBlue  = 0xff084c61;

    private float[][] hours         = new float[(int) resolution.x][(int) resolution.y];
    private int[][]   binaryHours   = new   int[(int) resolution.x][(int) resolution.y];
    private float[][] minutes       = new float[(int) resolution.x][(int) resolution.y];
    private int[][]   binaryMinutes = new   int[(int) resolution.x][(int) resolution.y];
    private float[][] seconds       = new float[(int) resolution.x][(int) resolution.y];
    private int[][]   binarySeconds = new   int[(int) resolution.x][(int) resolution.y];

    private ArrayList<PVector> hourDots   = new ArrayList<>();
    private ArrayList<PVector> minuteDots = new ArrayList<>();
    private ArrayList<PVector> secondDots = new ArrayList<>();

    private float animationTimeHours = 0;
    private float animationTimeMinutes = 0;
    private float animationTimeSeconds = 0;

    private float[] randomPhaseOffsets;
    private float[] randomFrequencies;

    private final PVector gridSize = new PVector(canvasSize.x / (resolution.x - 1), canvasSize.y / (resolution.y - 1));

    private static class Vertex {
        public PVector position;
        public float   value;
        public int     binary;
    }

    public void settings() {
        size((int) canvasSize.x, (int) canvasSize.y, P3D);
    }

    public void setup() {
        setPhaseOffsets();
        setRandomFrequencies();

        for (int i = 0; i < hour(); i++) {
            hourDots.add(dotInSpiral(i));
        }
        for (int i = 0; i < minute(); i++) {
            minuteDots.add(dotInSpiral(i + hour()));
        }
        for (int i = 0; i < second(); i++) {
            secondDots.add(dotInSpiral(i + minute() + hour()));
        }
    }

    public void draw() {
        updateFields();

        background(yellow);
        noStroke();
        for (int i = 0; i < resolution.x - 1; i++) {
            for (int j = 0; j < resolution.y - 1; j++) {
                fill(lightBlue);
                stroke(lightBlue);
                drawField(seconds, binarySeconds, i, j);

                fill(red);
                stroke(red);
                drawField(minutes, binaryMinutes, i, j);

                fill(darkBlue);
                stroke(darkBlue);
                drawField(hours, binaryHours, i, j);
            }
        }
    }

    private void updateFields() {
        if (hour() == 0 && hourDots.size() > 0) {
            resetHours();
            resetMinutes();
            resetSeconds();
        }
        if (hour() > hourDots.size()) {
            resetMinutes();
            resetSeconds();
            animationTimeHours = millis();
        }
        if (minute() > minuteDots.size()) {
            resetSeconds();
            animationTimeMinutes = millis();
        }
        if (second() > secondDots.size()) {
            animationTimeSeconds = millis();
        }

        hourDots   = new ArrayList<>();
        minuteDots = new ArrayList<>();
        secondDots = new ArrayList<>();
        for (int i = 0; i < hour(); i++) {
            hourDots.add(dotInSpiral(i));
        }
        for (int i = 0; i < minute(); i++) {
            minuteDots.add(dotInSpiral(i + hour()));
        }
        for (int i = 0; i < second(); i++) {
            secondDots.add(dotInSpiral(i + minute() + hour()));
        }

        PVector hourSpread   = new PVector(hourDotScale, hourDotScale);
        PVector minuteSpread = new PVector(minuteDotScale, minuteDotScale);
        PVector secondSpread = new PVector(secondDotScale, secondDotScale);
        for (int i = 0; i < resolution.x; i++) {
            for (int j = 0; j < resolution.y; j++) {
                PVector location = new PVector(gridSize.x * i, gridSize.y * j);
                hours[i][j]   = 0;
                minutes[i][j] = 0;
                seconds[i][j] = 0;
                if (hourDots.size() > 0) {
                    if (secondDots.size() < 59 || minuteDots.size() < 59 || hourDots.size() < 23) {
                        float bounce = bounceIn(map(millis() - animationTimeHours, 0, 500, 0, 1));
                        hours[i][j] += gaussian2d(hourDots.get(hourDots.size() - 1), PVector.mult(hourSpread, bounce), location);
                        for (int k = 0; k < hourDots.size() - 1; k++) {
                            hours[i][j] += gaussian2d(hourDots.get(k), hourSpread, location);
                        }
                    }
                    else {
                        float bounce = bounceOut(map(millis() - animationTimeSeconds, 0, 350, 0, 1));
                        for (PVector dot : hourDots) {
                            hours[i][j] += gaussian2d(dot, PVector.mult(hourSpread, bounce), location);
                        }
                    }
                }

                if (minuteDots.size() > 0) {
                    if (secondDots.size() < 59 || minuteDots.size() < 59) {
                        float bounce = bounceIn(map(millis() - animationTimeMinutes, 0, 500, 0, 1));
                        minutes[i][j] += gaussian2d(minuteDots.get(minuteDots.size() - 1), PVector.mult(minuteSpread, bounce), location);
                        for (int k = 0; k < minuteDots.size() - 1; k++) {
                            minutes[i][j] += gaussian2d(minuteDots.get(k), minuteSpread, location);
                        }
                    }
                    else {
                        float bounce = bounceOut(map(millis() - animationTimeSeconds, 0, 350, 0, 1));
                        for (PVector dot : minuteDots) {
                            minutes[i][j] += gaussian2d(dot, PVector.mult(minuteSpread, bounce), location);
                        }
                    }
                }

                if (secondDots.size() > 0) {
                    if (secondDots.size() < 59) {
                        float bounce = bounceIn(map(millis() - animationTimeSeconds, 0, 500, 0, 1));
                        seconds[i][j] += gaussian2d(secondDots.get(secondDots.size() - 1), PVector.mult(secondSpread, bounce), location);
                        for (int k = 0; k < secondDots.size() - 1; k++) {
                            seconds[i][j] += gaussian2d(secondDots.get(k), secondSpread, location);
                        }
                    }
                    else {
                        float bounce = bounceOut(map(millis() - animationTimeSeconds, 0, 350, 0, 1));
                        for (PVector dot : secondDots) {
                            seconds[i][j] += gaussian2d(dot, PVector.mult(secondSpread, bounce), location);
                        }
                    }
                }

                binaryHours[i][j] = (hours[i][j] > threshold) ? 1 : 0;
                binaryMinutes[i][j] = (minutes[i][j] > threshold) ? 1 : 0;
                binarySeconds[i][j] = (seconds[i][j] > threshold) ? 1 : 0;
            }
        }
    }

    private float gaussian2d(PVector center, PVector spread, PVector location) {
        return exp(-(sq(location.x - center.x) / (2 * sq(spread.x)) + sq(location.y - center.y) / (2 * sq(spread.y))));
    }

    private PVector dotInSpiral(int index) {
        float r = spiralScale * sqrt(index);
        float theta = index * PI * (3 - sqrt(5)) + rotationSpeed * millis() + wiggleAmount * sin(randomFrequencies[index] * millis() + randomPhaseOffsets[index]);
        return new PVector(canvasSize.x / 2 + r * cos(theta), canvasSize.y / 2 + r * sin(theta));
    }

    private void resetHours() {
        hourDots = new ArrayList<>();
    }

    private void resetMinutes() {
        minuteDots = new ArrayList<>();
    }

    private void resetSeconds() {
        secondDots = new ArrayList<>();
    }

    private float bounceIn(float t) {
        t = constrain(t, 0, 1);
        return sin(2 * PI * pow(t, 4)) * (1 - pow(t, 2)) + t;
    }

    private float bounceOut(float t) {
        t = constrain(t, 0, 1);
        return -2.5f * (t - 1) * (t + 0.4f);
    }

    private void drawField(float[][] field, int[][] binaryField, int i, int j) {
        float x = gridSize.x * i;
        float y = gridSize.y * j;

        // corners
        Vertex topLeft         = new Vertex();
        Vertex topRight        = new Vertex();
        Vertex bottomRight     = new Vertex();
        Vertex bottomLeft      = new Vertex();

        topLeft.position       = new PVector(x, y);
        topRight.position      = new PVector(x + gridSize.x, y);
        bottomRight.position   = new PVector(x + gridSize.x, y + gridSize.y);
        bottomLeft.position    = new PVector(x, y + gridSize.y);

        topLeft.value          = field[i][j];
        topRight.value         = field[i+1][j];
        bottomRight.value      = field[i+1][j+1];
        bottomLeft.value       = field[i][j+1];

        topLeft.binary         = binaryField[i][j];
        topRight.binary        = binaryField[i+1][j];
        bottomRight.binary     = binaryField[i+1][j+1];
        bottomLeft.binary      = binaryField[i][j+1];

        // midpoints
        PVector topPosition    = findMidPoint(topLeft, topRight);
        PVector rightPosition  = findMidPoint(topRight, bottomRight);
        PVector bottomPosition = findMidPoint(bottomRight, bottomLeft);
        PVector leftPosition   = findMidPoint(bottomLeft, topLeft);

        int cellWeight         = topLeft.binary * 8 + topRight.binary * 4 + bottomRight.binary * 2 + bottomLeft.binary;
        float centerValue      = (topLeft.value + topRight.value + bottomRight.value + bottomLeft.value) / 4;
        switch (cellWeight) {
            default:
                break;
            case 1:
                triangle(leftPosition, bottomPosition, bottomLeft.position);
                break;

            case 2:
                triangle(rightPosition, bottomRight.position, bottomPosition);
                break;

            case 3:
                triangle(leftPosition, rightPosition, bottomLeft.position);
                triangle(rightPosition, bottomRight.position, bottomLeft.position);
                break;

            case 4:
                triangle(topPosition, topRight.position, rightPosition);
                break;

            case 5:
                triangle(topPosition, topRight.position, rightPosition);
                if (centerValue > threshold) {
                    triangle(topPosition, rightPosition, bottomPosition);
                    triangle(topPosition, bottomPosition, leftPosition);
                }
                triangle(leftPosition, bottomPosition, bottomLeft.position);
                break;

            case 6:
                triangle(topPosition, topRight.position, bottomPosition);
                triangle(topRight.position, bottomRight.position, bottomPosition);
                break;

            case 7:
                triangle(topPosition, topRight.position, bottomRight.position);
                triangle(topPosition, bottomRight.position, leftPosition);
                triangle(leftPosition, bottomRight.position, bottomLeft.position);
                break;

            case 8:
                triangle(topLeft.position, topPosition, leftPosition);
                break;

            case 9:
                triangle(topLeft.position, topPosition, bottomPosition);
                triangle(topLeft.position, bottomPosition, bottomLeft.position);
                break;

            case 10:
                triangle(topLeft.position, topPosition, leftPosition);
                if (centerValue > threshold) {
                    triangle(topPosition, bottomPosition, leftPosition);
                    triangle(topPosition, rightPosition, bottomPosition);
                }
                triangle(rightPosition, bottomRight.position, bottomPosition);
                break;

            case 11:
                triangle(topLeft.position, topPosition, bottomLeft.position);
                triangle(topPosition, rightPosition, bottomLeft.position);
                triangle(rightPosition, bottomRight.position, bottomLeft.position);
                break;

            case 12:
                triangle(topLeft.position, topRight.position, leftPosition);
                triangle(topRight.position, rightPosition, leftPosition);
                break;

            case 13:
                triangle(topLeft.position, topRight.position, rightPosition);
                triangle(topLeft.position, rightPosition, bottomPosition);
                triangle(topLeft.position, bottomPosition, bottomLeft.position);
                break;

            case 14:
                triangle(topLeft.position, topRight.position, leftPosition);
                triangle(topRight.position, bottomPosition, leftPosition);
                triangle(topRight.position, bottomRight.position, bottomPosition);
                break;

            case 15:
                triangle(topLeft.position, topRight.position, bottomLeft.position);
                triangle(topRight.position, bottomRight.position, bottomLeft.position);
                break;
        }
    }

    private PVector findMidPoint(Vertex a, Vertex b) {
        return PVector.lerp(a.position, b.position, constrain((threshold - a.value) / (b.value - a.value), 0, 1));
    }

    private void triangle(PVector a, PVector b, PVector c) {
        triangle(a.x, a.y, b.x, b.y, c.x, c.y);
    }

    private void setPhaseOffsets() {
        randomPhaseOffsets = new float[144];
        for (int i = 0; i < 144; i++) {
            randomPhaseOffsets[i] = random(0, 2 * PI);
        }
    }

    private void setRandomFrequencies() {
        randomFrequencies = new float[144];
        for (int i = 0; i < 144; i++) {
            randomFrequencies[i] = wiggleSpeed * random(0, 2 * PI);
        }
    }

    public static void main(String[] args) {
        PApplet.runSketch(new String[] {""}, new Clock());
    }
}
