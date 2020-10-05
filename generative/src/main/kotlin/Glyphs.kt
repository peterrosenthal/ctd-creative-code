import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.noise.Random
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.parameters.ActionParameter
import org.openrndr.extra.parameters.ColorParameter
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.IntParameter
import org.openrndr.math.IntVector2
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class Glyph(val positions: List<Vector2>, val dots: List<Int>, val color: ColorRGBa)

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
            Glyph(
                List(9) { jt -> Vector2(position.x - size / 2.0 + (jt % 3) * size / 3.0, position.y - size / 2.0 + (jt / 3) * size / 3.0) },
                //Array(3) { IntArray(3) {Random.int0(2)} },
                List(9) { Random.int0(3) },
                ColorHSVa(color.h, color.s + Random.double(-0.4, 0.4), color.v + Random.double(-0.1, 0.1)).toRGBa()
            )
        }
        var glyphs = List(0) { Glyph(List(0) { Vector2.ZERO }, List(0) { 0 }, ColorRGBa.YELLOW) }

        val settings = @Description("settings") object {
            @ColorParameter("background color")
            var backgroundColor = ColorRGBa.PINK

            @ColorParameter("glyph color")
            var glyphColor = ColorRGBa.YELLOW

            @IntParameter("number of glyphs", 1, 100)
            var numGlyphs = 1

            @ActionParameter("generate glyphs")
            fun clicked() {
                glyphs = generateGlyphs(numGlyphs, glyphColor.toHSVa())
            }
        }

        extend(GUI()) {
            add(settings)
        }
        extend {
            drawer.clear(settings.backgroundColor)
            glyphs.forEach{ glyph ->
                drawer.fill = glyph.color
                drawer.stroke = glyph.color
                for (x in 0 until 3) {
                    for (y in 0 until 3) {
                        drawer.circle(glyph.positions[x+3*y],10.0 * glyph.dots[x+3*y])
                    }
                }
            }
        }
    }
}