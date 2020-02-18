
//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help

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





