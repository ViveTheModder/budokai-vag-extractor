package cmd;
//Generic AMB Class by ViveTheJoestar
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

public class Amb {
	private final byte[] FOOTER = 
	{0,7,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77};
	private int numVags=0;
	private RandomAccessFile amb;
	public Amb(File f) {
		try  {
			amb = new RandomAccessFile(f,"r");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public boolean isValidAmb() throws IOException {
		amb.seek(0);
		if (amb.readInt()==0x23414D42) return true;
		return false;
	}
	public int getNumVags() {
		return numVags;
	}
	public void extractVags(Path out, JProgressBar bar, JTextArea textArea, boolean print) 
	throws IOException {
		if (!print) bar.setValue(0);
		int start, end;
		amb.seek(32);
		start = LittleEndian.getInt(amb.readInt());
		amb.seek(48);
		end = LittleEndian.getInt(amb.readInt());
		amb.seek(start);
		byte[] rawVagData = new byte[end-start];
		amb.read(rawVagData);
		amb.seek(48);
		start = LittleEndian.getInt(amb.readInt());
		amb.seek(start + 88); //skip headers, go to selection size and num of VAGs
		amb.readInt(); //skip VAG Info Size
		int numVags = LittleEndian.getInt(amb.readInt())+1;
		this.numVags = numVags;
		if (!print) bar.setMaximum(numVags);
		int[] vagInfoPositions = new int[numVags];
		int[] vagSampleRates = new int[numVags];
		for (int i=0; i<numVags; i++)
			vagInfoPositions[i] = LittleEndian.getInt(amb.readInt())+4;
		for (int i=0; i<numVags; i++) {
			amb.seek(vagInfoPositions[i]+start+80);
			vagSampleRates[i] = LittleEndian.getShort(amb.readShort());
		}
		int size=0, vagCnt=0;
		for (int pos=0; pos<rawVagData.length; pos+=16) {
			byte[] input = new byte[16];
			System.arraycopy(rawVagData, pos, input, 0, input.length);
			size+=16;
			if (Arrays.equals(input, FOOTER)) {
				String vagFound = "VAG found! Writing "+vagCnt+".vag...";
				if (print) System.out.println(vagFound);
				else {
					LocalDateTime ldt = LocalDateTime.now();
			        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
					textArea.setText(textArea.getText() + ldt.format(dtf) + " - " + vagFound + "\n");
				}
				byte[] output = new byte[size];
				System.arraycopy(rawVagData, 16 + pos - size, output, 0, size);
				String vagPath = out.resolve(vagCnt + ".vag").toString();
				RandomAccessFile vag = new RandomAccessFile(vagPath, "rw");
				//write header info (useful for Awave Studio and Sparking Studio)
				vag.seek(0);
				vag.write("VAGp".getBytes());
				vag.writeInt(3);
				vag.writeInt(0);
				vag.writeInt(size - 16);
				vag.writeInt(vagSampleRates[vagCnt]);
				vag.write(new byte[12]);
				byte[] vagNameBytes = ("vag" + vagCnt).getBytes();
				vag.write(vagNameBytes);
				vag.write(new byte[16 - vagNameBytes.length]);
				vag.write(output);
				vag.seek(vag.length() - 16);
				vag.writeShort(1792); //changes first couple of bytes for the footer to 07 00 (instead of 00 07)
				vag.close();
				size = 0;
				vagCnt++;
				if (!print) bar.setValue(vagCnt);
			}
		}
	}
}