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

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

/**
 * A DocumentListener that limits the number of lines allowed in a Document.
 * Lines can be removed from the start or end of the document as new lines are added.
 */
public class InterfaceLimit implements DocumentListener {
	
	private int maximumLines;
	private final boolean removeFromStart;

	/**
	 * Specify the number of lines to be stored in the Document.
	 * Extra lines will be removed from the start of the Document.
	 * @param maximumLines The maximum number of lines allowed. Must be > 0.
	 */
	public InterfaceLimit(int maximumLines) {
		this(maximumLines, true);
	}

	/**
	 * Specify the number of lines to be stored in the Document.
	 * @param maximumLines The maximum number of lines allowed. Must be > 0.
	 * @param removeFromStart true to remove lines from the start, false to remove from the end.
	 */
	public InterfaceLimit(int maximumLines, boolean removeFromStart) {
		setLimitLines(maximumLines);
		this.removeFromStart = removeFromStart;
	}

	/**
	 * Return the maximum number of lines to be stored in the Document.
	 * @return the maximum lines limit.
	 */
	public int getLimitLines() {
		return maximumLines;
	}

	/**
	 * Set the maximum number of lines to be stored in the Document.
	 * @param maximumLines The maximum number of lines allowed. Must be > 0.
	 */
	public void setLimitLines(int maximumLines) {
		if (maximumLines < 1) {
			throw new IllegalArgumentException("Maximum lines must be greater than 0");
		}
		this.maximumLines = maximumLines;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		SwingUtilities.invokeLater(() -> removeLines(e));
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	/**
	 * Remove lines from the Document when necessary.
	 */
	private void removeLines(DocumentEvent e) {
		Document document = e.getDocument();
		Element root = document.getDefaultRootElement();

		while (root.getElementCount() > maximumLines) {
			if (removeFromStart) {
				removeFromStart(document, root);
			} else {
				removeFromEnd(document, root);
			}
		}
	}

	/**
	 * Remove lines from the start of the Document.
	 */
	private static void removeFromStart(Document document, Element root) {
		Element line = root.getElement(0);
		int end = line.getEndOffset();

		try {
			document.remove(0, end);
		} catch (BadLocationException ble) {
			System.err.println("Error removing lines from document start: " + ble);
		}
	}

	/**
	 * Remove lines from the end of the Document.
	 */
	private static void removeFromEnd(Document document, Element root) {

		Element lastLine = root.getElement(root.getElementCount() - 1);
		Element prevLine = root.getElement(root.getElementCount() - 2);

		int start = prevLine.getStartOffset();
		int end = lastLine.getEndOffset();

		try {
			document.remove(start, end - start);
		} catch (BadLocationException ble) {
			System.err.println("Error removing lines from document end: " + ble);
		}
	}
}