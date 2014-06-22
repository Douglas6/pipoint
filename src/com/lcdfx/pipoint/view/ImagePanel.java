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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

public class ImagePanel extends JPanel  {
	private static final long serialVersionUID = 1L;
	private Image baseImage= null;
	private double aspectRatio = 1.0;
	private Image cachedImage = null;
	
	public ImagePanel() {
		super();
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent ev) {
				final int height = ImagePanel.this.getHeight();
				int w = (int) (height * aspectRatio);
				int h = height;
				cachedImage = scaleImage(baseImage, w, h);
				ImagePanel.this.repaint();
			}
		});
	}
	
	Image scaleImage(Image image, int w, int h) {
		return w > 0 && h > 0 ? baseImage.getScaledInstance(w, h, Image.SCALE_SMOOTH) : baseImage;
	}
	
	public void setImage(Image bkgdImage) {
		this.baseImage = bkgdImage;
		this.aspectRatio = (double) bkgdImage.getWidth(null) / (double) bkgdImage.getHeight(null);
		final int height = ImagePanel.this.getHeight();
		int w = (int) (height * aspectRatio);
		int h = height;
		cachedImage = scaleImage(baseImage, w, h);
	}

	// JPanel methods
	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);
		if (cachedImage != null) {
			int offset = (int) ((g.getClipBounds().width - cachedImage.getWidth(null)) / 2.0);
			g.drawImage(cachedImage, offset, 0, null);
		}
	}
}

