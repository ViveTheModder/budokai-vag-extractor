package cmd;
//Budokai VAG Extractor by ViveTheModder
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main 
{
	public static void main(String[] args) throws IOException 
	{
		File out=null;
		File[] dirs=null;
		Scanner sc = new Scanner(System.in);
		while (dirs==null && out==null)
		{
			System.out.println("Enter a valid path to a folder with AMBs containing VAGs:");
			String path = sc.nextLine();
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
			System.out.println("Enter a valid path to a folder where the VAGs will be saved:");
			path = sc.nextLine();
			tmp = new File(path);
			if (tmp.isDirectory()) out=tmp;
		}
		sc.close();
		
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
		System.out.println("Time elapsed: "+(finish-start)/1000.0+" s.");
	}
}