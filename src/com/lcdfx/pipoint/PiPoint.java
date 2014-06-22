/*
 * Copyright (c) 2014 Douglas Otwell
 *  
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.lcdfx.pipoint;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.lcdfx.pipoint.renderer.RendererManager;
import com.lcdfx.pipoint.renderer.dlna.DlnaRendererManager;
import com.lcdfx.pipoint.view.DevicePanel;
import com.lcdfx.pipoint.view.NowPlayingPanel;

public class PiPoint extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private static final String APPLICATION_NAME = "PiPoint";
	private static final int DISPLAY_WIDTH = 320;
	private static final int DISPLAY_HEIGHT = 240;

	private RendererManager mgr;
	
	private final DevicePanel devicePanel;
	private final NowPlayingPanel nowPlayingPanel;
	
	private Logger logger;
	
	public PiPoint(String [] args) {
		
    	this.addWindowListener(new WindowAdapter() {
    		@Override
    		public void windowClosing(WindowEvent ev) {shutDown();}
    	});

		// add logging
    	Logger logger = Logger.getLogger(this.getClass().getName());
		logger.log(Level.INFO, "PiPoint version " + PiPoint.class.getPackage().getImplementationVersion() + " running under " + System.getProperty("java.vm.name") + " v" + System.getProperty("java.vm.version"));
    	
		// get command line options
		CommandLineParser parser = new BasicParser();
		Map<String, String> cmdOptions = new HashMap<String, String>();
		Options options = new Options();
		options.addOption(new Option("f", "fullscreen", false, "fullscreen mode (no cursor)"));
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			for (Option option : cmd.getOptions()) {
				cmdOptions.put(option.getOpt(), option.getValue());
			}
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar pipoint.jar", options);
			System.exit(0);
		}
		
    	if (cmd.hasOption("f")) {
	    	setUndecorated(true);
	    	BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	    	Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
	    	getContentPane().setCursor(blankCursor);
    	}

    	// instantiate the RendererManager
    	mgr = new DlnaRendererManager(this);
	    mgr.refreshDevices();

	    nowPlayingPanel = new NowPlayingPanel(this);
		mgr.getRenderer().addListener(nowPlayingPanel);
	    devicePanel = new DevicePanel(this);

	    this.getContentPane().setPreferredSize(new Dimension(DISPLAY_WIDTH, DISPLAY_HEIGHT));
	    this.getContentPane().add(devicePanel);
	}
	
    public void showDevices() {
    	this.getContentPane().removeAll();
    	this.getContentPane().add(devicePanel);
    	revalidate();
    	repaint();
    }
    
    public void showNowPlaying() {
    	this.getContentPane().removeAll();
    	this.getContentPane().add(nowPlayingPanel);
    	revalidate();
    	repaint();
    }
    
    public void resetNowPlaying() {
		nowPlayingPanel.resetNowPlaying();
		showNowPlaying();
    }
    
    public void shutDown() {
    	mgr.shutdown();
        System.exit(0);
    }

	public RendererManager getManager() {
		return mgr;
	}
	
    public static void main(final String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	JFrame frame = new PiPoint(args);
            	frame.setTitle(APPLICATION_NAME);
            	frame.setIconImage(new ImageIcon(PiPoint.class.getResource("/resources/pipoint_icon.png")).getImage());
            	frame.pack();
            	frame.setVisible(true);
            }
        });
    }

}
