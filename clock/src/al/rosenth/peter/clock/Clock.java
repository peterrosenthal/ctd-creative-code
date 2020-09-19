// This sketch makes a metaballs (not meatballs... unfortunately) based clock using a naive marching squares algorithm.

package al.rosenth.peter.clock;

import processing.core.PApplet;
import processing.core.PVector;

public class Clock extends PApplet {

    public PVector canvasSize  = new PVector(800, 800);
    public PVector resolution  = new PVector(50, 50);
    public PVector gridSize    = new PVector(
            canvasSize.x / (resolution.x - 1),
            canvasSize.y / (resolution.y - 1)
    );

    public float     threshold   = 0.5f;
    public float[][] field       = new float[(int) resolution.x][(int) resolution.y];
    public int[][]   binaryField = new   int[(int) resolution.x][(int) resolution.y];

    public void settings() {
        size((int) canvasSize.x, (int) canvasSize.y);
    }

    public void draw() {
        background(0, 0, 0);
        updateField();

        for (int i = 0; i < resolution.x - 1; i++) {
            for (int j = 0; j < resolution.y - 1; j++) {
                float x = gridSize.x * i;
                float y = gridSize.y * j;
                // corners
                PVector topLeft     = new PVector(                x,                 y);
                PVector topRight    = new PVector(x + gridSize.x,                 y);
                PVector bottomRight = new PVector(x + gridSize.x, y + gridSize.y);
                PVector bottomLeft  = new PVector(                x, y + gridSize.y);

                // midpoints with lerping
                PVector top;
                if (binaryField[i][j] == binaryField[i+1][j]) {
                    top = new PVector(x + gridSize.x / 2, y);
                }
                else {
                    top = PVector.lerp(topLeft, topRight, (threshold - field[i][j]) / (field[i+1][j] - field[i][j]));
                }

                PVector right;
                if (binaryField[i+1][j] == binaryField[i+1][j+1]) {
                    right  = new PVector(x + gridSize.x, y + gridSize.y / 1.5f);
                }
                else {
                    right = PVector.lerp(topRight, bottomRight, (threshold - field[i+1][j]) / (field[i+1][j+1] - field[i+1][j]));
                }

                PVector bottom;
                if (binaryField[i+1][j+1] == field[i][j+1]) {
                    bottom = new PVector(x + gridSize.x / 2, y + gridSize.y);
                }
                else {
                    bottom = PVector.lerp(bottomRight, bottomLeft, (threshold - field[i+1][j+1]) / (field[i][j+1] - field[i+1][j+1]));
                }

                PVector left;
                if (binaryField[i][j+1] == field[i][j]) {
                    left   = new PVector(x, y + gridSize.y / 2);
                }
                else {
                    left = PVector.lerp(bottomLeft, topLeft, (threshold - field[i][j+1]) / (field[i][j] - field[i][j+1]));
                }

                fill(255, 255, 255);
                strokeWeight(0);
                int cellWeight = binaryField[i][j] * 8 + binaryField[i+1][j] * 4 + binaryField[i+1][j+1] * 2 + binaryField[i][j+1];
                float center = (field[i][j] + field[i+1][j] + field[i+1][j+1] + field[i][j+1]) / 4;
                switch (cellWeight) {
                    default: break;
                    case 1:
                        triangle(left, bottom, bottomLeft);
                        break;

                    case 2:
                        triangle(right, bottomRight, bottom);
                        break;

                    case 3:
                        triangle(left, right, bottomLeft);
                        triangle(right, bottomRight, bottomLeft);
                        break;

                    case 4:
                        triangle(top, topRight, right);
                        break;

                    case 5:
                        triangle(top, topRight, right);
                        if (center > threshold) {
                            triangle(top, right, bottom);
                            triangle(top, bottom, left);
                        }
                        triangle(left, bottom, bottomLeft);
                        break;

                    case 6:
                        triangle(top, topRight, bottom);
                        triangle(topRight, bottomRight, bottom);
                        break;

                    case 7:
                        triangle(top, topRight, bottomRight);
                        triangle(top, bottomRight, left);
                        triangle(left, bottomRight, bottomLeft);
                        break;

                    case 8:
                        triangle(topLeft, top, left);
                        break;

                    case 9:
                        triangle(topLeft, top, bottom);
                        triangle(topLeft, bottom, bottomLeft);
                        break;

                    case 10:
                        triangle(topLeft, top, left);
                        if (center > threshold) {
                            triangle(top, bottom, left);
                            triangle(top, right, bottom);
                        }
                        triangle(right, bottomRight, bottom);
                        break;

                    case 11:
                        triangle(topLeft, top, bottomLeft);
                        triangle(top, right, bottomLeft);
                        triangle(right, bottomRight, bottomLeft);
                        break;

                    case 12:
                        triangle(topLeft, topRight, left);
                        triangle(topRight, right, left);
                        break;

                    case 13:
                        triangle(topLeft, topRight, right);
                        triangle(topLeft, right, bottom);
                        triangle(topLeft, bottom, bottomLeft);
                        break;

                    case 14:
                        triangle(topLeft, topRight, left);
                        triangle(topRight, bottom, left);
                        triangle(topRight, bottomRight, bottom);
                        break;

                    case 15:
                        triangle(topLeft, topRight, bottomLeft);
                        triangle(topRight, bottomRight, bottomLeft);
                        break;
                }
            }
        }
    }

    private void updateField() {
        PVector fixedCenter = new PVector(canvasSize.x / 2, canvasSize.y / 2);
        PVector fixedSpread = new PVector(100, 100);
        for (int i = 0; i < resolution.x; i++) {
            for (int j = 0; j < resolution.y; j++) {
                PVector location = new PVector(gridSize.x * i, gridSize.y * j);
                PVector mousePos = new PVector(mouseX, mouseY);
                field[i][j] = gaussian2d(fixedCenter, fixedSpread, location) + gaussian2d(mousePos, fixedSpread, location);

                binaryField[i][j] = (field[i][j] > threshold) ? 1 : 0;

                //stroke(255, 255, 255);
                //strokeWeight(2);
                //point(location.x, location.y);
            }
        }
    }

    private float gaussian2d (PVector center, PVector spread, PVector location) {
        return exp(-((location.x - center.x) * (location.x - center.x) / (2 * spread.x * spread.x) + (location.y - center.y) * (location.y - center.y) / (2 * spread.y * spread.y)));
    }

    private void triangle(PVector a, PVector b, PVector c) {
        triangle(a.x, a.y, b.x, b.y, c.x, c.y);
    }

    public static void main(String[] args) {
        PApplet.runSketch(new String[] {""}, new Clock());
    }
}
