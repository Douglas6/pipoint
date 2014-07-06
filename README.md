PiPoint version 0.2 ALPHA
=========================
Overview
--------
PiPoint is a simple DLNA control point intended for use on a Raspberry Pi(Tm) with a small touchscreen. It presents a list of DLNA renderers on the network for selection, and displays title, artist, album and cover art for the currently playing track. Control of the renderer is limited to play/pause, stop (next), mute, and volume and seek functions.
PiPoint is written in Java and uses the open source Cling UPnP stack by Christian Bauer (http://4thline.org/projects/cling/). PiPoint is free and open source, distributed under the MIT license.

This is a very simple control point implementation. It cannot be used to browse library content, nor to create and control playlists. So you will need another control point or other method of running a playlist. I use BubbleUPnP on my Android phone, it works very well to create and play playlists. Or you can use XBMC's playlists. The XBMC Pandora add-on also works very well; you can use PiPoint to view, pause, and skip tracks.

The source code for PiPoint is available on my GitHub site at https://github.com/Douglas6/pipoint.git

Requirements
------------

* Pipoint requires the Java JRE (already included in the standard Raspian distribution)
* Because it is written in Java/Swing, you will need to run PiPoint in a Desktop Environment, such as LXDE on Raspian.
* You'll also want another, more capable, control point such as BubbleUPnP for controlling playlists.

Quick Start
-----------
Open an LXTerminal window to enter the following commands. 

Step 1. Download the Pipoint zip file to your home directory. These instructions will assume that is "/home/pi"

    wget https://github.com/Douglas6/pipoint/releases/download/v0.2.0/pipoint-v0.1.0.zip

Step 2. Unzip the archive (the file name will vary depending on the version you have downloaded).

    unzip pipoint_0.2.0.zip

Step 3. Change to the pipoint directory

    cd pipoint

Step 4. Make the script executable.

    chmod +x pipoint.sh

If you have installed PiPoint in a directory other than "/home/pi/pipoint", you will need to edit the script and modify the EXEC location to the proper path.

Step 5. Run the application.

    ,/pipoint.sh
    
Pipoint will open in a new window. You may minimize your LXTerminal now, but don't close it.

PiPoint will display a list of DLNA renderers (music players) that it finds on your network. If you have many, you can scroll the list using the up and down buttons. Of course you can exit the application at any time by clicking the power button at the bottom right.

Select a renderer by clicking on it. If the renderer you want is already selected, you'll need to click the the 'Now Playing' button at the top right of the window. In either case the Now Playing screen will appear.

From the Now Playing screen, you can play or pause the track by clicking the play/pause button. Use the mute button to mute/unmute the volume. The stop/next button will stop the track; if you are using BubbleUPnP or XBMC, this will also start the next song in the playlist. Use the 'Renderers' button at the top right to return to the list of renderers.
Tap the bottom of the cover art panel to toggle the control panel. Use ths siders to adjust volume or playback position. Tap the bottom of the cover art again to close the control panel. 

That's it!

Issues
------

I have done some testing with Gmediarenderer and XBMC (Eden, running on a Windows 7 machine). Reports of your experiences with other renderers will be appreciated (especially if they are positive!)

There seems to be an issue with Gstreamer 0.10 that causes Gmediarenderer to hang after a time. I have reason to believe that upgrading to 1.0.8 or above will fix the problem, but have not had a chance to try.





    