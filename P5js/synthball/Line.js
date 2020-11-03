class Line {
	constructor(x1, y1, x2, y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		this.bounce = 0.98;

		this.note = 1000 - CollisionHelpers.dist(x1, y1, x2, y2);

		this.attackLevel = 0.1;
		this.decayLevel = 0.1;
		this.releaseLevel = 0;
		this.env = new p5.Envelope();
		this.env.setADSR(0.08, 0.18, this.decayLevel, 0.15);
		this.env.setRange(this.attackLevel, this.releaseLevel);

		this.oscillators = {
			sin: new p5.SinOsc(this.note),
			tri: new p5.TriOsc(this.note),
			sqr: new p5.SqrOsc(this.note),
			saw: new p5.SawOsc(this.note)
		};

		this.oscillators.sin.start();
		this.oscillators.sin.amp(this.env);
		this.oscillators.tri.start();
		this.oscillators.tri.amp(this.env);
		this.oscillators.sqr.start();
		this.oscillators.sqr.amp(this.env);
		this.oscillators.saw.start();
		this.oscillators.saw.amp(this.env);
	}

	play(amplitude) {
		this.env.setADSR(0.08, 0.18, this.decayLevel * amplitude, 0.15);
		this.env.setRange(this.attackLevel * amplitude, this.releaseLevel * amplitude);

		this.oscillators.sin.start();
		this.oscillators.sin.amp(this.env);
		this.oscillators.tri.start();
		this.oscillators.tri.amp(this.env);
		this.oscillators.sqr.start();
		this.oscillators.sqr.amp(this.env);
		this.oscillators.saw.start();
		this.oscillators.saw.amp(this.env);

		this.env.play();
	}

	draw() {
		line(this.x1, this.y1, this.x2, this.y2);
	}
}
