import processing.core.PApplet;
import processing.core.PShape;

public class AtlasLogo extends PApplet {

    public int canvasWidth    = 1000;
    public int canvasHeight   = 1000;
    public int logoWidth      = 500;//280;
    public int logoHeight     = 450;//230;
    public float segmentWidth = 0.25f;
    public float speed        = 2f;
    public int[][] colors = {
            {231,   0,   0},
            {255, 140,   0},
            {255, 239,   0},
            {  0, 129,  31},
            {  0,  68, 255},
            {110,   0, 137},
    };
    public PShape[] shapes = new PShape[12];

    public void settings() {
        size(canvasWidth, canvasHeight);
    }

    public void draw() {
        background(0, 0, 0);
        updateShapes(millis() * speed * 0.0001f);
        for (int i = 0; i < shapes.length; i++) {
            shape(shapes[i]);
        }
    }

    public void updateShapes(float t) {
        t = t % 2 + 1;
        for (int i = 0; i < shapes.length; i++) {
            t -= 1f / 3f;
            shapes[i] = createShape();
            shapes[i].beginShape();

            if (t < 0.75f && t + segmentWidth > 0.75f) {
                shapes[i].vertex((canvasWidth - logoWidth * 0.25f) * 0.5f,
                        (canvasHeight - logoHeight) * 0.5f);
            }
            shapes[i].vertex(lerp((canvasWidth - logoWidth) * 0.5f, (canvasWidth + logoWidth * 0.25f) * 0.5f, (t + segmentWidth < 0.75f) ? 4f / 5f * constrain(t + segmentWidth, 0, 1) : 8f / 5f * constrain(t + segmentWidth, 0, 1) - 3f / 5f),
                    lerp((canvasHeight + logoHeight) * 0.5f, (canvasHeight - logoHeight) * 0.5f, 4f / 3f * constrain(t + segmentWidth, 0, 0.75f)));
            shapes[i].vertex(lerp((canvasWidth - logoWidth) * 0.5f, (canvasWidth + logoWidth) * 0.5f, constrain(t + segmentWidth, 0, 1)),
                    (canvasHeight + logoHeight) * 0.5f);
            shapes[i].vertex(lerp((canvasWidth - logoWidth) * 0.5f, (canvasWidth + logoWidth) * 0.5f, constrain(t, 0, 1)),
                    (canvasHeight + logoHeight) * 0.5f);
            shapes[i].vertex(lerp((canvasWidth - logoWidth) * 0.5f, (canvasWidth + logoWidth * 0.25f) * 0.5f, (t < 0.75f) ? 4f / 5f * constrain(t, 0, 1) : 8f / 5f * constrain(t, 0, 1) - 3f / 5f),
                    lerp((canvasHeight + logoHeight) * 0.5f, (canvasHeight - logoHeight) * 0.5f, 4f / 3f * constrain(t, 0, 0.75f)));

            shapes[i].fill(colors[i % colors.length][0], colors[i % colors.length][1], colors[i % colors.length][2]);
            shapes[i].noStroke();

            shapes[i].endShape();
        }
    }

    public static void main(String[] args) {
        String[] processingArgs = {"MySketch"};
        AtlasLogo atlasLogo = new AtlasLogo();
        PApplet.runSketch(processingArgs, atlasLogo);
    }
}
