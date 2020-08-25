import processing.core.PApplet;

public class AtlasLogo extends PApplet {

    public void settings() {
        size(500, 500);
    }

    public void draw() {
        background(0);
        ellipse(mouseX, mouseY, 50, 50);
    }

    public static void main(String[] args) {
        String[] processingArgs = {"MySketch"};
        AtlasLogo atlasLogo = new AtlasLogo();
        PApplet.runSketch(processingArgs, atlasLogo);
    }
}
