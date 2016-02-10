# Introduction #

I've been getting a lot of questions about how to get the server running so i decided to make a QuickStart guide, with some pointer to get you going. I will also be updating a [FAQ](FAQ.md) as the questions come in.

# Set up enviroment #

First off you want to install a svn client, for windows users i can recommend [TortoiseSVN](http://tortoisesvn.tigris.org)

After you got your svn client you want to check out
```
http://reunion.googlecode.com/svn/trunk/
```
Refer to the documentation of your svn client if you are having trouble with this.

Since this project is still under heavy development we dont provide anything in compiled form, so you will have to download the [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
as well as [Eclipse IDE](http://eclipse.org)

Once you got both of those installed you will want to import the jreunion jlauncher and jcommon projects from the /trunk directory that you had earlier checked out using svn. Refer to the eclipse documentation as to how you import projects.