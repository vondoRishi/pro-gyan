package util;

import static util.constants.UIlabels.MULTIPLE_FASTA;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;

/**
 * @author Rishi Das Roy
 *
 * @Organization Institute Of Genomics & Integrative Biology
 */
public class FastaFocusListener implements FocusListener {

	public void focusGained(FocusEvent pFE) {
		JTextArea lTA = (JTextArea) pFE.getSource();
		if (lTA.getText().equals(MULTIPLE_FASTA))
			lTA.setText("");
	}

	public void focusLost(FocusEvent pFE) {
		JTextArea lTA = (JTextArea) pFE.getSource();
		if (lTA.getText().length() == 0)
			lTA.setText(MULTIPLE_FASTA);
	}
}
