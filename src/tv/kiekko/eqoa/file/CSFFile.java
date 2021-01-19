package tv.kiekko.eqoa.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;

/*
 * Decompress CSF to ESF.
 */

public class CSFFile {

	public static void main(String[] a) throws IOException {
		FileInputStream fis = new FileInputStream("/path/to/file.csf");
		ByteBuf cesfHeader = Unpooled.buffer(40);
		cesfHeader.writeBytes(fis, 40);
		ByteBuf sizebuf = Unpooled.buffer(8);
		int num_blocks = cesfHeader.getIntLE(4);
		System.out.println("num_blocks=" + num_blocks);
		long offset = cesfHeader.getLongLE(24);
		System.out.println("offset=" + offset);
		fis.getChannel().position(offset);
		int i;
		int outsize = cesfHeader.getIntLE(32);
		System.out.println("outsize=" + outsize);
		FileOutputStream fos = new FileOutputStream("pregame.esf");
		try {
			for (int b = 0; b < num_blocks; b++) {
				System.out.println("block=" + b + "/" + num_blocks);
				System.out.println("pos=" + fis.getChannel().position());
				sizebuf.writerIndex(0);
				int j = sizebuf.writeBytes(fis, 8);
				System.out.println("pos=" + fis.getChannel().position());
				if (j != 8)
					throw new IOException("can't read size");
				int size = (int) (sizebuf.getLongLE(0) & 0xffffffffL);
				System.out.println("size=" + size);
				ByteBuf data = Unpooled.buffer(size);
				j = data.writeBytes(fis, size);
				if (j != size)
					throw new IOException("can't read data");
				InflaterInputStream inf = new InflaterInputStream(new ByteBufInputStream(data));
				int r = 0;
				while ((i = inf.read()) != -1) {
					r++;
					fos.write(i);
				}
				inf.close();
				data.release();
			}
			System.out.println("end pos=" + fis.getChannel().position());
		} finally {
			fis.close();
			fos.close();
		}
	}

}
