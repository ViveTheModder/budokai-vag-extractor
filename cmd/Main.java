package cmd;
//Budokai VAG Extractor by ViveTheModder
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class Button implements ActionListener               // abstracted button 
{

    Button(String buttonString, String labelString) 
    {

        button = new JButton(buttonString);
        label = new JLabel(labelString);
   
        inputField = new JTextField(10);
        button.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

    JButton button;
    JLabel label;
    JTextField inputField;

    
}

class TaskOne extends Button//java implmentation Stack and Queue
{

    TaskOne(String buttonString, String labelString) 
    {
        super(buttonString, labelString);
       
    }

    @Override
	public void actionPerformed(ActionEvent e) 
	{
    	try 
		{
        	String result = Main.Run();
        	JOptionPane.showMessageDialog(null, result);
    	} 	
		catch (IOException ex) 
		{
        	ex.printStackTrace();
        	JOptionPane.showMessageDialog(null, "An error occurred:\n" + ex.getMessage());
    	}
}

    
}

class GUI
{
    GUI() 
    {
        frame = new JFrame(); 
        pannel = new JPanel();

        frame = new JFrame(); 
        pannel = new JPanel();
        pannel.setBackground(Color.LIGHT_GRAY);
  
        pannel.setBorder(BorderFactory.createEmptyBorder(50, 50, 100, 150));
        pannel.setLayout(new GridLayout(1, 1));

		try {
            buttonOne = new TaskOne("Click Here!", "budokai-vag-extractor");
            pannel.add(buttonOne.label);
            pannel.add(buttonOne.button);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error creating button:\n" + e.getMessage());
        }

  
        frame.add(pannel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setTitle("Vive budokai-vag-extractor");
        
        frame.setSize(400, 200);
        frame.pack();
        frame.setVisible(true); 

    }

    public static void main(String[] args) throws IOException
    {
        new GUI();           
    }
    
    JFrame frame;
    JPanel pannel;

    TaskOne buttonOne;

}

public class Main
{
	public static String Run() throws IOException
	{
		File out=null;
		File[] dirs=null;

		while (dirs==null && out==null)
		{
			String path = JOptionPane.showInputDialog("Enter a valid path to a folder with AMBs containing VAGs:");

			File tmp = new File(path);
			if (tmp.isDirectory())
			{
				File[] tmpFiles = tmp.listFiles(new FilenameFilter()
				{
					@Override
					public boolean accept(File dir, String name) 
					{
						String lower = name.toLowerCase();
						return lower.endsWith(".amb") || lower.endsWith(".unk");
					}
				});
				if (tmpFiles!=null)
				{
					if (tmpFiles.length>0) dirs=tmpFiles;
				}
			}

			String newPath = JOptionPane.showInputDialog("Enter a valid path to a folder where the VAGs will be saved:");
			tmp = new File(newPath);
			if (tmp.isDirectory()) out=tmp;
		}
	
		Amb[] ambs = new Amb[dirs.length];
		for (int i=0; i<ambs.length; i++) ambs[i] = new Amb(dirs[i]);
		long start = System.currentTimeMillis();
		for (int i=0; i<ambs.length; i++) 
		{
			Path newPath = out.toPath().resolve(dirs[i].getName().replace(".unk", ""));
			File newFile = newPath.toFile();
			newFile.mkdir();
			System.out.println("["+newFile.getName()+"]");
			if (ambs[i].isValidAmb()) ambs[i].extractVags(newPath);
		}
		long finish = System.currentTimeMillis();
		return "Time elapsed: "+(finish-start)/1000.0+" s.";

	}
}

