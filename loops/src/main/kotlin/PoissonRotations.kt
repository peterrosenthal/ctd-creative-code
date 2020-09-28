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
import org.openrndr.math.IntVector2
import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import org.openrndr.poissonfill.PoissonFill
import kotlin.math.min

data class Dot(var color: ColorHSVa, var pos: Vector2)

fun main() {
    application {
        configure {
            width = 1018
            height = 1018
            position = IntVector2(1920 - 1018, 0)
        }

        oliveProgram {
            val gui = GUI()

            val parameters = object {
                @DoubleParameter("rotation speed", 0.0, 1.0)
                var rotSpeed = 0.7

                @DoubleParameter("color changing speed", 0.0, 1.0)
                var colorSpeed = 0.35
            }

            val dry = renderTarget(width, height) {
                colorBuffer(type = ColorType.FLOAT32)
            }
            val wet = colorBuffer(width, height)
            val fx = PoissonFill()

            val dots = List(10) {
                Dot(
                        ColorHSVa(if(it > 2) 100.0 - it * 20.0 else 180.0 + it * 20.0, .4, .75),
                        Polar(it * 360.0 / 10.0, min(width, height) / 2.5).cartesian
                )
            }

            extend(gui) {
                add(parameters)
            }
            extend {
                drawer.isolatedWithTarget(dry) {
                    stroke = null
                    clear(ColorRGBa.TRANSPARENT)

                    dots.forEach{ dot ->
                        fill = dot.color.toRGBa()
                        circle(dot.pos + bounds.center, 125.0)

                        dot.color = ColorHSVa(dot.color.h + parameters.colorSpeed, dot.color.s, dot.color.v)

                        val theta = Polar.fromVector(dot.pos).theta + parameters.rotSpeed
                        val r = Polar.fromVector(dot.pos).radius
                        dot.pos = Polar(theta, r).cartesian
                    }
                }

                fx.apply(dry.colorBuffer(0), wet)
                drawer.image(wet)
            }
        }
    }
}