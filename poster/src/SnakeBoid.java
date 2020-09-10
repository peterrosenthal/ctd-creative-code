import java.io.Console;
import java.util.*;

import processing.core.PApplet;
import processing.core.PVector;

public class SnakeBoid {
    public PVector bounds;

    public PVector pos;
    public PVector dir;

    public ArrayList<PVector> segments;

    private PVector renderPos;
    private PVector target;

    private int addSegments;

    public SnakeBoid(PVector boundaries) {
        bounds = boundaries;
        pos = new PVector();
        pos.x = (int) (Math.random() * (bounds.x - 2)) + 1;
        pos.y = (int) (Math.random() * (bounds.y - 2)) + 1;
        dir = Get4WayDirection(PVector.random2D());
        target = PVector.add(pos, dir);
        segments = new ArrayList<PVector>();
        segments.add(pos);
        addSegments = 0;
    }

    public SnakeBoid(PVector boundaries, PVector startPos) {
        bounds = boundaries;
        pos = startPos;
        dir = Get4WayDirection(PVector.random2D());
        target = PVector.add(pos, dir);
        segments = new ArrayList<PVector>();
        segments.add(pos);
        addSegments = 0;
    }

    public SnakeBoid(PVector boundaries, PVector startPos, PVector startDir) {
        bounds = boundaries;
        pos = startPos;
        dir = startDir;
        target = PVector.add(pos, dir);
        segments = new ArrayList<PVector>();
        segments.add(pos);
        addSegments = 0;
    }

    public void BoidBehaviors(ArrayList<SnakeBoid> snakes) {
        for (SnakeBoid snake : snakes) {
            float dist = PApplet.sqrt((snake.pos.x - pos.x) * (snake.pos.x - pos.x) + (snake.pos.y - pos.y) + (snake.pos.y - pos.y));
            if (dist > 0) {
                float angle = AngleBetween2D(pos, snake.pos);
                dir.add(AlignmentBehavior(dist, snake.dir));
                dir.add(RepulsionBehavior(dist, angle));
            }
        }
        dir.add(AttractionBehavior(snakes));
        dir.normalize();
        dir = Get4WayDirection(dir);
    }

    public ArrayList<SnakeBoid> Move(ArrayList<SnakeBoid> snakes) {
        pos = target;

        target = PVector.add(pos, dir);
        while (!CheckBounds()) {
            target = PVector.add(pos, dir);
        }

        while (!CheckSegments()) {
            target = PVector.add(pos, dir);
        }

        segments.add(pos);
        if (addSegments == 0) {
            segments.remove(0);
        }
        else {
            addSegments--;
        }

        return CheckSnakes(snakes);
    }

    public void Draw(float amt, int width, int height, PApplet canvas) {
        renderPos = PVector.lerp(pos, target, amt);

        int canvasWidth  = canvas.width;
        int canvasHeight = canvas.height;

        canvas.circle(0.5f * (0.5f * (canvasWidth - width) + (width + canvasWidth) * renderPos.x / bounds.x), 0.5f * (0.5f * (canvasHeight - height) + (height + canvasHeight) * renderPos.y / bounds.y), 10);

        for (int i = 1; i < segments.size(); i++) {
            canvas.circle(0.5f * (0.5f * (canvasWidth - width)  + (width + canvasWidth) * PVector.lerp(segments.get(i - 1), segments.get(i), amt).x / bounds.x), 0.5f * (0.5f * (canvasHeight - height) + (height + canvasHeight) * PVector.lerp(segments.get(i - 1), segments.get(i), amt).y / bounds.y), 10);
        }
    }

    public void Debug() {
        System.out.println(pos.x);
        System.out.println(pos.y);
    }

    private PVector Get4WayDirection(PVector vector) {
        if (PApplet.abs(vector.x) > PApplet.abs(vector.y)) {
            if (vector.x > 0) {
                return new PVector(1, 0);
            }
            else {
                return new PVector(-1, 0);
            }
        }
        else {
            if (vector.y > 0) {
                return new PVector(0, 1);
            }
            else {
                return new PVector(0, -1);
            }
        }
    }

    private PVector AlignmentBehavior(float r, PVector direction) {
        float height = 0.05f;
        float center = 4;
        float width = 1;
        r = fGaussian(height, center, width, r);
        return PVector.mult(direction, r);
    }

    private PVector AttractionBehavior(ArrayList<SnakeBoid> snakes) {
        PVector centerOfMass = new PVector();
        for (SnakeBoid snake : snakes) {
            centerOfMass.x += snake.pos.x;
            centerOfMass.y += snake.pos.y;
        }
        centerOfMass.div(snakes.size());
        float theta = AngleBetween2D(pos, centerOfMass);
        float r = 1f;
        return new PVector(r * PApplet.cos(theta), r * PApplet.sin(theta));
    }

    private PVector RepulsionBehavior(float r, float theta) {
        float height = 1;
        float center = 0.5f;
        float width = 3;
        r = -fGaussian(height, center, width, r);
        return new PVector(r * PApplet.cos(theta), r * PApplet.sin(theta));
    }

    private float fGaussian(float a, float b, float c, float x) {
        return a * PApplet.exp(-((x - b) * (x - b)) / (2 * c * c));
    }

    private float AngleBetween2D(PVector v1, PVector v2) {
        if (v1.x < v2.x) {
            return PApplet.atan((v2.y - v1.y) / (v2.x - v1.x));
        }
        else if (v1.x > v2.x) {
            return PApplet.PI + PApplet.atan((v2.y - v1.y) / (v2.x - v1.x));
        }
        else {
            if (v1.y < v2.y) {
                return 3 * PApplet.PI / 2;
            }
            else {
                return PApplet.PI / 2;
            }
        }
    }

    private boolean CheckBounds() {
        if (target.x < 0) {
            dir.x = 0;
            pos.x = 0;
            if (target.y < bounds.y / 2) {
                dir.y = 1;
            }
            else {
                dir.y = -1;
            }
            return false;
        }
        if (target.y < 0) {
            dir.y = 0;
            pos.y = 0;
            if (target.x < bounds.x / 2) {
                dir.x = 1;
            }
            else {
                dir.x = -1;
            }
            return false;
        }
        if (target.x > bounds.x) {
            dir.x = 0;
            pos.x = bounds.x;
            if (target.y < bounds.y / 2) {
                dir.y = 1;
            }
            else {
                dir.y = -1;
            }
            return false;
        }
        if (target.y > bounds.y) {
            dir.y = 0;
            pos.y = bounds.y;
            if (target.x < bounds.x / 2) {
                dir.x = 1;
            }
            else {
                dir.x = -1;
            }
            return false;
        }
        return true;
    }

    private boolean CheckSegments() {
        return true;
    }

    private ArrayList<SnakeBoid> CheckSnakes(ArrayList<SnakeBoid> snakes) {
        for (SnakeBoid snake : snakes) {
            if (snake != this && PVectorSoftEquals(snake.target, target)) {
                addSegments = snake.segments.size();
                ArrayList<SnakeBoid> newSnakes = (ArrayList<SnakeBoid>) snakes.clone();
                newSnakes.remove(snake);
                return newSnakes;
            }
        }
        return snakes;
    }

    private boolean PVectorSoftEquals(PVector a, PVector b) {
        return (int) a.x == (int) b.x && (int) a.y == (int) b.y;
    }
}
