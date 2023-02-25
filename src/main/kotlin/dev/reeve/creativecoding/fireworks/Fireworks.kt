import org.openrndr.Fullscreen
import org.openrndr.Program
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.RectangleBatchBuilder
import org.openrndr.draw.renderTarget
import org.openrndr.extra.noise.Random
import org.openrndr.ffmpeg.VideoWriter
import org.openrndr.math.Vector2
import org.openrndr.shape.Rectangle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

private val speedModifier = 0.2
private val fireColors = listOf(
	ColorRGBa.fromHex("#ff0000"),
	ColorRGBa.fromHex("#ff6800")
)

private val fireworkColors = listOf(
	ColorRGBa.CYAN,
	ColorRGBa.PINK,
	ColorRGBa.GREEN,
	ColorRGBa.YELLOW,
	ColorRGBa.WHITE,
	ColorRGBa.RED,
)

fun main() = application {
	configure {
		fullscreen = Fullscreen.SET_DISPLAY_MODE
	}
	
	program {
		val videoWriter = VideoWriter.create().size(width, height).output("output.mp4").start()
		
		val videoTarget = renderTarget(width, height) {
			colorBuffer()
			depthBuffer()
		}
		
		var fireworks = mutableListOf<Firework>()
		
		var last = 0L
		extend {
			if (System.currentTimeMillis() / 10 != last && System.currentTimeMillis() / 10 % 20L == 0L) {
				last = System.currentTimeMillis() / 10
				fireworks.add(Firework(this, ColorRGBa.MAGENTA))
			}
			
			fireworks = fireworks.filter {
				it.run()
				!it.done
			}.toMutableList()
		}
	}
}

data class Position(var x: Double, var y: Double) {
	constructor(vector2: Vector2) : this(vector2.x, vector2.y)
	
	fun clone(): Position {
		return Position(x, y)
	}
	
	fun toVector(): Vector2 {
		return Vector2(x, y)
	}
	
	fun plus(vector2: Vector2): Position {
		return plus(Position(vector2))
	}
	
	fun plusMut(vector2: Vector2) {
		plusMut(Position(vector2))
	}
	
	fun plus(pos: Position): Position {
		return Position(x, y)
	}
	
	fun plusMut(pos: Position) {
		x += pos.x
		y += pos.y
	}
}

data class Firework(private val program: Program, var color: ColorRGBa) {
	private val speed = program.height * speedModifier
	private val start = program.seconds
	private var lastRun = program.seconds
	private var pos = Position(Random.double(0.0) * program.width / 3 + program.width / 3, program.height.toDouble())
	private var size = program.width * 0.004
	private var fireColor = fireColors.random()
	
	private val particles = mutableListOf<ExplosionParticle>()
	
	private var initializedExplosion = false
	private var explosionTime = 3 + Random.double(-1.0, 1.0)
	
	var done = false
	
	fun run() {
		with(program) {
			val time = seconds - lastRun
			
			if (seconds - start < explosionTime) {
				pos.y -= speed * time
				
				drawer.fill = fireColor
				drawer.rectangle(pos.toVector().plus(Vector2(0.0, size / 1.5)), size)
				
				drawer.fill = color
				drawer.rectangle(pos.toVector(), size)
				
				lastRun = seconds
			} else {
				if (!initializedExplosion) {
					val color = fireworkColors.random()
					for (i in 0 until 80) {
						particles.add(ExplosionParticle(this, color, pos.clone()))
					}
					initializedExplosion = true
				} else {
					drawer.rectangles {
						particles.forEach {
							it.run(this)
						}
					}
				}
			}
			
			if (seconds - start >= 7) {
				done = true
			}
		}
	}
}

data class ExplosionParticle(private val program: Program, var color: ColorRGBa, var pos: Position) {
	private val start = program.seconds
	private var lastRun = start
	private val angle = let {
		val distance = Random.double(0.03, 0.2)
		val angle = Random.double(0.0, 2 * PI)
		
		return@let Vector2(distance * cos(angle), distance * sin(angle))
	}
	private var size = program.width * 0.003
	private val fadingParticles = mutableListOf<FadingParticle>()
	
	private var lastTime = 0L
	
	fun run(batchBuilder: RectangleBatchBuilder) {
		with(program) {
			val time = seconds - lastRun
			val speed = angle.times(max(1.5 - (time / 3), 0.0))
			pos.plusMut(speed)
			
			pos.plusMut(Vector2(0.0, (seconds - start) / 15))
			
			if (lastTime != System.currentTimeMillis() / 100) {
				color = color.minus(ColorRGBa(0.0, 0.0, 0.0, 0.03))
				lastTime = System.currentTimeMillis() / 100
				
				fadingParticles.add(FadingParticle(program, color.copy(), pos.clone()))
				if (fadingParticles.size > 5) {
					fadingParticles.removeAt(0)
				}
			}
			
			fadingParticles.forEach { it.run(batchBuilder) }
			
			batchBuilder.fill = color
			batchBuilder.rectangle(Rectangle(pos.toVector(), size))
		}
	}
}

class FadingParticle(private val program: Program, var color: ColorRGBa, var pos: Position) {
	private var lastTime = 0L
	private var size = program.width * 0.003
	
	fun run(batchBuilder: RectangleBatchBuilder) {
		if (lastTime != System.currentTimeMillis() / 100) {
			color = color.minus(ColorRGBa(0.0, 0.0, 0.0, 0.08))
			lastTime = System.currentTimeMillis() / 100
		}
		with(batchBuilder) {
			fill = color.minus(ColorRGBa(0.0, 0.0, 0.0, 0.1))
			rectangle(Rectangle(pos.toVector(), size))
		}
	}
}