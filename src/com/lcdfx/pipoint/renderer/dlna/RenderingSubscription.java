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

package com.lcdfx.pipoint.renderer.dlna;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.teleal.cling.controlpoint.SubscriptionCallback;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.lastchange.LastChange;
import org.teleal.cling.support.lastchange.LastChangeParser;
import org.teleal.cling.support.renderingcontrol.lastchange.ChannelMute;
import org.teleal.cling.support.renderingcontrol.lastchange.ChannelVolume;
import org.teleal.cling.support.renderingcontrol.lastchange.RenderingControlVariable;
import org.teleal.cling.support.renderingcontrol.lastchange.RenderingControlVariable.Mute;
import org.teleal.cling.support.renderingcontrol.lastchange.RenderingControlVariable.Volume;

import com.lcdfx.pipoint.model.Renderer;
import com.lcdfx.pipoint.renderer.dlna.xml.RenderingControlLastChangeParser;

public class RenderingSubscription extends SubscriptionCallback {

	final Renderer renderer;
	final LastChangeParser lastChangeParser = new RenderingControlLastChangeParser();
	final DIDLParser didlParser = new DIDLParser();
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	public RenderingSubscription(RemoteService service, Renderer renderer) {
		super(service);
		this.renderer = renderer;
	}

	public RenderingSubscription(RemoteService service, int requestedDurationSeconds, Renderer renderer) {
		super(service, requestedDurationSeconds);
		this.renderer = renderer;
	}
	
	// SubscriptionCallback methods
	@Override
	@SuppressWarnings("rawtypes")
	public void established(GENASubscription subscription) {
		logger.log(Level.INFO, "Rendering subscription established");
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public void failed(GENASubscription subscription, UpnpResponse response, Exception ex) {
		logger.log(Level.SEVERE, createDefaultFailureMessage(response, ex));
	}
	@Override
	@SuppressWarnings("rawtypes")
	public void ended(GENASubscription subscription, CancelReason reason, UpnpResponse response) {
		logger.log(Level.INFO, "Rendering subscription ended");
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void eventReceived(GENASubscription subscription) {
		String lastChangeString = subscription.getCurrentValues().get("LastChange").toString();
		lastChangeString = lastChangeString.replaceAll("Channel", "channel");  // XBMC capitalizes 'channel' which breaks Cling
		logger.log(Level.FINE, lastChangeString);
		try {
			LastChange lastChange = new LastChange(lastChangeParser, lastChangeString);

			Mute mute = lastChange.getEventedValue(0, RenderingControlVariable.Mute.class);
			if (mute != null && mute.getValue() != null) {
				ChannelMute channelMute = mute.getValue();
				this.renderer.setMuted(channelMute.getMute());
			}
			Volume volume = lastChange.getEventedValue(0, RenderingControlVariable.Volume.class);
			if (volume != null && volume.getValue() != null) {
				ChannelVolume channelVolume = volume.getValue();
				this.renderer.setVolume(channelVolume.getVolume());
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Exception caught parsing rendering control last change: ", ex);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
		logger.log(Level.WARNING, "Missed rendering events: " + numberOfMissedEvents);
	}
	@Override
	@SuppressWarnings("rawtypes")
	protected void failed(GENASubscription sub, UpnpResponse response, Exception ex, String arg3) {
		System.err.println(createDefaultFailureMessage(response, ex));
	}

}
