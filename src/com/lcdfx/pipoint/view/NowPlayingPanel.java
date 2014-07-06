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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.lcdfx.pipoint.PiPoint;
import com.lcdfx.pipoint.model.NowPlayingItem;
import com.lcdfx.pipoint.model.Renderer;

public class NowPlayingPanel extends JPanel 
		implements PropertyChangeListener, MouseListener {
	private static final long serialVersionUID = 1L;
	
	private static final Color TRANSLUCENT_GRAY = 
			new Color(new Float(0.1), new Float(0.1), new Float(0.1), new Float(0.6)); 

	protected static final String NO_TITLE = "No title";
	protected static final String NO_ARTIST = "No artist";
	protected static final String NO_ALBUM = "No album";

	protected final ImageIcon listIcon = new ImageIcon(this.getClass().getResource("/resources/list.png"));
	protected final ImageIcon pauseIcon = new ImageIcon(this.getClass().getResource("/resources/pause.png"));
	protected final ImageIcon playIcon = new ImageIcon(this.getClass().getResource("/resources/play.png"));
	protected final ImageIcon stopIcon = new ImageIcon(this.getClass().getResource("/resources/stop.png"));
	protected final ImageIcon muteIcon = new ImageIcon(this.getClass().getResource("/resources/muted.png"));
	protected final ImageIcon unmuteIcon = new ImageIcon(this.getClass().getResource("/resources/unmuted.png"));
	protected final ImageIcon volumeIcon = new ImageIcon(this.getClass().getResource("/resources/volume.png"));
	protected final ImageIcon seekIcon = new ImageIcon(this.getClass().getResource("/resources/seek.png"));
	
	final PiPoint piPoint;
	
	BufferedImage defaultArt;
	Image coverArt;
	final JPanel controlPanel;
	final JPanel infoPanel;
	final ImagePanel coverArtPanel;
	final JPanel buttonsPanel;

	final MenuButton pausePlayButton;
	final MenuButton muteButton;

	final ProgressBar progressBar;
	final Slider volumeSlider;
	final Slider seekSlider;
	final JLabel titleLabel;
	final JLabel artistLabel;
	final JLabel albumLabel;
	
	public NowPlayingPanel(final PiPoint piPoint) {
		super(new BorderLayout());
		this.piPoint = piPoint;

		//
        // control panel
		//
        volumeSlider = new Slider(volumeIcon);
        volumeSlider.setBackground(TRANSLUCENT_GRAY);
        volumeSlider.setPreferredSize(new Dimension(Integer.MAX_VALUE, 32));
        volumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ev) {
				Double value = ((Slider) ev.getSource()).getValue();
				long volume = Math.round(value * 100.0);
           		piPoint.getManager().setVolume(volume);
			}
		});
        seekSlider = new Slider(seekIcon);
        seekSlider.setBackground(TRANSLUCENT_GRAY);
        seekSlider.setPreferredSize(new Dimension(Integer.MAX_VALUE, 32));
        seekSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ev) {
				Double value = ((Slider) ev.getSource()).getValue();
           		piPoint.getManager().seekPercent(value);
			}
		});
        
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setOpaque(false);
        controlPanel.setVisible(false);

        controlPanel.add(volumeSlider);
        controlPanel.add(seekSlider);

		//
        // info panel
        //
        titleLabel = new JLabel(NO_TITLE);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        artistLabel = new JLabel(NO_ARTIST); 
        artistLabel.setForeground(Color.WHITE);
        albumLabel = new JLabel(NO_ALBUM);
        albumLabel.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
        textPanel.setBackground(TRANSLUCENT_GRAY);
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 6, 4, 0));
        
        textPanel.add(titleLabel);
        textPanel.add(artistLabel);
        textPanel.add(albumLabel);
        
        progressBar = new ProgressBar();
        progressBar.setBackground(TRANSLUCENT_GRAY);
        progressBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 4));

        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.addMouseListener(this);

        infoPanel.add(textPanel, BorderLayout.CENTER);
        infoPanel.add(progressBar, BorderLayout.SOUTH);

		//
        // cover art panel
        //
		try {
			defaultArt = ImageIO.read(NowPlayingPanel.class.getResource("/resources/nowplaying_bkgd.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		coverArtPanel = new ImagePanel();
		coverArtPanel.setLayout(new BorderLayout());
		coverArtPanel.setBackground(Color.BLACK);
		coverArtPanel.setImage(defaultArt);
		
        coverArtPanel.add(controlPanel, BorderLayout.NORTH);
        coverArtPanel.add(infoPanel, BorderLayout.SOUTH);

        //
        // button panel
        //
		MenuButton devicesButton = new MenuButton(listIcon);
        devicesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	piPoint.showDevices();
            }
        });
        ImageIcon exitIcon = new ImageIcon(this.getClass().getResource("/resources/exit.png"));
        MenuButton exitButton = new MenuButton(exitIcon);
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	piPoint.shutDown();
            }
        });
        pausePlayButton = new MenuButton(pauseIcon);
        pausePlayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	if (piPoint.getManager().getRenderer().getTransportState().equals(Renderer.PLAYING)) {
            		piPoint.getManager().pause();
            	} else {
            		piPoint.getManager().play();
            	}
            }
        });
        muteButton = new MenuButton(muteIcon);
        muteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
           		piPoint.getManager().toggleMute();
            }
        });
        MenuButton stopButton = new MenuButton(stopIcon);
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
            	piPoint.getManager().stop();
            }
        });
        
		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridLayout(0, 1, 0, 0));
		buttonsPanel.setBackground(Color.BLACK);
		buttonsPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        buttonsPanel.add(devicesButton);
        buttonsPanel.add(stopButton);
        buttonsPanel.add(pausePlayButton);
        buttonsPanel.add(muteButton);
        buttonsPanel.add(exitButton);

        //
        // now playing panel
        //
        this.add(coverArtPanel, BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.EAST);
	}
		
	public void updateNowPlaying(NowPlayingItem item) {
		if (item.getCoverArt() != null) {
			try {
				Image coverArt = ImageIO.read(new URL(item.getCoverArt()));
				coverArtPanel.setImage(coverArt);
			} catch (Exception ex) {
				coverArtPanel.setImage(defaultArt);
			}
		} else {
			coverArtPanel.setImage(defaultArt);
		}
		titleLabel.setText(item.getTitle());
		artistLabel.setText(item.getArtist());
		albumLabel.setText(item.getAlbum());
		coverArtPanel.repaint();
	}

	public void resetNowPlaying() {
		titleLabel.setText(NO_TITLE);
		artistLabel.setText(NO_ARTIST);
		albumLabel.setText(NO_ALBUM);
		coverArtPanel.setImage(defaultArt);
		progressBar.setValue(0.0);
		coverArtPanel.repaint();
	}

	@Override
	public void propertyChange(final PropertyChangeEvent ev) {
		if (ev.getPropertyName().equals("volume")) {
			Long volume = (Long) ev.getNewValue();
			volumeSlider.setValue(new Double(volume / 100.0));
		}
		if (ev.getPropertyName().equals("mute")) {
			if ((Boolean) ev.getNewValue()) {
				muteButton.setIcon(unmuteIcon);
			} else {
				muteButton.setIcon(muteIcon);
			}
		}
		if (ev.getPropertyName().equals("nowPlaying")) {
			final NowPlayingItem item = (NowPlayingItem) ev.getNewValue();  
			updateNowPlaying(item);
		}
		if (ev.getPropertyName().equals("transportState")) {
			if (ev.getNewValue().equals("PLAYING")) {
				pausePlayButton.setIcon(pauseIcon);
			} else if (ev.getNewValue().equals("STOPPED") || ev.getNewValue().equals("PAUSED_PLAYBACK")) {
				pausePlayButton.setIcon(playIcon);
			}
		}
		if (ev.getPropertyName().equals("progress")) {
			final Double progress = (Double) ev.getNewValue();
			progressBar.setValue(progress);
			seekSlider.setValue(progress);
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		controlPanel.setVisible(!controlPanel.isShowing());
	}
}

