import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.noise.Random
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.parameters.*
import org.openrndr.math.IntVector2
import org.openrndr.math.Vector2
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class Glyph(val positions: List<Vector2>, val lines: List<Int>,val dots: List<Int>, val color: ColorRGBa)

fun main() = application {
    configure {
        val topBarHeight = 33
        width = 1080 - topBarHeight
        height = 1080 - topBarHeight
        position = IntVector2(1920 + topBarHeight - 1080, 0)
    }

    oliveProgram {
        fun generateGlyphs(number: Int, color: ColorHSVa) = List(number) {
            var rows = ceil(sqrt(number.toDouble())).toInt()
            var columns = rows
            if (rows * (rows - 1) >= number) {
                if (width > height) {
                    rows--
                }
                else {
                    columns--
                }
            }
            val position = Vector2((it % columns + 1) * width / (columns + 1.0), (it / columns + 1) * height / (rows + 1.0))
            val size = min(width, height) / (max(rows, columns) + 4.0)

            val straights = List(12) { Random.int0(2) }
            val diagonals = List(8) { Random.int0(2) }

            Glyph(
                List(9) { jt -> Vector2(position.x - size / 2.0 + (jt % 3) * size / 3.0, position.y - size / 2.0 + (jt / 3) * size / 3.0) },
                straights + diagonals,
                listOf(
                        straights[ 0] + straights[ 6]                                 + diagonals[0],
                        straights[ 0] + straights[ 3] + straights[ 7]                 + diagonals[1] + diagonals[2],
                        straights[ 3] + straights[ 8]                                 + diagonals[3],
                        straights[ 1] + straights[ 6] + straights[ 9]                 + diagonals[1] + diagonals[4],
                        straights[ 1] + straights[ 4] + straights[ 7] + straights[10] + diagonals[0] + diagonals[3] + diagonals[5] + diagonals[6],
                        straights[ 4] + straights[ 8] + straights[11]                 + diagonals[2] + diagonals[7],
                        straights[ 2] + straights[ 9]                                 + diagonals[5],
                        straights[ 2] + straights[ 5] + straights[10]                 + diagonals[4] + diagonals[7],
                        straights[ 5] + straights[11]                                 + diagonals[6]
                ),
                ColorHSVa(color.h, color.s + Random.double(-0.4, 0.4), color.v + Random.double(-0.1, 0.1)).toRGBa()
            )
        }
        var glyphs: List<Glyph>

        val settings = @Description("settings") object {
            @ColorParameter("background color", 1)
            var backgroundColor = ColorRGBa.PINK

            @ColorParameter("glyph color", 2)
            var glyphColor = ColorRGBa.YELLOW

            @IntParameter("number of glyphs", 1, 100, 3)
            var numGlyphs = 1

            @DoubleParameter("dot size", 0.0, 2.0, 4)
            var dotScale = 1.0

            @DoubleParameter("line thickness", 0.0, 2.0, 5)
            var lineWeight = 1.0

            @ActionParameter("generate glyphs", 6)
            fun clicked() {
                glyphs = generateGlyphs(numGlyphs, glyphColor.toHSVa())
            }
        }

        glyphs = generateGlyphs(settings.numGlyphs, settings.glyphColor.toHSVa())

        extend(GUI()) {
            add(settings)
        }
        extend {
            drawer.clear(settings.backgroundColor)
            glyphs.forEach{ glyph ->
                drawer.fill = glyph.color
                drawer.stroke = glyph.color
                drawer.strokeWeight = 4.0 * settings.lineWeight

                glyph.lines.forEachIndexed { index, line ->
                    if (line > 0) {
                        when (index) { // I am SUPER embarrassed by this, but I cant think of any better way than a 20 entry LUT
                            0  -> drawer.lineSegment(glyph.positions[0], glyph.positions[1])
                            1  -> drawer.lineSegment(glyph.positions[3], glyph.positions[4])
                            2  -> drawer.lineSegment(glyph.positions[6], glyph.positions[7])
                            3  -> drawer.lineSegment(glyph.positions[1], glyph.positions[2])
                            4  -> drawer.lineSegment(glyph.positions[4], glyph.positions[5])
                            5  -> drawer.lineSegment(glyph.positions[7], glyph.positions[8])
                            6  -> drawer.lineSegment(glyph.positions[0], glyph.positions[3])
                            7  -> drawer.lineSegment(glyph.positions[1], glyph.positions[4])
                            8  -> drawer.lineSegment(glyph.positions[2], glyph.positions[5])
                            9  -> drawer.lineSegment(glyph.positions[3], glyph.positions[6])
                            10 -> drawer.lineSegment(glyph.positions[4], glyph.positions[7])
                            11 -> drawer.lineSegment(glyph.positions[5], glyph.positions[8])
                            12 -> drawer.lineSegment(glyph.positions[0], glyph.positions[4])
                            13 -> drawer.lineSegment(glyph.positions[3], glyph.positions[1])
                            14 -> drawer.lineSegment(glyph.positions[1], glyph.positions[5])
                            15 -> drawer.lineSegment(glyph.positions[4], glyph.positions[2])
                            16 -> drawer.lineSegment(glyph.positions[3], glyph.positions[7])
                            17 -> drawer.lineSegment(glyph.positions[6], glyph.positions[4])
                            18 -> drawer.lineSegment(glyph.positions[4], glyph.positions[8])
                            19 -> drawer.lineSegment(glyph.positions[7], glyph.positions[5])
                        }
                    }
                }
                
                glyph.dots.forEachIndexed { index, dot ->
                    drawer.circle(glyph.positions[index], 5.0 * dot * settings.dotScale)
                }
            }
        }
    }
}