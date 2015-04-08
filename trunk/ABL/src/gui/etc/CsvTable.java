package gui.etc;

import static util.constants.FileName.PREDICTION_CSV;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import util.SystemUtil;

import feature.util.FileUtil;
import javax.swing.JButton;

public class CsvTable extends JFrame implements ActionListener {
	private static Logger log = Logger.getLogger(CsvTable.class);
	private JPanel contentPane;
	private JTable table;
	private JButton btnClose;
	private JButton btnSave;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CsvTable frame = new CsvTable("/home/rishi.das/tmp/EColi_Features/Class/Paper/Chapman_mRNA_etc.csv",true);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @param isHeader 
	 * @param pCsvFilePath 
	 * @throws IOException 
	 */
	public CsvTable(String pCsvFilePath, boolean isHeader) throws IOException {
		String wrkSpace = SystemUtil.getWorkSpace();
		setTitle(pCsvFilePath.replaceAll(Pattern.quote(wrkSpace+File.separator), "").replaceAll(Pattern.quote(File.separator), " > ").replaceAll(".csv", ""));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setResizable(false);
		setLocationByPlatform(true);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 12, 424, 213);
		contentPane.add(scrollPane);
		
		
		List<String> lData =FileUtil.getFileAsList(pCsvFilePath);
		String[] columns=null;
		Vector<String> columnNames = new Vector<String>();
		if(isHeader){
			 columns = lData.remove(0).split(",");
			 for (String name : columns) {
				 columnNames.addElement(name);
			}
		}
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		
		for (String rowString : lData) {
			columns = rowString.split(",");
			Vector<String> row = new Vector<String>();
			 for (String name : columns) {
				 row.addElement(name);
			}
			 data.addElement(row);
		}
		DefaultTableModel l = new  DefaultTableModel(data, columnNames);
		table = new JTable(l){
			public boolean isCellEditable(int rowIndex, int colIndex) {
				  return false; //Disallow the editing of any cell
				  }
		};
		scrollPane.setViewportView(table);
		
		btnClose = new JButton("Close");
		btnClose.setBounds(319, 237, 117, 25);
		btnClose.addActionListener(this);
		contentPane.add(btnClose);
		
		/*btnSave = new JButton("Save");
		btnSave.setBounds(190, 237, 117, 25);
		btnSave.addActionListener(this);
		contentPane.add(btnSave);*/
	}

	public static CsvTable get(String pCsvFilePath, boolean isHeader) throws IOException {
		
		return new CsvTable(pCsvFilePath+PREDICTION_CSV, isHeader);
	}

	@Override
	public void actionPerformed(ActionEvent arg) {
		if(arg.getSource()==btnClose){
			this.dispose();
		}
		
	}
}

