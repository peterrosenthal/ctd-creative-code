class CollisionHelpers {
	static lineLineIntersection(x1, y1, x2, y2, x3, y3, x4, y4) {
		let a1 = y2 - y1;
		let b1 = x1 - x2;
		let c1 = a1 * x1 + b1 * y1;
		let a2 = y4 - y3;
		let b2 = x3 - x4;
		let c2 = a2 * x3 + b2 * y3;

		let determinant = a1 * b2 - a2 * b1;
		if (determinant != 0) {
			let x = (b2 * c1 - b1 * c2) / determinant;
			let y = (a1 * c2 - a2 * c1) / determinant;
			return [x, y];
		}
		return null;
	}

	static closestPointOnLine(x0, y0, x1, y1, x2, y2) {
		let a1 = y2 - y1;
		let b1 = x1 - x2;
		let c1 = (y2 - y1) * x1 + (x1 - x2) * y1;
		let c2 = -b1 * x0 + a1 * y0;
		var cx = 0;
		var cy = 0;

		let determinant = a1 * a1 + b1 * b1;
		if (determinant != 0) {
			cx = (a1 * c1 - b1 * c2) / determinant;
			cy = (a1 * c2 + b1 * c1) / determinant;
		}
		else {
			cx = x0;
			cy = y0;
		}
		
		return[cx, cy];
	}

	static dist(x1, y1, x2, y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}
}
