import java.io.Console;
import java.util.*;

import processing.core.PApplet;
import processing.core.PVector;

public class SnakeBoid {
    public PVector bounds;

    public PVector pos;
    public PVector dir;
    public PVector target;

    public ArrayList<PVector> segments;

    public boolean cutCorners = false;

    private int addSegments;

    private boolean disruptor;

    private int[] colors = {
            0xff5c2361,
            0xff49236a,
            0xff2f2273,
            0xff21367c,
            0xff205e86,
            0xff1f8e90,
            0xff1d9a6c,
            0xff299d40,
            0xff4c9f34,
            0xff80a23f,
            0xffa59e4a,
            0xffa87d55,
            0xffac645f,
            0xffaf6a83,
            0xffb374a9,
            0xffaa7eb7,
            0xff9a88ba,
            0xff9296bf
    };

    public SnakeBoid(PVector boundaries) {
        bounds = boundaries;
        pos = new PVector();
        pos.x = (int) (Math.random() * (bounds.x - 2)) + 1;
        pos.y = (int) (Math.random() * (bounds.y - 2)) + 1;
        dir = Get4WayDirection(PVector.random2D());
        target = PVector.add(pos, dir);
        segments = new ArrayList<>();
        segments.add(pos);
        addSegments = 0;
        disruptor = false;
    }

    public SnakeBoid(PVector boundaries, PVector startPos) {
        bounds = boundaries;
        pos = startPos;
        dir = Get4WayDirection(PVector.random2D());
        target = PVector.add(pos, dir);
        segments = new ArrayList<>();
        segments.add(pos);
        addSegments = 0;
        disruptor = false;
    }

    public SnakeBoid(PVector boundaries, PVector startPos, PVector startDir) {
        bounds = boundaries;
        pos = startPos;
        dir = startDir;
        target = PVector.add(pos, dir);
        segments = new ArrayList<>();
        segments.add(pos);
        addSegments = 0;
        disruptor = false;
    }

    public SnakeBoid(PVector boundaries, ArrayList<PVector> startSegments, PVector startDir) {
        bounds = boundaries;
        pos = startSegments.get(startSegments.size() - 1);
        dir = startDir;
        target = PVector.add(pos, dir);
        segments = startSegments;
        addSegments = 0;
        disruptor = true;
    }

    public void BoidBehaviors(ArrayList<SnakeBoid> snakes) {
        for (SnakeBoid snake : snakes) {
            float dist = PApplet.sqrt((snake.pos.x - pos.x) * (snake.pos.x - pos.x) + (snake.pos.y - pos.y) + (snake.pos.y - pos.y));
            for (int i = 1; i < snake.segments.size(); i++) {
                PVector segment = snake.segments.get(i);
                float segmentDist = PApplet.sqrt((segment.x - pos.x) * (segment.x - pos.x) + (segment.y - pos.y) + (segment.y - pos.y));
                if (segmentDist < dist) {
                    dist = segmentDist;
                }
            }
            if (dist > 0) {
                float angle = AngleBetween2D(pos, snake.pos);
                dir.add(AlignmentBehavior(dist, snake.dir));
                dir.add(RepulsionBehavior(dist, angle));
            }
        }
        dir.add(AttractionBehavior(snakes));
        if (disruptor) {
            dir.add(DisruptionBehavior(snakes));
        }
        dir.normalize();
        dir = Get4WayDirection(dir);
    }

    public void Move(ArrayList<SnakeBoid> snakes, ArrayList<ArrayList<SnakeBoid>> snakeDiffs) {
        pos = target;

        target = PVector.add(pos, dir);
        for (int i = 0; i < 4; i++) {
            if (CheckBounds()) {
                break;
            }
            target = PVector.add(pos, dir);
        }

        CheckSelfSegments();

        segments.add(pos);
        if (addSegments == 0) {
            segments.remove(0);
        }
        else {
            addSegments--;
        }

        CheckSnakes(snakes, snakeDiffs);
    }

    public void Draw(float amt, int minX, int minY, int maxX, int maxY, PApplet canvas) {
        int color = colors[PApplet.min(segments.size(), colors.length) - 1];
        canvas.fill(color);
        canvas.stroke(color);
        //canvas.fill(0);
        //canvas.stroke(0);

        PVector renderPos = PVector.lerp(pos, target, amt);

        int width = maxX - minX;
        int height = maxY - minY;

        if (segments.size() == 1) {
            canvas.strokeWeight(0);
            canvas.circle(minX + width / bounds.x * renderPos.x,
                    minY + height / bounds.y * renderPos.y, 10);
        }
        else {
            canvas.strokeWeight(10);
            if (cutCorners) {
                PVector firstPos = renderPos;
                for (int i = segments.size() - 1; i > 0; i--) {
                    PVector secondPos = PVector.lerp(segments.get(i - 1), segments.get(i), amt);
                    canvas.line(minX + width / bounds.x * firstPos.x, minY + height / bounds.y * firstPos.y,
                            minX + width / bounds.x * secondPos.x, minY + height / bounds.y * secondPos.y);
                    firstPos = secondPos;
                }
            }
            else {
                PVector firstPos = renderPos;
                for (int i = segments.size() - 1; i > 0; i--) {
                    PVector secondPos = segments.get(i);
                    PVector thirdPos;
                    if (addSegments > 0 && i == 1) {
                        thirdPos = segments.get(1);
                    }
                    else {
                        thirdPos = PVector.lerp(segments.get(i - 1), segments.get(i), amt);
                    }
                    canvas.line(minX + width / bounds.x * firstPos.x, minY + height / bounds.y * firstPos.y,
                            minX + width / bounds.x * secondPos.x, minY + height / bounds.y * secondPos.y);
                    canvas.line(minX + width / bounds.x * secondPos.x, minY + height / bounds.y * secondPos.y,
                            minX + width / bounds.x * thirdPos.x, minY + height / bounds.y * thirdPos.y);
                    firstPos = thirdPos;
                }
            }
        }
    }

    public SnakeBoid Split(int location) {
        SnakeBoid snakeToBeAdded = null;
        ArrayList<PVector> newSegments = new ArrayList<>();
        for (int i = location - 2; i >= 0; i--) {
            newSegments.add(segments.get(i));
        }
        if (newSegments.size() > 0) {
            snakeToBeAdded = new SnakeBoid(bounds, newSegments, new PVector(0, 0));
        }
        segments.subList(0, location).clear();

        return snakeToBeAdded;
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
        float height = 0.07f * segments.size();
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
        float r = 0.4f * segments.size();
        return new PVector(r * PApplet.cos(theta), r * PApplet.sin(theta));
    }

    private PVector RepulsionBehavior(float r, float theta) {
        float height = 0.15f * segments.size();
        float center = 0.5f;
        float width = 3;
        r = -fGaussian(height, center, width, r);
        return new PVector(r * PApplet.cos(theta), r * PApplet.sin(theta));
    }

    private PVector DisruptionBehavior(ArrayList<SnakeBoid> snakes) {
        float closestSnakeDist = 1000f;
        PVector closestSnakeSegment = new PVector();
        for (SnakeBoid snake : snakes) {
            if (snake != this) {
                for (PVector segment : snake.segments) {
                    float dist = PApplet.sqrt((segment.x - pos.x) * (segment.x - pos.x) + (segment.y - pos.y) + (segment.y - pos.y));
                    if (dist < closestSnakeDist) {
                        closestSnakeDist = dist;
                        closestSnakeSegment = segment;
                    }
                }
            }
        }
        float theta = AngleBetween2D(pos, closestSnakeSegment);
        float r = (0.8f - (0.2f * segments.size())) / closestSnakeDist;
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

    private void CheckSelfSegments() {
        for (PVector segment : segments) {
            if (PVectorSoftEquals(target, segment)) {
                if (dir.x == 0) {
                    if (pos.x < bounds.x / 2) {
                        dir.x = 1;
                        dir.y = 0;
                    }
                    else {
                        dir.x = -1;
                        dir.y = 0;
                    }
                }
                else {
                    if (pos.y < bounds.y / 2) {
                        dir.x = 0;
                        dir.y = 1;
                    }
                    else {
                        dir.x = 0;
                        dir.y = -1;
                    }
                }
                target = PVector.add(pos, dir);
            }
        }
    }

    private void CheckSnakes(ArrayList<SnakeBoid> snakes, ArrayList<ArrayList<SnakeBoid>> snakeDiffs) {
        SnakeBoid snakeToBeDeleted = null;
        SnakeBoid snakeToBeAdded = null;
        for (SnakeBoid snake : snakes) {
            if (snake != this && PVectorSoftEquals(snake.target, target)) {
                addSegments = 1 + snake.segments.size() + snake.addSegments;
                snakeToBeDeleted = snake;
            }
            for (int i = 1; i < snake.segments.size(); i++) {
                if (PVectorSoftEquals(target, snake.segments.get(i))) {
                    if (snake != this) {
                        addSegments = 1 + snake.addSegments;
                        snakeToBeAdded = snake.Split(i);
                    }
                }
            }
        }
        if (snakeToBeDeleted != null) {
            snakeDiffs.get(0).add(snakeToBeDeleted);
        }
        if (snakeToBeAdded != null) {
            snakeDiffs.get(1).add(snakeToBeAdded);
        }
    }

    private boolean PVectorSoftEquals(PVector a, PVector b) {
        return (int) a.x == (int) b.x && (int) a.y == (int) b.y;
    }
}
