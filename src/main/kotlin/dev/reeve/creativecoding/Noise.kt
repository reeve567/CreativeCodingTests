import org.openrndr.Fullscreen
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.color.rgba
import org.openrndr.extensions.Screenshots
import org.openrndr.extra.noclear.NoClear
import org.openrndr.extra.noise.Random
import org.openrndr.math.Polar

fun main() = application {
	configure {
		width = 1920
		height = 1024
		fullscreen = Fullscreen.SET_DISPLAY_MODE
	}
	program {
		val screenshotWriter = Screenshots()
		screenshotWriter.shutdown(this)
		screenshotWriter.name = "screenshot.png"
		
		val zoom = 0.05
		backgroundColor = ColorRGBa.WHITE
		extend(NoClear())
		extend {
			drawer.fill = rgba(0.0, 0.0, 0.0, 0.01)
			drawer.points(generateSequence(Random.point(drawer.bounds)) {
				it + Polar(
					180 * if (it.x < width / 2)
						Random.cubic(it.vector3(z = seconds / 8) * zoom)
					else
						Random.perlin(it.vector3(z = seconds / 8) * zoom)
				).cartesian
			}.take(500).toList())
		}
	}
}