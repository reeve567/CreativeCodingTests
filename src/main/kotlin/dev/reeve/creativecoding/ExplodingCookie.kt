import org.openrndr.application
import org.openrndr.draw.loadImage

fun main() = application {
	configure {
		width = 768
		height = 576
	}
	
	program {
		val cookie = loadImage("data/images/cookie.png")
		val explosion = loadImage("data/images/explosion.png")
		val kick = loadImage("data/images/ninja-kick.png")
		
		extend {
			//drawer.drawStyle.colorMatrix = tint(ColorRGBa.WHITE.shade(0.2))
			//drawer.image(image)
			
			if (seconds < 2) {
				drawer.image(cookie, width / 2.0 - 50, height / 2.0 - 50, 100.0, 100.0)
				
			} else if (seconds >= 2 && seconds < 8) {
				drawer.image(cookie, width / 2.0 - 50, height / 2.0 - 50, 100.0, 100.0)
				
				val size = seconds * 100
				drawer.image(explosion, width / 2.0 - size / 2, height / 2.0 - size / 2, size, size)
				
				drawer.image(kick, width / 2.0 - size / 2, height / 2.0 - size / 2, size, size)
			} else if (seconds >= 8) {
			
			}
		}
	}
}
