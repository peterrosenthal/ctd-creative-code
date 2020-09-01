import processing.core.PApplet;
import processing.core.PShape;

public class AtlasLogo extends PApplet {

    // Define a bunch of variables
    public int canvasWidth      = 500;       // Width of the canvas
    public int canvasHeight     = 500;       // Height "  "    "
    public int logoWidth        = 280;       // Width  "  "  logo
    public int logoHeight       = 230;       // Height "  "    "
    public float segmentWidth   = 0.25f;     // Width  " each segment
    public float segmentSpacing = 1f / 3f;   // Spacing between each segment as a multiple of the logo width
    public float speed          = 2f;        // Speed of the animation
    public int[][] colors = {                // Array containing all the colors as RGB pairs
            {231,   0,   0},                 // You need at least 6 but can have as many more as you want
            {255, 140,   0},
            {255, 239,   0},
            {  0, 129,  31},
            {  0,  68, 255},
            {110,   0, 137}
    };
    public float[][] logoVertices = {
            {(canvasWidth - logoWidth * 0.25f) * 0.5f,   // Top Left x
                    (canvasHeight - logoHeight) * 0.5f}, //  "   "   y
            {(canvasWidth + logoWidth * 0.25f) * 0.5f,   //  " Right x
                    (canvasHeight - logoHeight) * 0.5f}, //  "   "   y
            {(canvasWidth - logoWidth) * 0.5f,           // Bottom Left x
                    (canvasHeight + logoHeight) * 0.5f}, //   "     "   y
            {(canvasWidth + logoWidth) * 0.5f,           //   "   Right x
                    (canvasHeight + logoHeight) * 0.5f}  //   "     "   y
    };

    public void settings() { // Apparently you use settings() instead of setup() when not using the Processing IDE
        size(canvasWidth, canvasHeight);
    }

    public void draw() {
        background(0, 0, 0);

        // The main algorithm
        for (int i = 0; i < colors.length * 2; i++) {
            // 't' is our main time keeping variable based on milliseconds instead of frames so the animation plays the same speed regardless of framerate
            float t = (millis() * speed * 0.0001f) % (colors.length * 2 / 6f) + 1 - i * segmentSpacing; // 't' is offset using the segmentSpacing variable here
            float xt, yt; // 'xt' and 'yt' are x and y variations of 't' that will be set and re-set for each vertex

            PShape shape = createShape();
            shape.beginShape();

            // First vertex
            if (t < 3f / 4f) {
                xt = 4f / 5f * constrain(t, 0, 1);           // We must travel slower for the first 3/4 of the time
            } else {
                xt = 8f / 5f * constrain(t, 0, 1) - 3f / 5f; // And faster for the last 1/4
            }
            yt = 4f / 3f * constrain(t, 0, 3f / 4f);         // We need to travel 100% of the vertical distance in just 3/4 the time
            shape.vertex(
                    lerp(logoVertices[2][0], logoVertices[1][0], xt),  // Travel from the Bottom Left vertex to the Top Right vertex
                    lerp(logoVertices[2][1], logoVertices[1][1], yt)   // 'Traveling' is just linear interpolation
            );

            // Second vertex
            if (t < 3f / 4f && t + segmentWidth > 3f / 4f) {           // The second vertex has a fixed location in the Top Left
                shape.vertex(logoVertices[0][0], logoVertices[0][1]);  // Only add it if the first and third are spanning it
            }

            // Third vertex
            if (t + segmentWidth < 3f / 4f) {
                xt = 4f / 5f * constrain(t + segmentWidth, 0, 1);
            } else {
                xt = 8f / 5f * constrain(t + segmentWidth, 0, 1) - 3f / 5f;
            }
            yt = 4f / 3f * constrain(t + segmentWidth, 0, 3f / 4f);
            shape.vertex(                                              // The third vertex is identical to the first
                    lerp(logoVertices[2][0], logoVertices[1][0], xt),  // but offset forward in "time" by the segmentWidth
                    lerp(logoVertices[2][1], logoVertices[1][1], yt)
            );

            // Fourth vertex
            xt = constrain(t + segmentWidth, 0, 1);      // The fourth and fifth vertices are identical to
            yt = constrain(t + segmentWidth, 0, 1);      // each other except offset by segmentWidth (like
            shape.vertex(                                              // the first and third were). They just travel along
                    lerp(logoVertices[2][0], logoVertices[3][0], xt),  // the bottom from the Bottom Left to the Bottom Right
                    lerp(logoVertices[2][1], logoVertices[3][1], yt)
            );

            // Fifth vertex
            xt = constrain(t, 0, 1);
            yt = constrain(t, 0, 1);
            shape.vertex(
                    lerp(logoVertices[2][0], logoVertices[3][0], xt),
                    lerp(logoVertices[2][1], logoVertices[3][1], yt)
            );

            // Assign a color from the colors array to the segment of the logo
            shape.fill(colors[i % colors.length][0], colors[i % colors.length][1], colors[i % colors.length][2]);
            shape.noStroke();

            shape.endShape();
            shape(shape); // Call the processing shape function to draw the shape
        }
    }

    // This function acts as the "entry point" for Java or something.
    // If you are just using Processing or p5.js you do NOT have
    // to worry about writing any function similar to this one.
    public static void main(String[] args) {
        PApplet.runSketch(new String[]{""}, new AtlasLogo());
    }
}
