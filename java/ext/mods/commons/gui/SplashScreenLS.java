/*
* Copyleft © 2024-2026 L2Brproject
* * This file is part of L2Brproject derived from aCis409/RusaCis3.8
* * L2Brproject is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by the
* Free Software Foundation, either version 3 of the License.
* * L2Brproject is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.
* * You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
* Our main Developers, Dhousefe-L2JBR, Agazes33, Ban-L2jDev, Warman, SrEli.
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.commons.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;

/**
 * A simple splash screen displayed in a JWindow.
 */
public class SplashScreenLS extends JWindow {
	
	private static final long serialVersionUID = 1L;
	private final int duration;

	/**
	 * Creates a splash screen.
	 * @param imagePath Path to the splash image.
	 * @param frame The main JFrame to show after the splash.
	 * @param duration Duration to display the splash screen in milliseconds.
	 */
	public SplashScreenLS(String imagePath, JFrame frame, int duration) {
		this.duration = duration;

		JLabel splashLabel = new JLabel(new ImageIcon(imagePath));
		getContentPane().add(splashLabel, BorderLayout.CENTER);
		pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension windowSize = getSize();
		int x = (screenSize.width - windowSize.width) / 2;
		int y = (screenSize.height - windowSize.height) / 2;
		setLocation(x, y);

		setAlwaysOnTop(true);
		
		setVisible(true);

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				setVisible(false);
				if (frame != null) {
					frame.setVisible(true);
					frame.toFront();
					frame.setState(Frame.NORMAL);
				}
				dispose();
			}
		}, duration);
	}
	
	/**
	 * Overloaded constructor with default duration (1500ms).
	 * @param imagePath Path to the splash image.
	 * @param frame The main JFrame to show after the splash.
	 */
	 public SplashScreenLS(String imagePath, JFrame frame) {
	 	this(imagePath, frame, 1500);
	 }
}