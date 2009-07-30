unit readlist;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils, FileUtil, LResources, Forms, Controls, Graphics, Dialogs,
  StdCtrls, strutils;

type
  { TForm1 }

  TForm1 = class(TForm)
    Button1: TButton;
    Button2: TButton;
    Memo1: TMemo;
    OpenDialog: TOpenDialog;
    SaveDialog: TSaveDialog;
    procedure Button1Click(Sender: TObject);
    procedure Button2Click(Sender: TObject);
  private
     procedure loadit(filename:string);
     procedure saveit(filename:string);
    { private declarations }
  public
    { public declarations }
  end; 

var
  Form1: TForm1; 

implementation

{ TForm1 }



procedure TForm1.Button1Click(Sender: TObject);
Begin
  if OpenDialog.Execute then
    loadit(opendialog.FileName);
end;


procedure Tform1.loadit(filename:string);
type tptr=array[0..5] of word;
var F:file;
    e0,c8,d4,a4:integer;
    fb,ef:integer;
    i,j:integer;
    results:integer;
    line:string;
begin
  memo1.clear;

  assignfile(F,utf8tosys(filename));
  reset(f,1);
  if IOResult=0 then
  Begin
  e0:=0;
  fb:=0;
  d4:=0;
  seek(F,6);
  blockread(F,e0,2);
  seek(F,10);
  blockread(F,fb,1);
  seek(F,11);
  blockread(F,d4,2);
  seek(F,filepos(F)+2);
  c8:=d4-e0;
  for i:=1 to c8 do
  Begin
    e0:=d4;
    d4:=0;
    line:='';
    blockread(F,d4,2);
    seek(F,Filepos(F)+2);
    a4:=d4-e0;
    for j:=1 to a4 do
    Begin
      blockread(F,ef,1,results);
      line:=line+chr(ef-fb);
      fb:=ef;
    end;
    memo1.Lines.Add(line);
  end;
  closefile(F);
  end;
end;

procedure TForm1.Button2Click(Sender: TObject);
Begin
  if SaveDialog.Execute then
    saveit(savedialog.FileName);
end;

procedure Tform1.saveit(filename:string);
var F:file;
    e0,c8,d4,a4:word;
    w0:word;
    fb,ef:byte;
    i,j:integer;
    results:integer;
    line:string;
begin
  assignfile(F,utf8tosys(filename));
  rewrite(f,1);
  if IOResult=0 then
  Begin
    //Header
    w0:=0;
    blockwrite(F,w0,6);
    e0:=30779;
    fb:=75;
    d4:=e0+memo1.Lines.Count;
    blockwrite(F,e0,2);
    blockwrite(F,w0,2);
    blockwrite(F,fb,1);
    blockwrite(F,d4,2);

    blockwrite(F,w0,2);
    // data
    for i:=1 to memo1.Lines.Count do
    Begin
      line:=memo1.Lines.Strings[i-1];
      a4:=Length(line);
      e0:=d4;
      d4:=a4+e0;
      blockwrite(F,d4,2);
      blockwrite(F,w0,2);
      for j:=1 to a4 do
      Begin
        ef:=ord(line[j])+fb;
        blockwrite(F,ef,1);
        fb:=ef;
      end;
    end;
  closefile(F)
  end;

end;





initialization
  {$I readlist.lrs}

end.

