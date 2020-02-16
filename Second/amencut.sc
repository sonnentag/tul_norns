
//This file is part of The BBCut Library. Copyright (C) 2001  Nick M.Collins distributed under the terms of the GNU General Public License full notice in file BBCutLibrary.help


//beat induction and event capture on an existing stereo track using CutStream3
(
var trackbus, trackgroup;

s.latency=0.05;

//clear any existing OSCresponder
OSCresponder.all.do({arg val; if(val.cmdName=='/tr',{OSCresponder.remove(val)}); });

//run a line at a time
~clock= ServerClock.new;

~clock.play(100,s); //will wait on trigID 100

~database=AnalyseEventsDatabase(10,2,s);

Routine.run({

//adding a Limiter before pressing s.record
SynthDef(\limiter,{ReplaceOut.ar(0,Limiter.ar(In.ar(0,2),0.99))}).play(Group.after(Node.basicNew(s,1)));

//choose a stereo file you want to track
~source=Buffer.read(s,"sounds/amen.wav");

s.sync;

~trackbus=Bus.audio(s,2);

trackgroup= Group.before(Node.basicNew(s,1));

//run a beat tracker on the Server which sends the appropriate OSC message
~tracksynth= SynthDef(\beattrackingstereoremix,{arg vol=1.0, beepvol=0.0, lock=0;
var trackb,trackh,trackq,tempo;
var source, beep;

source= PlayBuf.ar(2,~source.bufnum,1.0,1,0,1);

//see AutoTrack help file
#trackb,trackh,trackq,tempo=AutoTrack.kr(Mix(source), lock);

beep= SinOsc.ar(1000,0.0,Decay.kr(trackb,0.1));

Out.ar(~trackbus.index,source);

Out.ar(0,(vol*source)+Pan2.ar((beepvol*beep),0.0));

SendTrig.kr(trackb,100,tempo);	//sends with ID of 100 matching what clock expects

}).play(trackgroup);

//creates at tail of trackgroup
~database.analyse(~trackbus.index, 101, trackgroup, 0.34, ~clock); //trigID 101 is default

});

)




~tracksynth.set(\vol,0.0);

~tracksynth.set(\beepvol,1.0);

~database.threshold_(0.1); //make it more event trigger happy


a=BBCut2(CutGroup(CutStream3(~database, 4, 0.0, 0.0,false,false, 1.0),numChannels:2), SQPusher1.new).play(~clock);

b=BBCut2(CutGroup(CutStream3(~database, 4, 0.0, 1.0,false,false),numChannels:2), WarpCutProc1.new).play(~clock);

c=BBCut2(CutGroup([CutBRF1.new,CutRev1.new,CutStream3(~database, 4, 0.0, 0.0,false,false)],numChannels:2), ChooseCutProc(0.5,4)).play(~clock);

d=BBCut2(CutGroup(CutStream1(~trackbus.index),numChannels:2), ChooseBlockProc(1,8)).play(~clock);

e=BBCut2(CutGroup(CutStream1(~trackbus.index, nil),numChannels:2), BBCutProc11.new).play(~clock);

f=BBCut2(CutGroup(CutStream1(~trackbus.index, nil),numChannels:2), SQPusher1.new).play(~clock);



~database.threshold_(0.02); //make it extremely event trigger happy

a.end;
b.end;
c.end;
d.end;
e.end;
f.end;

~clock.stop;
~database.stop;



//Limiter
//SynthDef(\limiter,{
//var input;
//
//input=In.ar(0,2);
//
//ReplaceOut.ar(0,Limiter.ar(input,0.99));
//}).play(Group.tail(Node.basicNew(s,1)));


