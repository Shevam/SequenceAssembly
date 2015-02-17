import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class MyGui extends JFrame
{
	JPanel headerPanel;
	JPanel contentPanel;
	JLabel lbltitle;
	static JTextField sequenceFileName;
	static JButton btnNext, btnNext2, btnNext3;
	JButton btnbst;
	static ButtonHandler handler;
	GridBagConstraints c;
	
	public MyGui()
	{	
		super("Sequence Assembly");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		
		c = new GridBagConstraints();
		handler = new ButtonHandler();
		
		headerPanel = new JPanel(new GridBagLayout());
		headerPanel.setPreferredSize(new java.awt.Dimension(0, 110));
		headerPanel.setBackground(Color.LIGHT_GRAY);
		c.anchor = GridBagConstraints.CENTER;
		
		lbltitle = new JLabel("SEQUENCE ASSEMBLY");
		lbltitle.setFont(new Font("Serif", Font.BOLD, 30));
		headerPanel.add(lbltitle, c);
		
		contentPanel = new JPanel(new GridBagLayout());
		contentPanel.setPreferredSize(new java.awt.Dimension(0,600));
		contentPanel.setBackground(Color.WHITE);
		
		displayStep1(contentPanel, c);
		
		getContentPane().add(headerPanel, BorderLayout.NORTH);
		getContentPane().add(contentPanel, BorderLayout.SOUTH);
	}
	
	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event) 
		{
			if(event.getSource()==btnNext){
				contentPanel.removeAll();
				contentPanel.repaint();
				displayStep2(contentPanel, c);
			}
			if(event.getSource()==btnNext2){
				contentPanel.removeAll();
				//contentPanel.repaint();
				displayStep3(contentPanel, c);
			}
			
		}

		
	}
	
	private static void displayStep1(JPanel contentPanel, GridBagConstraints c)
	{
		c = new GridBagConstraints();
		
		c.gridx = 1;
		c.gridy = 0;
		c.ipady = 10;
		c.weighty = 0.01;
		contentPanel.add(new JLabel("Please enter sequence file name below"), c);
		
		c.gridy = 1;
		sequenceFileName = new JTextField("BorreliaFull_CompleteSequence.fasta", 50);
		contentPanel.add(sequenceFileName, c);
		
		c.gridx = 2;
		c.ipady = 0;
		contentPanel.add(new JButton("..."), c);
		
		c.gridy = 2;
		btnNext = new JButton("Next");
		contentPanel.add(btnNext, c);
		btnNext.addActionListener(handler);
	}
	
	private static void displayStep2(JPanel contentPanel, GridBagConstraints c) 
	{
		contentPanel.removeAll();
		
		c.gridx = 1;
		c.gridy = 0;
		c.ipady = 10;
		c.weighty = 0.01;
		contentPanel.add(new JLabel("Please enter contig file name below"), c);
		
		c.gridy = 1;
		sequenceFileName = new JTextField("BorreliaFull_CompleteSequence.fasta", 50);
		contentPanel.add(sequenceFileName, c);
		
		c.gridx = 2;
		c.ipady = 0;
		contentPanel.add(new JButton(".."), c);
		
		c.gridy = 2;
		btnNext2 = new JButton("Next");
		contentPanel.add(btnNext2, c);
		btnNext2.addActionListener(handler);
	}
	
	private static void displayStep3(JPanel contentPanel, GridBagConstraints c) 
	{
		contentPanel.removeAll();
		
		c.gridx = 1;
		c.gridy = 0;
		c.ipady = 10;
		c.weighty = 0.01;
		contentPanel.add(new JLabel("Pleas below"), c);
		
		c.gridy = 1;
		sequenceFileName = new JTextField("Borrquence.fasta", 50);
		contentPanel.add(sequenceFileName, c);
		
		c.gridx = 2;
		c.ipady = 0;
		contentPanel.add(new JButton(".."), c);
		
		c.gridy = 2;
		btnNext3 = new JButton("Next");
		contentPanel.add(btnNext3, c);
		btnNext3.addActionListener(handler);
	}
}