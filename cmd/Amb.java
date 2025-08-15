package cmd;
//Generic AMB Class by ViveTheModder
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Arrays;

public class Amb 
{
	private final byte[] FOOTER = {0,7,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77,0x77};
	private RandomAccessFile amb;
	public Amb(File f) 
	{
		try 
		{
			amb = new RandomAccessFile(f,"r");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public boolean isValidAmb() throws IOException
	{
		amb.seek(0);
		if (amb.readInt()==0x23414D42) return true;
		return false;
	}
	public void extractVags(Path out) throws IOException
	{
		int start,end;
		amb.seek(32);
		start = LittleEndian.getInt(amb.readInt());
		amb.seek(48);
		end = LittleEndian.getInt(amb.readInt());
		amb.seek(start);
		byte[] rawVagData = new byte[end-start];
		amb.read(rawVagData);
		amb.seek(48);
		start = LittleEndian.getInt(amb.readInt());
		amb.seek(start+88); //skip headers, go to selection size and num of VAGs
		amb.readInt(); //skip VAG Info Size
		int numVags = LittleEndian.getInt(amb.readInt())+1;
		int[] vagInfoPositions = new int[numVags];
		int[] vagSampleRates = new int[numVags];
		for (int i=0; i<numVags; i++)
			vagInfoPositions[i] = LittleEndian.getInt(amb.readInt())+4;
		for (int i=0; i<numVags; i++)
		{
			amb.seek(vagInfoPositions[i]+start+80);
			vagSampleRates[i] = LittleEndian.getShort(amb.readShort());
		}
		int size=0, vagCnt=0;
		for (int pos=0; pos<rawVagData.length; pos+=16)
		{
			byte[] input = new byte[16];
			System.arraycopy(rawVagData, pos, input, 0, input.length);
			size+=16;
			if (Arrays.equals(input, FOOTER))
			{
				System.out.println("VAG found! Writing "+vagCnt+".vag...");
				byte[] output = new byte[size];
				System.arraycopy(rawVagData, 16+pos-size, output, 0, size);
				RandomAccessFile vag = new RandomAccessFile(out.resolve(vagCnt+".vag").toString(),"rw");
				//write header info (useful for Awave Studio and Sparking Studio)
				vag.seek(0);
				vag.write("VAGp".getBytes());
				vag.writeInt(3);
				vag.writeInt(0);
				vag.writeInt(size-16);
				vag.writeInt(vagSampleRates[vagCnt]);
				vag.write(new byte[12]);
				byte[] vagNameBytes = ("vag"+vagCnt).getBytes();
				vag.write(vagNameBytes);
				vag.write(new byte[16-vagNameBytes.length]);
				vag.write(output);
				vag.seek(vag.length()-16);
				vag.writeShort(1792); //changes first couple of bytes for the footer to 07 00 (instead of 00 07)
				vag.close();
				size=0;
				vagCnt++;
			}
		}
	}
}