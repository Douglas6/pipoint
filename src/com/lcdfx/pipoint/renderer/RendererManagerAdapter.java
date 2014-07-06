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

package com.lcdfx.pipoint.renderer;

import javax.swing.DefaultListModel;

import com.lcdfx.pipoint.model.DeviceListItem;
import com.lcdfx.pipoint.model.Renderer;

public abstract class RendererManagerAdapter implements RendererManager {
	
	protected DefaultListModel<DeviceListItem> listModel = new DefaultListModel<DeviceListItem>();
	
	private Renderer renderer = new Renderer();;
	
	protected boolean isConnected;
	
	@Override
	abstract public void connect(DeviceListItem device);

	@Override
	abstract public void shutdown();

	@Override
	public void refreshDevices() {}

	@Override
	abstract public void play();

	@Override
	abstract public void pause();

	@Override
	abstract public void stop();

	@Override
	public void setVolume(long volume) {};
	
	@Override
	public void toggleMute() {};
	
	@Override
	public void seekPercent(Double percent) {};
	
	@Override
	public DefaultListModel<DeviceListItem> getListModel() {
		return listModel;
	}
	
	@Override
	public DeviceListItem getDeviceItemById(Object id) {
		for (int i=0; i<this.listModel.getSize(); i++) {
			if (id.equals(this.listModel.elementAt(i).getId())) {return this.listModel.elementAt(i);} 
		}
		return null;
	}
	
	@Override
	public Renderer getRenderer() {
		return renderer;
	}

	public boolean isConnected() {
		return isConnected;
	}
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
}
