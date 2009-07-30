unit mainwindow;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils, FileUtil, LResources, Forms, Controls, Graphics, Dialogs,
  StdCtrls, process, Pipes;

type

  { TForm1 }

  TForm1 = class(TForm)
    Button1: TButton;
    Aprocess: TProcess;
    Memo1: TMemo;
    procedure Button1Click(Sender: TObject);
  private
//    TOutput:TinputPipeStream;
    { private declarations }
  public
    { public declarations }
  end; 

var
  Form1: TForm1; 

implementation

{ TForm1 }

procedure TForm1.Button1Click(Sender: TObject);
var a:integer;
begin
  //Aprocess.Output.read(a,1);
  Aprocess.Active:=true;
  form1.Close;
end;

initialization
  {$I mainwindow.lrs}

end.

