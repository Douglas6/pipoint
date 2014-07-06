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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Slider extends JComponent implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;

	private static final int THUMB_SIZE = 14;
	private static final int THUMB_OFFSET = THUMB_SIZE / 2;
	private static final int ICON_SIZE = 32;

	private Double value = 0.0;
	private ImageIcon icon; 
	private boolean valueIsAdjusting = false;
	private List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

	public Slider(ImageIcon icon) {
		this.icon = icon;
		setForeground(new Color(0, 170, 255));
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	private void updateOnMouseEvent(MouseEvent ev) {
		double x = ev.getX();
		double w = getWidth() - (ICON_SIZE + THUMB_OFFSET);
		x = Math.max(x, 32);
		x = Math.min(x, w + (ICON_SIZE + THUMB_OFFSET));
		setValue((x - 32) / w);
	}

	void fireChangeEvent() {
		for(ChangeListener listener : changeListeners) {
			listener.stateChanged(new ChangeEvent(this));
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.drawImage(icon.getImage(), 0, 0, null);
		int w = getWidth() - (ICON_SIZE + THUMB_OFFSET);
		int x = (int) (value * w);
		g2.setColor(Color.GRAY);
//		g2.fillRect(THUMB_OFFSET, (getHeight() / 2) - 1, w, 2);
		g2.fillRect(ICON_SIZE, (getHeight() / 2) - 1, w, 2);

		g2.setColor(getForeground());
		g2.fillOval(x + (ICON_SIZE - THUMB_OFFSET), (getHeight() / 2) - THUMB_OFFSET, THUMB_SIZE, THUMB_SIZE);
	}

	// MouseListener methods 
	@Override
	public void mouseClicked(MouseEvent ev) {}

	@Override
	public void mousePressed(MouseEvent ev) {
		valueIsAdjusting = true;
		updateOnMouseEvent(ev);
	}

	@Override
	public void mouseReleased(MouseEvent ev) {
		valueIsAdjusting = false;
		fireChangeEvent();
	}

	@Override
	public void mouseEntered(MouseEvent ev) {}

	@Override
	public void mouseExited(MouseEvent ev) {}

	// MouseMotionListener methods 
	@Override
	public void mouseDragged(MouseEvent ev) {
		updateOnMouseEvent(ev);
	}

	@Override
	public void mouseMoved(MouseEvent ev) {}

	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		value = Math.max(value, 0.0);
		value = Math.min(value, 1.0);
		this.value = value;
		repaint();
	}
	public boolean getValueIsAdjusting() {
		return valueIsAdjusting;
	}
	public void setValueIsAdjusting(boolean valueIsAdjusting) {
		this.valueIsAdjusting = valueIsAdjusting;
	}
	public void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}
	public void removeChangeListener(ChangeListener listener) {
		changeListeners.remove(listener);
	}
}
