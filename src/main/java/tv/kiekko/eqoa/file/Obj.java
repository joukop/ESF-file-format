package tv.kiekko.eqoa.file;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import tv.kiekko.eqoa.geom.Box;
import tv.kiekko.eqoa.geom.Point;

/*
 * This is the base class of all objects in an ESF file.
 * 
 */

public class Obj {
	protected ObjInfo info;
	protected boolean loaded;
	protected static boolean debug = true;

	public Obj(ObjInfo info) {
		this.info = info;
	}

	public String toString() {
		if (getClass() == Obj.class)
			return String.format("[Obj %04x]", info.type);
		else
			return super.toString();
	}

	// seek to the beginning of this object's data

	public void seek() {
		info.file.seek(info);
	}

	public void load() throws IOException {
	}

	public int readByte() {
		return info.file.buf.readByte();
	}

	public int readInt() {
		return info.file.buf.readIntLE();
	}

	public void readBytes(ByteBuf dst) {
		info.file.buf.readBytes(dst);
	}

	public short readShort() {
		return info.file.buf.readShortLE();
	}

	public void readBytes(byte[] dst) {
		info.file.buf.readBytes(dst);
	}

	public long readLong() {
		return info.file.buf.readLongLE();
	}

	public float readFloat() {
		return info.file.buf.readFloatLE();
	}

	public String readString() throws IOException {
		int len = readShort();
		if (len < 0 || len > 1024)
			throw new IOException("readString sanity check: len=" + len);
		byte[] bytes = new byte[len];
		readBytes(bytes);
		return new String(bytes);
	}

	public byte[] readColor() {
		byte[] ret = new byte[4];
		readBytes(ret);
		return ret;
	}

	public Point readPoint() {
		return Point.read(info.file.buf);
	}

	public Box readBox() {
		return Box.read(info.file.buf);
	}

	public void skipBytes(int i) {
		info.file.buf.skipBytes(i);
	}

	protected void debug(String s) {
		if (debug == false)
			return;
		ObjFile.debug("[" + getClass().getSimpleName() + "] " + s);
	}

}