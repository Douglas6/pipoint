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

package com.lcdfx.pipoint.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.event.SwingPropertyChangeSupport;

public class Renderer {
	
	public static final String STOPPED = "STOPPED";
	public static final String PAUSED = "PAUSED";
	public static final String PLAYING = "PLAYING";
	public static final String CHANGING = "TRANSITIONING";
	
	// transport
	private String transportState = Renderer.PLAYING;
	private long trackDuration = (long) 0;
	private long trackElapsed = (long) 0;
	// now playing
	private NowPlayingItem nowPlayingItem = new NowPlayingItem();
	// volume control
	private long volume;
	private boolean muted;
	
	private PropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

    public void addListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    public void removeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
	public String getTransportState() {
		return transportState;
	}
	public void setTransportState(String transportState) {
		pcs.firePropertyChange("transportState", this.transportState, this.transportState = transportState);
	}
	public void setTrackDuration(long trackDuration) {
		pcs.firePropertyChange("treackDuration", this.trackDuration, this.trackDuration = trackDuration);
	}
	public long getTrackDuration() {
		return trackDuration;
	}
	public void setTrackElapsed(long trackElapsed) {
		Double oldValue = (new Double(this.trackElapsed) / new Double(trackDuration));
		this.trackElapsed = trackElapsed;
		Double newValue = (new Double(this.trackElapsed) / new Double(trackDuration));
		pcs.firePropertyChange("progress", oldValue, newValue);
	}
	public long getTrackElapsed() {
		return trackElapsed;
	}
	public NowPlayingItem getNowPlayingItem() {
		return nowPlayingItem;
	}
	public void setNowPlayingItem(NowPlayingItem nowPlayingItem) {
		pcs.firePropertyChange("nowPlaying", this.nowPlayingItem, this.nowPlayingItem = nowPlayingItem);
	}
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		pcs.firePropertyChange("volume", this.volume, this.volume = volume);
	}
	public boolean isMuted() {
		return muted;
	}
	public void setMuted(boolean muted) {
		pcs.firePropertyChange("mute", this.muted, this.muted = muted);
	}
}
