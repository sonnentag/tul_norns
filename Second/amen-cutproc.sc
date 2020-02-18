
//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help


Note that due to scheduling limitations (the anticipatory nature required by latency, PAT and expressive timing corrections and a beat induction clock) swapping procs at tempoclock beat boundaries may miss the first cuts of a beat (because they should have been prescheduled)

Or see the MultiProc help file for fill pattern examples


(
var sf, clock, cutsynth, cutproc;

clock= ExternalClock(TempoClock(2.3));
clock.play;

Routine.run({

sf= BBCutBuffer("/usr/share/SuperCollider/sounds/amen.wav",16);

s.sync; //this forces a wait for the Buffer to load

cutsynth= CutBuf2(sf, dutycycle: CutPBS1({rrand(0.8,1.0)}, 0));
cutproc=BBCutProc11(phrasebars:2, stutterchance:0.8, stutterspeed:{[2,8].wchoose([0.7,0.3])});

g=BBCut2(cutsynth,cutproc).play(clock);
});

)

//the swaps occurs from the next beat; there may be overlaps, missing initial events (usually fine for small cuts)

//change cutproc
g.proc_(WarpCutProc1.new);

g.proc_(BBCPPermute(4.0,8,{|i,n| (i**5)%n},{[1,2].choose}));




