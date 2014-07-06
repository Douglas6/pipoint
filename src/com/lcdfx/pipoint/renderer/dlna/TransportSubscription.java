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

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.teleal.cling.controlpoint.SubscriptionCallback;
import org.teleal.cling.model.gena.CancelReason;
import org.teleal.cling.model.gena.GENASubscription;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.support.avtransport.lastchange.AVTransportVariable;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.lastchange.LastChange;
import org.teleal.cling.support.lastchange.LastChangeParser;
import org.teleal.cling.support.model.DIDLContent;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.PersonWithRole;
import org.teleal.cling.support.model.item.Item;
import org.teleal.cling.support.model.item.MusicTrack;

import com.lcdfx.pipoint.PiPointUtils;
import com.lcdfx.pipoint.model.NowPlayingItem;
import com.lcdfx.pipoint.model.Renderer;
import com.lcdfx.pipoint.renderer.dlna.xml.AVTransportLastChangeParser;

public class TransportSubscription extends SubscriptionCallback {

	final LastChangeParser lastChangeParser = new AVTransportLastChangeParser();
	final DIDLParser didlParser = new DIDLParser();
	final Renderer renderer;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public TransportSubscription(RemoteService service, Renderer renderer) {
		super(service);
		this.renderer = renderer;
	}

	public TransportSubscription(RemoteService service, int requestedDurationSeconds, Renderer renderer) {
		super(service, requestedDurationSeconds);
		this.renderer = renderer;
	}
	
	// SubscriptionCallback methods
	@Override
	@SuppressWarnings("rawtypes")
	public void established(GENASubscription subscription) {
		logger.log(Level.INFO, "Transport subscription established");
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public void failed(GENASubscription subscription, UpnpResponse response, Exception ex) {
		logger.log(Level.SEVERE, createDefaultFailureMessage(response, ex));
	}
	@Override
	@SuppressWarnings("rawtypes")
	public void ended(GENASubscription subscription, CancelReason reason, UpnpResponse response) {
		logger.log(Level.INFO, "Transport subscription ended");
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void eventReceived(GENASubscription subscription) {

		String xml = subscription.getCurrentValues().get("LastChange").toString();
		try {
			LastChange lastChange = new LastChange(lastChangeParser, xml);
			logger.log(Level.FINE, xml);
			AVTransportVariable.CurrentTrackMetaData currentTrackMetaDataVariable = 
					lastChange.getEventedValue(0, AVTransportVariable.CurrentTrackMetaData.class);

			// parse current track metadata
			if  (currentTrackMetaDataVariable != null 
					&& currentTrackMetaDataVariable.getValue() != null) {
				String currentTrackXml = currentTrackMetaDataVariable.getValue();
				if (!currentTrackXml.equals("NOT_IMPLEMENTED")) {
	//				currentTrackXml = currentTrackXml.replaceAll("%22", "\""); // Gmediarenderer escapes quotes
					DIDLContent currentTrackMetaDataContent = didlParser.parse(currentTrackXml);
					Item item = currentTrackMetaDataContent.getItems().get(0);
					
					// create and populate NowPlayingItem
					NowPlayingItem nowPlayingItem = new NowPlayingItem();
					nowPlayingItem.setTitle(item.getTitle());
					PersonWithRole artist = item.getFirstPropertyValue(DIDLObject.Property.UPNP.ARTIST.class);
					if (artist != null) {nowPlayingItem.setArtist(artist.getName());}
					if (item instanceof MusicTrack) {
						MusicTrack musicTrackItem = (MusicTrack) item; 
						if (musicTrackItem != null) {nowPlayingItem.setAlbum(musicTrackItem.getAlbum());}
					}
					URI coverArtUri = item.getFirstPropertyValue(DIDLObject.Property.UPNP.ALBUM_ART_URI.class);
					if (coverArtUri != null) {nowPlayingItem.setCoverArt(coverArtUri.toString());}
					
					renderer.setNowPlayingItem(nowPlayingItem);
				}
			}
			// get track duration
			AVTransportVariable.CurrentTrackDuration currentTrackDurationVariable = 
					lastChange.getEventedValue(0, AVTransportVariable.CurrentTrackDuration.class);
			if (currentTrackDurationVariable != null) {
				String duration = currentTrackDurationVariable.getValue();
				long seconds = PiPointUtils.stringToSeconds(duration);
				renderer.setTrackDuration(seconds);
			}

			// handle transport state
			AVTransportVariable.TransportState transportStateVariable = 
					lastChange.getEventedValue(0, AVTransportVariable.TransportState.class);
			if (transportStateVariable != null) {
				renderer.setTransportState(transportStateVariable.getValue().getValue());
			}
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Exception caught parsing AV transport last change: ", ex);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void eventsMissed(GENASubscription sub, int numberOfMissedEvents) {
		logger.log(Level.WARNING, "Missed events: " + numberOfMissedEvents);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	protected void failed(GENASubscription sub, UpnpResponse response, Exception ex, String arg3) {
		logger.log(Level.SEVERE, createDefaultFailureMessage(response, ex));
	}
	
}
