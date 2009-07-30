unit mainwindow;

{$mode objfpc}{$H+}

interface

uses
  Classes, SysUtils, FileUtil, LResources, Forms, Controls, Graphics, Dialogs,
  StdCtrls, process, configwindow;

type

  { TForm1 }

  TForm1 = class(TForm)
    Button1: TButton;
    Aprocess: TProcess;
    configbtn: TButton;
    Memo1: TMemo;
    procedure Button1Click(Sender: TObject);
    procedure configbtnClick(Sender: TObject);
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
begin
  Aprocess.Active:=true;
  form1.Close;
end;

procedure TForm1.configbtnClick(Sender: TObject);
begin
  form2.loadprefs();
end;

initialization
  {$I mainwindow.lrs}

end.

