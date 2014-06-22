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

package com.lcdfx.pipoint.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.lcdfx.pipoint.PiPoint;
import com.lcdfx.pipoint.model.DeviceListItem;

public class DevicePanel extends JPanel implements ListSelectionListener {
	private static final long serialVersionUID = 1L;
	
	final private PiPoint piPoint;
	final private JScrollPane scrollPane;
	final private DeviceList devices;
	
	public DevicePanel(final PiPoint piPoint) {
		super(new BorderLayout());
		this.piPoint = piPoint;
		
        devices = new DeviceList(this);
        devices.setModel(piPoint.getManager().getListModel());
        
	    scrollPane = new JScrollPane(devices, 
	    		JScrollPane.VERTICAL_SCROLLBAR_NEVER, 
	    		JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    scrollPane.setBorder(null);
	    
		// create button panel and buttons
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(0, 1, 0, 0));
		buttonsPanel.setBackground(Color.BLACK);
		buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        ImageIcon upIcon = new ImageIcon(this.getClass().getResource("/resources/up.png"));
		MenuButton upButton = new MenuButton(upIcon);
        upButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	scrollUp();
            }
        });      

        ImageIcon downIcon = new ImageIcon(this.getClass().getResource("/resources/down.png"));
        MenuButton downButton = new MenuButton(downIcon);
        downButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	scrollDown();
            }
        });
        
        ImageIcon refreshIcon = new ImageIcon(this.getClass().getResource("/resources/refresh.png"));
        MenuButton refreshButton = new MenuButton(refreshIcon);
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	piPoint.getManager().refreshDevices();
            }
        });
        
        ImageIcon nowPlayingIcon = new ImageIcon(this.getClass().getResource("/resources/nowplaying.png"));
        MenuButton nowPlayingButton = new MenuButton(nowPlayingIcon);
        nowPlayingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	piPoint.showNowPlaying();
            }
        });
        
        ImageIcon exitIcon = new ImageIcon(this.getClass().getResource("/resources/exit.png"));
        MenuButton exitButton = new MenuButton(exitIcon);
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	piPoint.shutDown();
            }
        });

        buttonsPanel.add(nowPlayingButton);
        buttonsPanel.add(upButton);
        buttonsPanel.add(downButton);
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(exitButton);

        this.add(scrollPane, BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.EAST);
	}
	
    public void scrollUp() {
    	int y = (int) scrollPane.getViewport().getViewPosition().getY();
    	y -= scrollPane.getViewport().getHeight();
    	y = (y < 0) ? 0 : y;
    	scrollPane.getViewport().setViewPosition(new Point(0, y));
    }
    
    public void scrollDown() {
    	int y = (int) scrollPane.getViewport().getViewPosition().getY();
    	y += scrollPane.getViewport().getHeight();
    	if (y < devices.getHeight()) {
    		scrollPane.getViewport().setViewPosition(new Point(0, y));
    	}
    }
    
	// ListSelectionListener methods
	@Override
	public void valueChanged(ListSelectionEvent ev) {
		if (!ev.getValueIsAdjusting()) {
			DeviceList selectionModel = (DeviceList) ev.getSource();
			DeviceListItem device = selectionModel.getSelectedValue();
			if (device != null) {
				piPoint.getManager().connect(device);
				piPoint.resetNowPlaying();
				piPoint.showNowPlaying();
			} else {
				piPoint.showDevices();
			}
		}
	}
}
