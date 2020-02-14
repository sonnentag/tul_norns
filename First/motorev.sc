(
// first collect some things to play with
SynthDef("moto-rev", { arg out=0;
    var x;
    x = RLPF.ar(LFPulse.ar(SinOsc.kr(0.2, 0, 10, 21), [0,0.1], 0.1),
        100, 0.1).clip2(0.4);
    Out.ar(out, x);
}).add;

SynthDef("bubbles", { arg out=0;
    var f, zout;
    f = LFSaw.kr(0.4, 0, 24, LFSaw.kr([8,7.23], 0, 3, 80)).midicps;
    zout = CombN.ar(SinOsc.ar(f, 0, 0.04), 0.2, 0.2, 4); // echoing sine wave
    Out.ar(out, zout);
}).add;


SynthDef("rlpf",{ arg out=0,ffreq=600,rq=0.1;
    ReplaceOut.ar( out, RLPF.ar( In.ar(out), ffreq,rq) )
}).add;


SynthDef("wah", { arg out, rate = 1.5, cfreq = 1400, mfreq = 1200, rq=0.1;
    var zin, zout;

    zin = In.ar(out, 2);
    cfreq = Lag3.kr(cfreq, 0.1);
    mfreq = Lag3.kr(mfreq, 0.1);
    rq   = Ramp.kr(rq, 0.1);
    zout = RLPF.ar(zin, LFNoise1.kr(rate, mfreq, cfreq), rq, 10).distort
                    * 0.15;

    // replace the incoming bus with the effected version
    ReplaceOut.ar( out , zout );

}).add;

SynthDef("modulate",{ arg out = 0, freq = 1, center = 440, plusMinus = 110;
    Out.kr(out, SinOsc.kr(freq, 0, plusMinus, center));
}).add;
)

// execute these one at a time

// y is playing on bus 0
y = Synth("moto-rev",["out",0]);

// z is reading from bus 0 and replacing that; It must be *after* y
z = Synth.after(y,"wah",["out",0]);

// stop the wah-ing
z.run(false);

// resume the wah-ing
z.run(true);

// add a rlpf after that, reading and writing to the same buss
x = Synth.after(z,"rlpf",["out",0]);

// create another rlpf after x
t = Synth.after(x,"rlpf",["out",0]);

x.set("ffreq", 200);

x.set(\ffreq, 800); // Symbols work for control names too

// Now let's modulate x's ffreq arg
// First get a control Bus
b = Bus.control(s, 1);

// now the modulator, *before* x
m = Synth.before(x, "modulate", [\out, b]);

// now map x's ffreq to b
x.map("ffreq", b);

m.set("freq", 4, "plusMinus", 20);

x.free;
z.free;
m.free;

// now place another synth after y, on the same bus
// they both write to the buss, adding their outputs
r = Synth.after(y,"bubbles",["out",0]);

y.free;

r.free;

// look at the Server window
// still see 4 Ugens and 1 synth?
// you can't hear me, but don't forget to free me
t.free;