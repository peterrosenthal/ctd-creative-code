let gravity = 0.15;

var balls = [];
var lines = [];
var dragStartTime, newX, newY;

function setup() {
	createCanvas(window.innerWidth, window.innerHeight);
	userStartAudio();
}

function draw() {
	background(200);
	stroke(50);

	strokeWeight(1);
	var ballToRemove = null;
	balls.forEach(ball => {
		ball.checkCollisions(lines);
		ball.move(gravity);
		ball.draw();
		if (ball.posY > height) {
			ballToRemove = ball;
		}
	});
	if (ballToRemove != null) {
		let indexToRemove = balls.indexOf(ballToRemove);
		balls.splice(indexToRemove, 1);
	}

	strokeWeight(2.5);
	lines.forEach(line => line.draw());
}

function mousePressed() {
	dragStartTime = millis();
	newX = mouseX;
	newY = mouseY;
}

function mouseReleased() {
	if (CollisionHelpers.dist(newX, newY, mouseX, mouseY) > 10) {
		lines.push(new Line(newX, newY, mouseX, mouseY));
	}
	else {
		balls.push(new Ball(Math.log((millis() - dragStartTime)) * 2, mouseX, mouseY));
	}
}
