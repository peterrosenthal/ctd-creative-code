import processing.core.PApplet;
import processing.core.PShape;

public class AtlasLogo extends PApplet {

    public int canvasWidth    = 500;
    public int canvasHeight   = 500;
    public int logoWidth      = 280;
    public int logoHeight     = 230;

    public float segmentWidth = 0.25f;
    public float speed        = 2f;

    public PShape[] shapes = new PShape[7];

    public void settings() {
        size(canvasWidth, canvasHeight);
    }

    public void draw() {
        background(0, 0, 0);
        fill(255, 255, 255);
        noStroke();
        updateShapes(millis() * speed * 0.0001f, segmentWidth);
        for (int i = 0; i < shapes.length; i++) {
            shape(shapes[i]);
        }
    }

    public void updateShapes(float t, float w) {
        t = t % 1 + 1;
        for (int i = 0; i < shapes.length; i++) {
            t -= 1f / 3f;
            shapes[i] = createShape();
            shapes[i].beginShape();
            if (t < 0.75f && t + w > 0.75f) {
                shapes[i].vertex((canvasWidth - logoWidth * 0.25f) * 0.5f,
                        (canvasHeight - logoHeight) * 0.5f);
            }
            shapes[i].vertex(lerp((canvasWidth - logoWidth) * 0.5f, (canvasWidth + logoWidth * 0.25f) * 0.5f, (t + w < 0.75f) ? 4f / 5f * constrain(t + w, 0, 1) : 8f / 5f * constrain(t + w, 0, 1) - 3f / 5f),
                    lerp((canvasHeight + logoHeight) * 0.5f, (canvasHeight - logoHeight) * 0.5f, 4f / 3f * constrain(t + w, 0, 0.75f)));
            shapes[i].vertex(lerp((canvasWidth - logoWidth) * 0.5f, (canvasWidth + logoWidth) * 0.5f, constrain(t + w, 0, 1)),
                    (canvasHeight + logoHeight) * 0.5f);
            shapes[i].vertex(lerp((canvasWidth - logoWidth) * 0.5f, (canvasWidth + logoWidth) * 0.5f, constrain(t, 0, 1)),
                    (canvasHeight + logoHeight) * 0.5f);
            shapes[i].vertex(lerp((canvasWidth - logoWidth) * 0.5f, (canvasWidth + logoWidth * 0.25f) * 0.5f, (t < 0.75f) ? 4f / 5f * constrain(t, 0, 1) : 8f / 5f * constrain(t, 0, 1) - 3f / 5f),
                    lerp((canvasHeight + logoHeight) * 0.5f, (canvasHeight - logoHeight) * 0.5f, 4f / 3f * constrain(t, 0, 0.75f)));
            shapes[i].endShape();
        }
    }

    public static void main(String[] args) {
        String[] processingArgs = {"MySketch"};
        AtlasLogo atlasLogo = new AtlasLogo();
        PApplet.runSketch(processingArgs, atlasLogo);
    }
}
