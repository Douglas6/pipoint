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

import javax.swing.JComponent;


public class ProgressBar extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private static final Color PROGRESS_BAR_COLOR = new Color(0, 170, 255);

	private Double value = 0.0;
	
	public ProgressBar() {
		setOpaque(false);
		setForeground(PROGRESS_BAR_COLOR);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int length = (int) (value * getWidth()); 
		g.setColor(getForeground());
		g.fillRect(0, 0, length, getHeight());
	}

	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
		repaint();
	}

}
