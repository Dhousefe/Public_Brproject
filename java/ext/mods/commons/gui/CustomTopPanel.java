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
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, 
* SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo, Tiagorosendo, Schuster, LucasStark, damedd
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.commons.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Um painel reutilizável que combina uma barra de título personalizada 
 * (com ícone, título, botões de minimizar, maximizar/restaurar e fechar)
 * e uma JMenuBar.
 * <p>
 * A lógica de arrastar, maximizar e minimizar é tratada internamente.
 * A ação de fechar é fornecida através de uma Runnable no construtor.
 * Suporta quadros redimensionáveis e não redimensionáveis.
 */
public class CustomTopPanel extends JPanel {

    private static final Color TITLE_BAR_BG = ThemeManager.VERY_DARK_BACKGROUND;
    private static final Color TITLE_BUTTON_COLOR = ThemeManager.BASE_PURPLE;
    private static final Color TITLE_BUTTON_HOVER_BG = ThemeManager.COMPONENT_BACKGROUND;
    private static final Color TITLE_TEXT_COLOR = ThemeManager.TEXT_SELECTED;

    private final JFrame targetFrame;
    private final Runnable closeAction;
    private final boolean isResizable;
    private final String iconPath;
    
    private JLabel maximizeButton;
    private Point initialClick;
    private JPanel titleBar;

    /**
     * Cria o painel superior combinado (Barra de Título + Barra de Menu).
     *
     * @param targetFrame     A JFrame que esta barra irá controlar.
     * @param menuBar         A JMenuBar a ser exibida abaixo da barra de título.
     * @param closeAction     A ação (Runnable) a ser executada quando o botão 'X' for clicado.
     * @param isResizable     Se 'true', mostra o botão de maximizar/restaurar.
     * @param iconPath        O caminho relativo (ex: "./images/icon.png") para o ícone da barra.
     */
    public CustomTopPanel(JFrame targetFrame, JMenuBar menuBar, Runnable closeAction, boolean isResizable, String iconPath) {
        super(new BorderLayout());
        this.targetFrame = targetFrame;
        this.closeAction = closeAction;
        this.isResizable = isResizable;
        this.iconPath = iconPath;

        this.titleBar = createCustomTitleBar();
        
        add(titleBar, BorderLayout.NORTH);
        if (menuBar != null) {
            menuBar.setBackground(TITLE_BAR_BG);
            menuBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
            add(menuBar, BorderLayout.CENTER);
        }
    }

    /**
     * Deve ser chamado pelo WindowStateListener da JFrame principal
     * para atualizar o ícone de maximizar/restaurar (se for redimensionável).
     */
    public void onWindowStateChanged() {
        if (isResizable) {
            updateMaximizeButtonState();
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }



    private JPanel createCustomTitleBar() {
		titleBar = new JPanel(new BorderLayout());
		titleBar.setBackground(TITLE_BAR_BG);
        int titleBarHeight = 28;
		titleBar.setPreferredSize(new Dimension(targetFrame.getWidth(), titleBarHeight));

		MouseAdapter dragListener = new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { initialClick = e.getPoint(); titleBar.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)); }
			@Override public void mouseReleased(MouseEvent e) { titleBar.setCursor(Cursor.getDefaultCursor()); }
        };
		MouseMotionAdapter motionListener = new MouseMotionAdapter() {
             @Override public void mouseDragged(MouseEvent e) {
                 if (isResizable && (targetFrame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) return; 
                 
				int thisX = targetFrame.getLocation().x; int thisY = targetFrame.getLocation().y;
				int xMoved = e.getX() - initialClick.x; int yMoved = e.getY() - initialClick.y;
				targetFrame.setLocation(thisX + xMoved, thisY + yMoved);
			}
        };
		titleBar.addMouseListener(dragListener);
		titleBar.addMouseMotionListener(motionListener);

        if (isResizable) {
            titleBar.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                        Component clickedComp = titleBar.getComponentAt(e.getPoint());
                        if(clickedComp == titleBar || (clickedComp instanceof JPanel && clickedComp.getParent() == titleBar && titleBar.getComponent(0) == clickedComp) ) {
                            toggleMaximizeState();
                        }
                    }
                }
            });
        }

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(2, 5, 0, 0));

        try {
            ImageIcon icon = new ImageIcon(this.iconPath); 
            
            if(icon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
                 Image img = icon.getImage();
                 int iconHeight = titleBarHeight - 8; 
                 Image scaledImg = img.getScaledInstance(-1, iconHeight, Image.SCALE_SMOOTH);
                 JLabel iconLabel = new JLabel(new ImageIcon(scaledImg));
                 leftPanel.add(iconLabel);
            } else { 
                System.err.println("Warn: Title bar icon " + this.iconPath + " not found."); 
            }
        } catch (Exception e) { 
            System.err.println("Warn: Title bar icon error - " + e.getMessage()); 
        }

        JLabel windowTitle = new JLabel(targetFrame.getTitle());
        windowTitle.setForeground(TITLE_TEXT_COLOR);
        windowTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leftPanel.add(Box.createVerticalStrut(titleBarHeight));
        leftPanel.add(windowTitle);
        titleBar.add(leftPanel, BorderLayout.WEST);
        
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		buttonPanel.setOpaque(false);

		JLabel minimizeButton = createTitleBarButton("_");
		minimizeButton.setToolTipText("Minimizar");
		minimizeButton.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { targetFrame.setState(Frame.ICONIFIED); } });
        buttonPanel.add(minimizeButton); 
        buttonPanel.add(Box.createHorizontalStrut(1));

        if (isResizable) {
            maximizeButton = createTitleBarButton("□");
            updateMaximizeButtonState();
            maximizeButton.addMouseListener(new MouseAdapter() { @Override public void mouseClicked(MouseEvent e) { toggleMaximizeState(); } });
            buttonPanel.add(maximizeButton); 
            buttonPanel.add(Box.createHorizontalStrut(1));
        }

		JLabel closeButton = createTitleBarButton("X");
        closeButton.setToolTipText("Fechar");
        closeButton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { 
                closeButton.setBackground(ThemeManager.BASE_PURPLE);
                closeButton.setForeground(Color.WHITE); 
            }
			@Override public void mouseExited(MouseEvent e) { 
                closeButton.setBackground(TITLE_BAR_BG); 
                closeButton.setForeground(TITLE_BUTTON_COLOR); 
            }
        });
		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
                if (closeAction != null) {
                    closeAction.run();
                } else {
                    System.exit(0);
                }
			}
		});

		buttonPanel.add(closeButton); 
        buttonPanel.add(Box.createHorizontalStrut(5));

		titleBar.add(buttonPanel, BorderLayout.EAST);
		return titleBar;
	}

    private void toggleMaximizeState() {
        if (!isResizable) return;
        
        boolean isMaximized = (targetFrame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
        if (isMaximized) {
			targetFrame.setExtendedState(Frame.NORMAL);
		} else {
			targetFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		}
        updateMaximizeButtonState();
    }

    private void updateMaximizeButtonState() {
        if (maximizeButton == null) return;
        
        if ((targetFrame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
            maximizeButton.setText("❐");
            maximizeButton.setToolTipText("Restaurar");
        } else {
            maximizeButton.setText("□");
            maximizeButton.setToolTipText("Maximizar");
        }
    }

	private JLabel createTitleBarButton(String text) {
		JLabel buttonLabel = new JLabel(text, SwingConstants.CENTER);
		buttonLabel.setOpaque(true); buttonLabel.setBackground(TITLE_BAR_BG); buttonLabel.setForeground(TITLE_BUTTON_COLOR);
		buttonLabel.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
		buttonLabel.setPreferredSize(new Dimension(45, 28));
		buttonLabel.setBorder(BorderFactory.createEmptyBorder()); buttonLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		buttonLabel.addMouseListener(new MouseAdapter() {
			@Override public void mouseEntered(MouseEvent e) { if (!"X".equals(text)) buttonLabel.setBackground(TITLE_BUTTON_HOVER_BG); }
			@Override public void mouseExited(MouseEvent e) { if (!"X".equals(text)) buttonLabel.setBackground(TITLE_BAR_BG); }
		});
		return buttonLabel;
	}
}