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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.UDADeviceTypeHeader;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Icon;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;
import org.teleal.cling.support.avtransport.callback.GetPositionInfo;
import org.teleal.cling.support.avtransport.callback.Pause;
import org.teleal.cling.support.avtransport.callback.Play;
import org.teleal.cling.support.avtransport.callback.Seek;
import org.teleal.cling.support.avtransport.callback.Stop;
import org.teleal.cling.support.model.PositionInfo;
import org.teleal.cling.support.model.SeekMode;
import org.teleal.cling.support.renderingcontrol.callback.SetMute;
import org.teleal.cling.support.renderingcontrol.callback.SetVolume;

import com.lcdfx.pipoint.PiPoint;
import com.lcdfx.pipoint.PiPointUtils;
import com.lcdfx.pipoint.model.DeviceListItem;
import com.lcdfx.pipoint.renderer.RendererManagerAdapter;

public class DlnaRendererManager extends RendererManagerAdapter implements RegistryListener {
	protected final ImageIcon defaultDeviceIcon = new ImageIcon(this.getClass().getResource("/resources/device.png"));

	private UpnpService upnpService;
	RemoteService avtService;
	RemoteService rcService;
	private ControlPoint controlPoint;
	private Registry registry;
	private Timer positionInfoScheduler;
	
	private TransportSubscription transportSubscription = null;
	private RenderingSubscription renderingSubscription = null;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public DlnaRendererManager(PiPoint piPoint) {
		upnpService = new UpnpServiceImpl();
		controlPoint = upnpService.getControlPoint();
		registry = upnpService.getRegistry();
		registry.addListener(this);
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public void connect(DeviceListItem deviceItem) {
		if (transportSubscription != null) {transportSubscription.end();}
		if (renderingSubscription != null) {renderingSubscription.end();}
		if (positionInfoScheduler != null) {positionInfoScheduler.cancel();}

		Device device = registry.getDevice((UDN) deviceItem.getId(), false);
		
		avtService = (RemoteService) device.findService(new UDAServiceId("AVTransport"));
		transportSubscription = new TransportSubscription(avtService, getRenderer());
		controlPoint.execute(transportSubscription);

		rcService = (RemoteService) device.findService(new UDAServiceId("RenderingControl"));
		renderingSubscription = new RenderingSubscription(rcService, getRenderer());
		controlPoint.execute(renderingSubscription);
		
		logger.log(Level.INFO, "Started subscriptions on [" + device.getDetails().getFriendlyName() + "]");
		
		// get position info callback
		GetPositionInfo getPositionInfoAction = new GetPositionInfo(avtService) {
			@Override
			public void failure(ActionInvocation action, UpnpResponse response, String msg) {
				logger.log(Level.WARNING, "Could not get position info: " + msg);
			}
			@Override
			public void received(ActionInvocation invocation, PositionInfo positionInfo) {
				
				Long trackDuration = positionInfo.getTrackDurationSeconds();
				Long trackElapsed = positionInfo.getTrackElapsedSeconds();
				getRenderer().setTrackDuration(trackDuration);
				getRenderer().setTrackElapsed(trackElapsed);
			}
		};
		
		positionInfoScheduler = new Timer();
		TimerTask getPositionInfo = new PositionInfoTask(controlPoint, getPositionInfoAction);
		positionInfoScheduler.schedule(getPositionInfo, 0, 2000);
		
	}

	@Override
	public void refreshDevices() {
    	UDADeviceType deviceType = new UDADeviceType ("MediaRenderer");
		controlPoint.search(new UDADeviceTypeHeader(deviceType));
	}

	// DeviceManager methods
	@Override
	@SuppressWarnings("rawtypes")
	public void play() {
    	ActionCallback playAction = new Play(avtService) {
			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
				logger.log(Level.SEVERE, "Play failed: " + arg2);
			}
        };
        controlPoint.execute(playAction);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void pause() {
    	ActionCallback pauseAction = new Pause(avtService) {
			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
				logger.log(Level.SEVERE, "Pause failed: " + arg2);
			}
        };
        controlPoint.execute(pauseAction);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void stop() {
    	ActionCallback stopAction = new Stop(avtService) {
			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
				logger.log(Level.SEVERE, "Stop failed: " + arg2);
			}
        };
        controlPoint.execute(stopAction);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void setVolume(long volume) {
   		ActionCallback setVolumeAction = new SetVolume(rcService, volume) {
   			@Override
   			public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
				logger.log(Level.SEVERE, "SetVolume failed: " + arg2);
   			}
   		};
        controlPoint.execute(setVolumeAction);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void toggleMute() {
		getRenderer().setMuted(!getRenderer().isMuted());
   		ActionCallback muteAction = new SetMute(rcService, getRenderer().isMuted()) {
   			@Override
   			public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
				logger.log(Level.SEVERE, "Mute failed: " + arg2);
   			}
   		};
        controlPoint.execute(muteAction);
	}

	@Override
	public void togglePlayPause() {
		play();
	}

	@Override
	public void seekPercent(Double percent) {
		long seconds = Math.round(percent * getRenderer().getTrackDuration());
		seekAbsolute(PiPointUtils.secondsToString(seconds));
	}

	@SuppressWarnings("rawtypes")
	private void seekAbsolute(String time) {
		// one would think ABS_TIME would be proper here; apparently not
   		ActionCallback seekAction = new Seek(avtService, SeekMode.REL_TIME, time) {
   			@Override
   			public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
				logger.log(Level.SEVERE, "Seek failed: " + arg2);
   			}
   		};
        controlPoint.execute(seekAction);
	}

	@Override
	public void shutdown() {
    	upnpService.shutdown();
	}

	// RegistryListener methods
	@Override
	public void remoteDeviceAdded(Registry registry, final RemoteDevice device) {
    	if (device.findDevices(new UDADeviceType("MediaRenderer")).length > 0) {
    		logger.log(Level.INFO, "Adding remote device: " + device.getDetails().getFriendlyName() + "UUID: " + device.getIdentity().getUdn().toString());
    		final DeviceListItem deviceItem = adaptDevice(device);
    		SwingUtilities.invokeLater(new Runnable() {
    			@Override
    			public void run() {
    	    		listModel.addElement(deviceItem);
    			}
    		});
        }
	}

	@Override
	public void remoteDeviceRemoved(Registry registry, final RemoteDevice device) {
		logger.log(Level.INFO, "Removing remote device: " + device.getDetails().getFriendlyName() + "UUID: " + device.getIdentity().getUdn().toString());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				listModel.removeElement(getDeviceItemById(device.getIdentity().getUdn()));
			}
		});
	}

	@Override
	public void beforeShutdown(Registry arg0) {}

	@Override
	public void afterShutdown() {}

	@Override
	public void localDeviceAdded(Registry arg0, LocalDevice arg1) {}

	@Override
	public void localDeviceRemoved(Registry arg0, LocalDevice arg1) {}

	@Override
	public void remoteDeviceDiscoveryFailed(Registry arg0, RemoteDevice arg1, Exception arg2) {}

	@Override
	public void remoteDeviceDiscoveryStarted(Registry arg0, RemoteDevice arg1) {}

	@Override
	public void remoteDeviceUpdated(Registry arg0, RemoteDevice arg1) {}
	
	private DeviceListItem adaptDevice(Device device) {
		DeviceListItem deviceItem = new DeviceListItem();
		deviceItem.setId(device.getIdentity().getUdn());
		deviceItem.setName(device.getDetails().getFriendlyName());
		deviceItem.setDescription(device.getDetails().getModelDetails().getModelName());
		if (device.getIcons() != null && device.getIcons().length > 0) {
			Icon icon = device.getIcons()[0]; 
			URL iconUrl = ((RemoteDevice) device).normalizeURI(icon.getUri());
			try {
				BufferedImage deviceImage = ImageIO.read(iconUrl);
				deviceItem.setIcon(deviceImage);
			} catch (IOException ex) {
				logger.log(Level.SEVERE, "Exception caught scaling renderer icon at [" + iconUrl.getPath() + "]; " + ex.getMessage(), ex);
			}
		} else {
			deviceItem.setIcon(defaultDeviceIcon.getImage());
		}
		
		return deviceItem;
	}

	class PositionInfoTask extends TimerTask {
		final ControlPoint controlPoint;
		final ActionCallback cb;
		
		PositionInfoTask(ControlPoint controlPoint, ActionCallback cb) {
			this.controlPoint = controlPoint;
			this.cb = cb;
		}
		@Override
		public void run() {
			controlPoint.execute(cb);
		}
	}
}
