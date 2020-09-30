import org.openrndr.application
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorType
import org.openrndr.draw.colorBuffer
import org.openrndr.draw.isolatedWithTarget
import org.openrndr.draw.renderTarget
import org.openrndr.extra.gui.GUI
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.ffmpeg.VideoWriter
import org.openrndr.math.IntVector2
import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import org.openrndr.poissonfill.PoissonFill
import kotlin.math.min

data class Dot(var color: ColorHSVa, var pos: Polar)

fun main() {
    application {
        configure {
            width = 1018
            height = 1018
            position = IntVector2(1920 - 1018, 0)
        }

        //oliveProgram {
        program {
            val dry = renderTarget(width, height) {
                colorBuffer(type = ColorType.FLOAT32)
            }
            val wet = colorBuffer(width, height)
            val fx = PoissonFill()

            val dots = List(10) {
                Dot(
                        ColorHSVa(if(it > 2) 100.0 - it * 20.0 else 180.0 + it * 20.0, .4, .75),
                        Polar(it * 360.0 / 10.0, min(width, height) / 2.5)
                )
            }

            extend(ScreenRecorder())
            extend {
                drawer.isolatedWithTarget(dry) {
                    stroke = null
                    clear(ColorRGBa.TRANSPARENT)

                    dots.forEach{ dot ->
                        fill = dot.color.toRGBa()
                        circle(dot.pos.cartesian + bounds.center, 125.0)

                        dot.color = ColorHSVa(dot.color.h + 0.35, dot.color.s, dot.color.v)
                        dot.pos = Polar(dot.pos.theta + 0.7, dot.pos.radius)
                    }
                }

                fx.apply(dry.colorBuffer(0), wet)
                drawer.image(wet)

                if (frameCount > 1028) {
                    application.exit()
                }
            }
        }
    }
}