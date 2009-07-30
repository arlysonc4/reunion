unit configwindow;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils, FileUtil, LResources, Forms, Controls, Graphics, Dialogs,
  StdCtrls, ComCtrls, math;

type

  { TForm2 }

  TForm2 = class(TForm)
    Savebtn: TButton;
    cancelbtn: TButton;
    musicchk: TCheckBox;
    soundchk: TCheckBox;
    d3soundchk: TCheckBox;
    resolutioncb: TComboBox;
    Fullscreenchk: TCheckBox;
    soundlevel: TTrackBar;
    musiclevel: TTrackBar;
    Weatherchk: TCheckBox;
    Shadowchk: TCheckBox;
    procedure cancelbtnClick(Sender: TObject);
    procedure SavebtnClick(Sender: TObject);
  private
    { private declarations }
  public
    procedure loadprefs();
    { public declarations }
  end; 

var
  Form2: TForm2; 

implementation

procedure TForm2.cancelbtnClick(Sender: TObject);
begin
  close;
end;

procedure TForm2.SavebtnClick(Sender: TObject);
var F          :Textfile;
    t          :integer;
begin
  //write config.ini
  assignfile(F,'config.ini');
  rewrite(F);
  if IOResult=0 then
  Begin
    writeln(F,'# Dont touch this file.');
    writeln(F,'# If you cant handle this file, delete it.');
    t:=resolutioncb.ItemIndex;
    writeln(F,'Resolution '+inttostr(t));
    if self.Shadowchk.Checked then t:=1
    else t:=0;
    writeln(F,'Shadow '+inttostr(t));
    if self.Weatherchk.Checked then t:=1
    else t:=0;
    writeln(F,'Weather '+inttostr(t));
    if self.Musicchk.Checked then t:=1
    else t:=0;
    writeln(F,'MusicOn '+inttostr(t));
    if self.Soundchk.Checked then t:=1
    else t:=0;
    writeln(F,'MusicEffectOn '+inttostr(t));
    t:=musiclevel.Position;
    writeln(F,'MusicLevel '+inttostr(t));
    t:=soundlevel.Position;
    writeln(F,'MusicEffectLevel '+inttostr(t));
    if self.Fullscreenchk.Checked then t:=0
    else t:=1;
    writeln(F,'Windowed '+inttostr(t));
    writeln(F,'ColorBit 0');
    writeln(F,'ZoneMusic 0');
    closefile(F);
  end;

  //write the 3dSound.ini
  assignfile(F,'3dsound.ini');
  rewrite(F);
  if IOResult=0 then
  Begin
    if self.d3soundchk.Checked then t:=1
    else t:=0;
    writeln(F,inttostr(t));
    writeln(F,0);
    closefile(F);
  end;
  close;
end;

procedure Tform2.loadprefs();
var F          :Textfile;
    line       :string;
    t          :integer;
begin
  // read/parse config ini
  assignfile(F,'config.ini');
  reset(F);
  if IOResult=0 then
  Begin
    while not eof(F) do
    Begin
      readln(F,line);
      if pos('Resolution',line)>0 then
      Begin
        line:=StringReplace(line,'Resolution ','',[rfIgnoreCase]);
        t:=strtoint(line);
        t:=min(max(t,0),2);
        resolutioncb.ItemIndex:=t;
      end else
      if pos('Shadow',line)>0 then
      Begin
        line:=StringReplace(line,'Shadow ','',[rfIgnoreCase]);
        t:=strtoint(line);
        self.Shadowchk.Checked:=(t<>0);
      end else
      if pos('Weather',line)>0 then
      Begin
        line:=StringReplace(line,'Weather ','',[rfIgnoreCase]);
        t:=strtoint(line);
        self.Weatherchk.Checked:=(t<>0);
      end else
      if pos('MusicOn',line)>0 then
      Begin
        line:=StringReplace(line,'MusicOn ','',[rfIgnoreCase]);
        t:=strtoint(line);
        self.musicchk.Checked:=(t<>0);
      end else
      if pos('MusicEffectOn',line)>0 then
      Begin
        line:=StringReplace(line,'MusicEffectOn ','',[rfIgnoreCase]);
        t:=strtoint(line);
        self.soundchk.Checked:=(t<>0);
      end else
      if pos('Windowed',line)>0 then
      Begin
        line:=StringReplace(line,'Windowed ','',[rfIgnoreCase]);
        t:=strtoint(line);
        self.Fullscreenchk.Checked:=(t=0);
      end else
      if pos('MusicEffectLevel',line)>0 then
      Begin
        line:=StringReplace(line,'MusicEffectLevel ','',[rfIgnoreCase]);
        t:=strtoint(line);
        t:=min(max(t,0),100);
        self.soundlevel.Position:=t;
      end else
      if pos('MusicLevel',line)>0 then
      Begin
        line:=StringReplace(line,'MusicLevel ','',[rfIgnoreCase]);
        t:=strtoint(line);
        t:=min(max(t,0),100);
        self.Musiclevel.Position:=t;
      end else
    end;
    closefile(F);
  end;

  // 3d Sound
  assignfile(F,'3dsound.ini');
  reset(F);
  if IOResult=0 then
  Begin
    readln(F,line);
    t:=strtoint(line);
    self.d3soundchk.Checked:=(t<>0);
    closefile(F);
  end;
  show;
end;



initialization
  {$I configwindow.lrs}

end.

