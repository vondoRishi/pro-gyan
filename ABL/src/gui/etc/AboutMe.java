package gui.etc;

import gui.MainWindow;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import util.SystemUtil;
import util.UserPreference;
import static util.constants.UIlabels.*;

public class AboutMe extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();
	private JButton mBtnPref;
	private JButton mBtnReadMe;
	private JButton mOkButton;
	private static Logger log = Logger.getLogger(AboutMe.class);
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AboutMe dialog = new AboutMe();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AboutMe() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(MainWindow.getFrame());
		setSize( 450, 310);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel();
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		ImageIcon igibIcon = new ImageIcon(AboutMe.class.getResource("/data/Igib_logo.png"));
		
		lblNewLabel.setIcon(new ImageIcon(getScaledImage(igibIcon.getImage(), 370, 135)));
		lblNewLabel.setBounds(12, 55, 424, 139);
		//contentPanel.add(lblNewLabel);
		
		String fontSize = "\"4\"";
		JLabel lblDevelopedBy = new JLabel("<html>"+
				"<body >"+
				"<h1 align=\"center\">"+
				APPLICATION_NAME+" <font size=\"3\">1.1</font>"+
				"</h1>"+
				"<h3 align=\"center\">"+
				  "Developed by :"+
				"</h3>"+
				"<ul >"+
				  "<li >"+
				"Rishi Das Roy (rishi.das@igib.in)"+
				  "</li>"+
				  "<li >"+
				"Dr. Debasis Dash (ddash@igib.res.in)"+
				  "</li>"+
				"</ul>"+
				"<div align=\"center\">"+
				"  at <br><font size="+fontSize+">CSIR-Institute of Genomics &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font>"+
				"</div>"+
				/*"<div >"+
				  "<font size="+fontSize+">and </font>"+
				"</div>"+*/
				"<div align=\"center\">"+
				  "<font size="+fontSize+">and Integrative Biology, Delhi &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><br><br>Report-bug : ddash@igib.res.in"+
				"</div>"+
				"</body>"+
				"</html>");
		lblDevelopedBy.setHorizontalAlignment(SwingConstants.CENTER);
		lblDevelopedBy.setBounds(0, 0, 436, 238);
		contentPanel.add(lblDevelopedBy);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setIcon(new ImageIcon(AboutMe.class.getResource("/data/IGIB.jpeg")));
		lblNewLabel_1.setBounds(329, 166, 107, 33);
		contentPanel.add(lblNewLabel_1);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				mBtnPref = new JButton("Preference");
				mBtnPref.addActionListener(this);
				buttonPane.add(mBtnPref);
			}
			{
				mBtnReadMe = new JButton("Read Me");
				mBtnReadMe.addActionListener(this);
				buttonPane.add(mBtnReadMe);
			}
			{
				mOkButton = new JButton("OK");
				mOkButton.setActionCommand("OK");
				mOkButton.addActionListener(this);
				buttonPane.add(mOkButton);
				getRootPane().setDefaultButton(mOkButton);
			}
		}
	}
	
	/**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     * 
     * 
	 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
	 *
	 * Redistribution and use in source and binary forms, with or without
	 * modification, are permitted provided that the following conditions
	 * are met:
	 *
	 *   - Redistributions of source code must retain the above copyright
	 *     notice, this list of conditions and the following disclaimer.
	 *
	 *   - Redistributions in binary form must reproduce the above copyright
	 *     notice, this list of conditions and the following disclaimer in the
	 *     documentation and/or other materials provided with the distribution.
	 *
	 *   - Neither the name of Oracle or the names of its
	 *     contributors may be used to endorse or promote products derived
	 *     from this software without specific prior written permission.
	 *
	 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
	 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
	 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
	 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
	 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
	 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
	 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
	 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
	 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
	 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
	 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     */
    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

	@Override
	public void actionPerformed(ActionEvent evnt) {
		if(evnt.getSource()==mOkButton){
			setVisible(false);
		} else if(evnt.getSource()==mBtnPref){
			UserPreference.getUpr().repaint();
			UserPreference.getUpr().setLocationRelativeTo(this.getContentPane());
			UserPreference.getUpr().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			UserPreference.getUpr().setVisible(true);
		} else if(evnt.getSource()==mBtnReadMe){
			openHelp();
		}
		
	}
	
	private void openHelp() {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			java.net.URL imgURL = getClass().getResource("/ReadMe.pdf");
            File file = new File(imgURL.getFile());
			if (file.exists()) {
				try {
					desktop.open(file);
				} catch (IOException e) {
					log.error("", e);
					SystemUtil.showErrMsg(null, e.toString());
				}
			} else {
				SystemUtil
						.showErrMsg(null,
								"ReadMe.pdf file is not found in installation directory");
			}

		} else {
			SystemUtil
					.showErrMsg(null,
							"Desktop is not supported\n Please read ReadMe.pdf from Installation folder");
		}
	}
}
