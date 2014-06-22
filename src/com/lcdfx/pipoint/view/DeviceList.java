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
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;

import com.lcdfx.pipoint.model.DeviceListItem;

public class DeviceList extends JList<DeviceListItem> {
	private static final long serialVersionUID = 1L;
	
	protected static final int DEVICE_ICON_WIDTH = 48;
	protected static final int DEVICE_ICON_HEIGHT = 48;
	
	private Map<Object, ImageIcon> deviceIcons = new HashMap<Object, ImageIcon>();
	

	public DeviceList(final DevicePanel devicePanel) {
		this.setBackground(Color.BLACK);
		this.setForeground(Color.WHITE);
		this.setCellRenderer(new DeviceCellRenderer());
		this.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.addListSelectionListener(devicePanel);
	}

	static class DeviceCellRenderer implements ListCellRenderer<DeviceListItem> {

		@Override
		@SuppressWarnings("rawtypes")
		public Component getListCellRendererComponent(JList list, DeviceListItem device, int index, boolean isSelected, boolean cellHasFocus) {
			JPanel deviceCell = new JPanel(new BorderLayout());
			deviceCell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			deviceCell.setBackground((isSelected) ? Color.DARK_GRAY : Color.BLACK);

			JPanel deviceInfoPanel = new JPanel();
			deviceInfoPanel.setLayout(new GridLayout(0, 1));
			deviceInfoPanel.setOpaque(false);

			Border paddingBorder = BorderFactory.createEmptyBorder(0, 4, 0, 0);
			JLabel friendlyName = new JLabel(device.getName());
			friendlyName.setBorder(paddingBorder);
			friendlyName.setForeground(Color.WHITE);
			friendlyName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
			JLabel name = new JLabel(device.getDescription());
			name.setBorder(paddingBorder);
			name.setForeground(Color.WHITE);
			name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
			deviceInfoPanel.add(friendlyName);
			deviceInfoPanel.add(name);
			
			JLabel deviceIcon = new JLabel(getDeviceIcon(device, (DeviceList) list));
			
			deviceCell.add(deviceIcon, BorderLayout.WEST);
			deviceCell.add(deviceInfoPanel, BorderLayout.CENTER);
			
			return deviceCell;
		}
		
		public ImageIcon getDeviceIcon(DeviceListItem device, DeviceList list) {

			Logger logger = Logger.getLogger(this.getClass().getName());

			if (list.deviceIcons.get(device.getId()) == null) {
				BufferedImage deviceImage = null;
				try {
					deviceImage = ImageIO.read(device.getIcon());
					Image scaledImage = deviceImage.getScaledInstance(DEVICE_ICON_WIDTH, DEVICE_ICON_HEIGHT, Image.SCALE_SMOOTH);
					list.deviceIcons.put(device.getId(), new ImageIcon(scaledImage));
				} catch (Exception ex) {
					logger.log(Level.SEVERE, "Exception caught scaling device icon; " + ex.getMessage(), ex);
				}
			}
			return list.deviceIcons.get(device.getId());
		}
	}	
}
