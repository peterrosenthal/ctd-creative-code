let azimuth = 4294967040;
let distance = 4294967040;
      
let radius = 200;
let frequency = 100;
let amp = 1;
      
let osc;

window.onSoliEvent = function(event) {
    if (event.data.detected == true) {
      azimuth = event.data.angle[0];
      distance = event.data.distance;
    }
};

function setup(){ 
  createCanvas(window.innerWidth,window.innerHeight); 
  position = width/2; 
  speed = 0;
  userStartAudio();
  osc = new p5.Oscillator('sine');
  osc.start();
}

function draw(){ 
  noStroke();
  fill(255,255,255);
  background(0);
  
  if (distance < 4294967040 && distance >= 0) {
    radius = 200 * (1 - distance);
    amp = constrain(map(distance, 0, 1, 1, 0), 0, 1);
    //console.log(distance);
    osc.amp(amp, 0.1);
  }
  else {
    radius = 0;
  }
  
  if (azimuth < 4294967040 && azimuth >= 0) {
    frequency = constrain(map(azimuth, 0, 100, 300, 800), 300, 800);
    //console.log(azimuth);
    osc.freq(frequency, 0.1);
  }
  
  ellipse(
     position,
     height/2,
     radius);
}
   
  //This detects if the prototype is opened in Soli Sandbox, and sends an alert to the user that soli functionality will not work in other apps/browswe
  if(!navigator.userAgent.includes("Soli Sandbox")){ alert("This prototype needs to be opened in Soli Sandbox in order to receive Soli Events. Soli functionality will not work.");} else {console.log("Soli Sandbox Detected");}

