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

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Window.Type;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class InteraceAbout
{
	private JFrame frmCredits;
	
	private static final String L2JBRASIL = "https://l2jbrasil.com";
	private static final String L2JCENTER = "https://l2jcenter.com";
	
	public InteraceAbout()
	{
		initialize();
		frmCredits.setVisible(true);
	}
	
	private void initialize()
	{
		frmCredits = new JFrame();
		frmCredits.setResizable(false);
		frmCredits.setTitle("Créditos e Parceiros");
		frmCredits.setBounds(100, 100, 400, 330);
		frmCredits.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frmCredits.setType(Type.UTILITY);
		frmCredits.getContentPane().setLayout(null);
		
		JLabel lblTitle = new JLabel("[BR] PROJECT - Créditos");
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 22));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setBounds(10, 10, 364, 40);
		frmCredits.getContentPane().add(lblTitle);
		
		JLabel lblAuthor = new JLabel("Criado por Julio Prado");
		lblAuthor.setHorizontalAlignment(SwingConstants.CENTER);
		lblAuthor.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAuthor.setBounds(10, 55, 364, 20);
		frmCredits.getContentPane().add(lblAuthor);
		
		JLabel lblThanks = new JLabel("Agradecimentos: Victor, Dhousefe, Natan, Kelvin");
		lblThanks.setHorizontalAlignment(SwingConstants.CENTER);
		lblThanks.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblThanks.setBounds(10, 85, 364, 20);
		frmCredits.getContentPane().add(lblThanks);
		
		JLabel lblPartners = new JLabel("Parceiros");
		lblPartners.setHorizontalAlignment(SwingConstants.CENTER);
		lblPartners.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblPartners.setBounds(10, 140, 364, 20);
		frmCredits.getContentPane().add(lblPartners);
		
		JLabel lblL2JBrasil = createLinkLabel("🌐 L2JBrasil.com", L2JBRASIL);
		lblL2JBrasil.setBounds(10, 170, 364, 20);
		frmCredits.getContentPane().add(lblL2JBrasil);
		
		JLabel lblL2JCenter = createLinkLabel("🌐 L2JCenter.com", L2JCENTER);
		lblL2JCenter.setBounds(10, 195, 364, 20);
		frmCredits.getContentPane().add(lblL2JCenter);
		
		JLabel lblFooter = new JLabel("Obrigado por apoiar o projeto!");
		lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
		lblFooter.setFont(new Font("Tahoma", Font.ITALIC, 12));
		lblFooter.setBounds(10, 250, 364, 20);
		frmCredits.getContentPane().add(lblFooter);
		
		frmCredits.setLocationRelativeTo(null);
	}
	
	private static JLabel createLinkLabel(String text, String url)
	{
		JLabel label = new JLabel("<html><font color='#f0c93d'><u>" + text + "</u></font></html>");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (Desktop.isDesktopSupported())
				{
					try
					{
						Desktop.getDesktop().browse(new URI(url));
					}
					catch (IOException | URISyntaxException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});
		return label;
	}
}
