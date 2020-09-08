import java.io.Console;
import java.util.*;

import processing.core.PApplet;
import processing.core.PVector;

public class SnakeBoid {
    public PVector bounds;

    public PVector pos;
    public PVector dir;

    public ArrayList<PVector> corners;

    public SnakeBoid(PVector boundaries) {
        bounds = boundaries;
        pos = new PVector();
        pos.x = (int) (Math.random() * bounds.x);
        pos.y = (int) (Math.random() * bounds.y);
        dir = new PVector();
        double dx = Math.random() * 2 - 1;
        double dy = Math.random() * 2 - 1;
        if (Math.abs(dx) > Math.abs(dy)) {
            dir.x = (int) Math.signum(dx);
        }
        else {
            dir.y = (int) Math.signum(dy);
        }
        corners = new ArrayList<PVector>();
        corners.add(pos);
    }

    public SnakeBoid(PVector boundaries, PVector startPos) {
        bounds = boundaries;
        pos = startPos;
        dir = new PVector();
        double dx = Math.random() * 2 - 1;
        double dy = Math.random() * 2 - 1;
        if (Math.abs(dx) > Math.abs(dy)) {
            dir.x = (int) Math.signum(dx);
        }
        else {
            dir.y = (int) Math.signum(dy);
        }
        corners = new ArrayList<PVector>();
        corners.add(pos);
    }

    public SnakeBoid(PVector boundaries, PVector startPos, PVector startDir) {
        bounds = boundaries;
        pos = startPos;
        dir = startDir;
        corners = new ArrayList<PVector>();
        corners.add(pos);
    }

    public void ChangeDirection(ArrayList<SnakeBoid> snakes) {
        for (SnakeBoid snake : snakes) {
            float dist = (float) Math.sqrt((snake.pos.x - pos.x) * (snake.pos.x - pos.x) + (snake.pos.y - pos.y) + (snake.pos.y - pos.y));
            if (dist > 0) {
                float angle = AngleBetween2D(pos, snake.pos);
                dir.add(AlignmentBehavior(dist, snake.dir));
                dir.add(RepulsionBehavior(dist, angle));
            }
        }
        dir.add(AttractionBehavior(snakes));
        dir.normalize();
        if (Math.abs(dir.x) > Math.abs(dir.y)) {
            dir.x = (int) Math.signum(dir.x);
            dir.y = 0;
        }
        else {
            dir.x = 0;
            dir.y = (int) Math.signum(dir.y);
        }
    }

    public void CheckCollisions(ArrayList<SnakeBoid> snakes) {
        for (SnakeBoid snake : snakes) {
            if (PVector.add(pos, dir) == snake.pos) {
                dir.x = 0;
                dir.y = 0;
            }
        }

        if (pos.x < 0) {
            pos.x = 0;
            dir = PVector.mult(dir,-1);
            dir.x += 0.1f;
        }
        if (pos.y < 0) {
            pos.y = 0;
            dir = PVector.mult(dir,-1);
            dir.y += 0.1f;
        }
        if (pos.x > bounds.x) {
            pos.x = bounds.x;
            dir = PVector.mult(dir, -1);
            dir.x -= 0.1f;
        }
        if (pos.y > bounds.y) {
            pos.y = bounds.y;
            dir = PVector.mult(dir, -1);
            dir.y -= 0.1f;
        }
    }

    public void Move() {
        pos.add(dir);
    }

    public void Draw(PApplet canvas) {
        int width  = canvas.width;
        int height = canvas.height;

        canvas.circle(width * pos.x / bounds.x, height * pos.y / bounds.y, 10);
    }

    public void Debug() {
        System.out.println(pos.x);
        System.out.println(pos.y);
    }

    private PVector AlignmentBehavior(float r, PVector direction) {
        float height = 0.01f;
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
        return new PVector(r * (float) Math.cos(theta), r * (float) Math.sin(theta));
    }

    private float fGaussian(float a, float b, float c, float x) {
        return a * (float) Math.exp(-((x - b) * (x - b)) / (2 * c * c));
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
}
