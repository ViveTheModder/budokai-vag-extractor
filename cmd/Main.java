package cmd;
//Budokai VAG Extractor by ViveTheJoestar
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import gui.App;

public class Main {
	public static File[] getAmbDirs(File dir) {
		if (dir != null) {
			return dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					String lower = name.toLowerCase();
					return lower.endsWith(".amb") || lower.endsWith("bin")
						|| lower.endsWith(".unk");
				}
			});
		}
		return null;
	}
	public static double extract(File out, File[] dirs, 
	JProgressBar[] bars, JTextArea ta, boolean print) throws IOException {
		out.mkdir();
		Amb[] ambs = new Amb[dirs.length];
		for (int i=0; i<ambs.length; i++) ambs[i] = new Amb(dirs[i]);
		long start = System.currentTimeMillis();
		for (int i=0; i<ambs.length; i++) {
			String ambFileName = dirs[i].getName().toLowerCase();
			String fileExt = ".unk";
			if (ambFileName.endsWith(".amb")) fileExt = ".amb";
			else if (ambFileName.endsWith(".bin")) fileExt = ".bin";
			Path newPath = out.toPath().resolve(ambFileName.replace(fileExt, ""));
			File newFile = newPath.toFile();
			newFile.mkdir();
			if (print) System.out.println("[" + newFile.getName() + "]");
			else ta.setText(ta.getText() + "[" + newFile.getName() + "]\n");
			if (ambs[i].isValidAmb()) {
				if (print) ambs[i].extractVags(newPath, null, ta, print);
				else ambs[i].extractVags(newPath, bars[1], ta, print);
			}
			if (ambs[i].getNumVags() == 0) { 
				newFile.delete();
				if (!print) bars[0].setMaximum(bars[0].getMaximum() - 1);
			}
			else {
				if (!print) bars[0].setValue(bars[0].getValue() + 1);
			}
		}
		long finish = System.currentTimeMillis();
		return (finish-start)/1000.0; //yes, the method returns the time it takes
	}
	public static void main(String[] args) throws IOException {
		File out = null;
		File[] dirs = null;
		String error = "ERROR: Invalid folder path!";
		if (args.length>0) {
			if (args.length>1) {
				File tmp = new File(args[0].replace("\"", ""));
				if (tmp.isDirectory()) {
					File[] tmpFiles = getAmbDirs(tmp);
					if (tmpFiles!=null) {
						if (tmpFiles.length>0) dirs=tmpFiles;
					}
				} else { 
					System.out.println(error);
					System.exit(1);
				}
				tmp = new File(args[1].replace("\"", ""));
				if (tmp.isDirectory() || tmp.mkdir()) out = tmp;
				else { 
					System.out.println(error);
					System.exit(2);
				}
				System.out.println("Time elapsed: "+extract(out, dirs, null, null, true)+" s");
			}
			else {
				if (args[0].equals("-h"))
					System.out.println("USAGE: java -jar budokai-vag-extractor.jar "
					+ "\"path/to/AMBs\" \"path/to/VAGs\"");
			}
		} else App.setApp();
	}
}