class Ball {
	constructor(radius, posX, posY) {
		this.radius = radius;
		this.posX = posX;
		this.posY = posY;
		this.velX = 0;
		this.velY = 0;
	}

	move(gravity) {
		this.posX += this.velX;
		this.posY += this.velY;
		this.velY += gravity; 
	}

	checkCollisions(lines) {
		lines.forEach(line => {
			let p1 = CollisionHelpers.closestPointOnLine(this.posX, this.posY, line.x1, line.y1, line.x2, line.y2);
			let p1x = constrain(p1[0], Math.min(line.x1, line.x2), Math.max(line.x1, line.x2));
			let p1y = constrain(p1[1], Math.min(line.y1, line.y2), Math.max(line.x1, line.x2));

			let a = CollisionHelpers.lineLineIntersection(this.posX, this.posY, this.posX + this.velX, this.posY + this.velY, line.x1, line.y1, line.x2, line.y2);
			if (a != null) {
				let ax = a[0];
				let ay = a[1];

				let collided = false;

				if (CollisionHelpers.dist(p1x, p1y, this.posX, this.posY) < this.radius) {
					collided = true;
				}
				else {
					//let b = CollisionHelpers.closestPointOnLine(this.posX, this.posY,   line.x1,   line.y1,               line.x2,               line.y2);
					//let c = CollisionHelpers.closestPointOnLine(  line.x1,   line.y1, this.posX, this.posY, this.posX + this.velX, this.posY + this.velY);
					//let d = CollisionHelpers.closestPointOnLine(  line.x2,   line.y2, this.posX, this.posY, this.posX + this.velX, this.posY + this.velY);

					//let bx = constrain(b[0], Math.min(line.x1, line.x2), Math.max(line.x1, line.x2));
					//let by = constrain(b[1], Math.min(line.y1, line.y2), Math.max(line.y1, line.y2));
					//let cx = c[0];
					//let cy = c[1];
					//let dx = d[0];
					//let dy = d[1];
					
					if ((ax >= Math.min(  line.x1,               line.x2) && ax <= Math.max(  line.x1,               line.x2)
					  && ax >= Math.min(this.posX, this.posX + this.velX) && ax <= Math.max(this.posX, this.posX + this.velX)
					  && ay >= Math.min(  line.y1,               line.y2) && ay <= Math.max(  line.y1,               line.y2)
					  && ay >= Math.min(this.posY, this.posY + this.velY) && ay <= Math.max(this.posY, this.posY + this.velY))) {
					  //|| CollisionHelpers.dist(bx, by, this.posX + this.velX, this.posY + this.velY) < this.radius
					  //|| CollisionHelpers.dist(cx, cy,               line.x1,               line.y1) < this.radius
					  //|| CollisionHelpers.dist(dx, dy,               line.x2,               line.y2) < this.radius) {
						collided = true;
					}
				}

				if (collided) {
					let n1x = line.y1 - line.y2;
					let n1y = line.x2 - line.x1;
					let n2x = line.y2 - line.y1;
					let n2y = line.x1 - line.x2;

					let dist = this.radius * (CollisionHelpers.dist(ax, ay, this.posX, this.posY) / CollisionHelpers.dist(p1x, p1y, this.posX, this.posY));
					let magVel = CollisionHelpers.dist(0, 0, this.velX, this.velY);

					let p2x = ax - dist * this.velX / magVel;
					let p2y = ay - dist * this.velY / magVel;

					let p3 = CollisionHelpers.closestPointOnLine(p2x, p2y, line.x1, line.y1, line.x2, line.y2);
					let p3x = p3[0];
					let p3y = p3[1];

					if (p3x >= Math.min(line.x1, line.x2) && p3x <= Math.max(line.x1, line.x2)
					 && p3y >= Math.min(line.y1, line.y2) && p3y <= Math.max(line.y1, line.y2)) {
						var nx = 0;
						var ny = 0;
						if ((p3x - this.posX) * n1x + (p3y - this.posY) * n1y > 0 && (p3x - this.posX) * n2x + (p3y - this.posY) * n2y <= 0) {
							nx = n1x;
							ny = n1y;
						}
						else if ((p3x - this.posX) * n2x + (p3y - this.posY) * n2y > 0 && (p3x - this.posX) * n1x + (p3y - this.posY) * n1y <= 0) {
							nx = n2x;
							ny = n2y;
						}
						else {
							console.log("oh no!");
						}
						let magN = CollisionHelpers.dist(0, 0, nx, ny);
						nx /= magN;
						ny /= magN;

						let nDotV = this.velX * nx + this.velY * ny;

						let noteVelocity = Math.pow((1 - ((p3x - this.posX) * nx + (p3y - this.posY) * ny)) * magVel, 4) / 100000000;
						line.play(constrain((10 + 0.0001 * this.radius) * noteVelocity, 0, 2));

						this.velX -= 2 * nx * nDotV * line.bounce;
						this.velY -= 2 * ny * nDotV * line.bounce;
					}
				}
			}
		});
	}

	draw() {
		ellipse(this.posX, this.posY, this.radius * 2);
		line(this.posX, this.posY, this.posX + this.velX, this.posY + this.velY);
	}
}
