package tv.kiekko.eqoa.file;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/*
 * Surface = texture
 */

public class Surface extends Obj {
	int w, h;
	int depth;
	int mip;
	BufferedImage image;
	BufferedImage alpha;
	boolean hasAlpha;

	public Surface(ObjInfo info) {
		super(info);
	}

	@Override
	public void load() {
		info.file.seek(this.info);
		int dict_id = readInt();
		w = readInt();
		h = readInt();
		int depth = readInt();
		mip = readInt();
		debug("dict_id=" + String.format("%x", dict_id) + " depth=" + depth + " mip=" + mip + " w=" + w + " h=" + h);

		ByteBuf palette = null;
		if (depth < 2) {
			int datasize = readInt();
			datasize <<= 2;
			palette = Unpooled.buffer(datasize);
			readBytes(palette);
		}
		ByteBuf mipbuf = null;
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		alpha = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		hasAlpha = false;
		if (mip > 0) {
			int i = 0;
			int mipsize = readInt();
			int total = 0;
			// debug("mipsize="+mipsize);
			mipbuf = Unpooled.buffer(mipsize);
			for (int j = 0; j < h; j++) {
				total += mipsize;
				mipbuf.writerIndex(0);
				readBytes(mipbuf);
				if (i == 0) { // only consider the full-size mipmap
					for (int x = 0; x < mipsize; x++) {
						int col = mipbuf.getByte(x) & 255;
						int off = col << 2;
						int rgba = palette.getIntLE(off);
						int b = ((rgba & 0xff0000) >> 16) & 255;
						int g = ((rgba & 0xff00) >> 8) & 255;
						int r = (rgba & 0xff);
						int a = ((rgba & 0xff000000) >> 24) & 255;
						if (a != 255) {
							if (hasAlpha == false)
								debug("found a=" + a + ", hasAlpha true");
							hasAlpha = true;
						}
						rgba = (a << 24) | (r << 16) | (g << 8) | b;
						image.setRGB(x, j, rgba);
						alpha.setRGB(x, j, (int) (65535f * a / 255f));
					}
				}
			}
			if (!hasAlpha)
				alpha = null;
			mipbuf.release();
			debug("mipmap " + total + " bytes");
			w >>= 1;
			if (w == 0)
				w = 1;
			h >>= 1;
			if (h == 0)
				h = 1;
		}
		palette.release();
		loaded = true;
	}

	public BufferedImage getTexture() {
		return image;
	}

	public boolean hasAlpha() {
		return hasAlpha;
	}

	public void saveTexture(String dir, String fn, String alp) throws IOException {
		File dirf = new File(dir);
		if (!dirf.exists())
			dirf.mkdir();
		FileOutputStream fos = new FileOutputStream(dir + File.separator + fn);
		ImageIO.write(image, "png", fos);
		fos.close();
		if (alpha != null) {
			fos = new FileOutputStream(dir + File.separator + alp);
			ImageIO.write(alpha, "png", fos);
			fos.close();
		}
	}

	@Override
	protected void debug(String s) {
	}

}