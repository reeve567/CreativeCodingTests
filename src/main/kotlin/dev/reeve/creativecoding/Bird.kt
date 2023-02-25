package dev.reeve.creativecoding

import org.openrndr.application
import org.openrndr.draw.loadImage

fun main() = application {
	configure {}
	program {
		val bird = loadImage("data/images/bird-no-mouth.png")
		val topMouth = loadImage("data/images/bird-top-mouth.png")
		val bottomMouth = loadImage("data/images/bird-bottom-mouth.png")
		
		extend {
			if (seconds < 2) {
				drawer.image(bird, width / 2.0 - 250, height / 2.0 - 250, 500.0, 500.0)
				drawer.image(topMouth, width / 2.0 - 250, height / 2.0 - 250, 500.0, 500.0)
				drawer.image(bottomMouth, width / 2.0 - 250, height / 2.0 - 250, 500.0, 500.0)
				
			} else if (seconds > 2) {
				
				drawer.rotate(-15.0)
				
				drawer.rotate(15.0)
				
			}
			
			
			if (seconds > 2) {
			}
			
			if (seconds > 2) {
			}
		}
	}
}