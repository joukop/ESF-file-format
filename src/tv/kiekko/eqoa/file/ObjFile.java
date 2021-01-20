package tv.kiekko.eqoa.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import tv.kiekko.eqoa.file.Dictionary;

public class ObjFile {
	String magic;
	int numObjects;
	int fileType;
	long field5;
	ByteBuf buf;
	final static int MAX_NESTING = 16;
	Dictionary dictionary;
	ObjInfo root;
	static PrintStream out = System.out;
	Map<Integer, Obj> objectCache = new HashMap<>();

	public ObjInfo getRoot() throws IOException {
		if (root == null)
			parse();
		return root;
	}

	// Our implementation creates a dictionary containing all objects that have a
	// dictionary ID
	// when the file is opened. This slows down opening it but simplifies the
	// subsequent parsing process a lot.

	public void buildDictionary() throws IOException {
		dictionary = new Dictionary(this);
		getRoot();
		debug("building dictionary from " + objects.size() + " objects");
		for (ObjInfo o : objects) {
			if (o.dictID != 0 && !dictionary.contains(o.dictID)) {
				dictionary.add(o);
			}
		}
		debug("built dictionary, size=" + dictionary.map.size());
	}

	// find an object in the dictionary by ID

	public Obj findObject(int id) throws IOException {
		if (dictionary == null)
			buildDictionary();
		ObjInfo e = dictionary.get(id);
		if (e == null)
			return null;
		Obj ret = e.getObj();
		if (ret == null)
			debug("getObject(): type " + String.format("%04x", e.type) + " not implemented");
		return ret;
	}

	// load an object, called by ObjInfo.getObj()

	public Obj getObject(ObjInfo info) throws IOException {

		Obj obj = objectCache.get(info.offset);

		if (obj != null)
			return obj;

		switch (info.type) {
		case ObjType.SurfaceArray:
			break;
		case ObjType.Surface:
			obj = new Surface(info);
			break;
		case ObjType.MaterialPalette:
			obj = new MaterialPalette(info);
			break;
		case ObjType.Material:
			obj = new Material(info);
			break;
		case ObjType.PrimBuffer:
			obj = new PrimBuffer(info);
			break;
		case ObjType.Zone:
			obj = new Zone(info);
			break;
		case ObjType.SimpleSubSprite:
			obj = new SimpleSubSprite(info);
			break;
		case ObjType.ZoneBase:
			obj = new ZoneBase(info);
			break;
		case ObjType.ZonePreTranslations:
			obj = new ZonePreTranslations(info);
			break;
		case ObjType.SimpleSprite:
			obj = new SimpleSprite(info);
			break;
		case ObjType.ZoneActors:
			obj = new ZoneActors(info);
			break;
		case ObjType.ZoneActor:
			obj = new ZoneActor(info);
			break;
		case ObjType.LODSprite:
			obj = new LODSprite(info);
			break;
		case ObjType.GroupSprite:
			obj = new GroupSprite(info);
			break;
		case ObjType.CollBuffer:
			obj = new CollBuffer(info);
			break;
		case ObjType.WorldZoneProxies:
			obj = new WorldZoneProxies(info);
			break;
		case ObjType.HSprite:
			obj = new HSprite(info);
			break;
		case ObjType.HSpriteHierarchy:
			obj = new HSprite.HSpriteHierarchy(info);
			break;
		case ObjType.CSprite:
			obj = new CSprite(info);
			break;
		case ObjType.SkinSubSprite:
			obj = new SkinSubSprite(info);
			break;
		case ObjType.SkinPrimBuffer:
			obj = new PrimBuffer(info);
			break;
		// If we haven't implemented a class for this object type, create a generic Obj

		default:
			obj = new Obj(info);
		}
		if (obj != null) {
			obj.load();
			objectCache.put(info.offset, obj);
		}
		return obj;
	}

	public static String getObjectName(int type) {
		ObjType t = ObjType.objTypeMap.get(type);
		if (t == null)
			return String.format("Unknown(%04x)", type);
		return String.format("%s(%04x) ", t.name, type);
	}

	void readFileHeader() throws IOException {
		ByteBuf header = Unpooled.buffer(0x20);
		buf.readBytes(header);
		byte[] tmp = new byte[4];
		header.readBytes(tmp);
		magic = "" + (char) tmp[3] + (char) tmp[2] + (char) tmp[1] + (char) tmp[0];
		numObjects = header.readIntLE();
		fileType = header.readIntLE();
		int unknown = header.readIntLE();
		long offset = header.readLongLE();
		long unknown2 = header.readLongLE();
		header.release();
		buf.readerIndex((int) offset);
	}

	// map the file as a Netty buffer so we can use the same parsing methods as with
	// UDP packets

	public ObjFile(File f) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		FileChannel fc = fis.getChannel();
		MappedByteBuffer mappedBuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, f.length());
		buf = Unpooled.wrappedBuffer(mappedBuf);
		fis.close();
		readFileHeader();
		if (!magic.equals("OBJF"))
			throw new IOException("missing OBJF magic");
	}

	public ObjFile(String filename) throws IOException {
		this(new File(filename));
	}
	
	public void seek(long offset) {
		buf.readerIndex((int) offset);
	}

	public void seek(ObjInfo obj) {
		seek(obj.offset);
	}

	public String toString() {
		return String.format("[ObjFile magic=%s numObjects=%d type=%x field5=%x", magic, numObjects, fileType, field5);
	}

	public ObjInfo readBegin(String debugInfo, int acceptType, ObjInfo parent) throws IOException {

		ObjInfo obj = new ObjInfo(this, parent); // reads the obj header, 12 bytes

		int type = obj.type;

		if (acceptType != 0 && type != acceptType)
			throw new IOException(String.format("readBegin " + debugInfo + ": expected object type %04x, found %04x",
					acceptType, type));

		if (type == 0x2000)
			sprites++;

		return obj;
	}

	int sprites = 0;

	public void readEnd(ObjInfo obj) {
		int skip = obj.getAvailable();
		if (skip != 0) {
			buf.skipBytes(skip);
		}
	}

	public List<ObjInfo> objects = new ArrayList<>();

	public ObjInfo readObject(ObjInfo parent) throws IOException {
		ObjInfo o = readBegin("", 0, parent);
		ObjType type = o.getType();
		if (type != null && type.dictIdOffset != null) {
			int id = buf.readIntLE();
			buf.readerIndex(buf.readerIndex() - 4);
			o.dictID = id;
		}
		objects.add(o);
		for (int si = 0; si < o.numSubObjects; si++) {

			readObject(o);

		}
		readEnd(o);
		return o;
	}

	public void parse() throws IOException {
		root = readObject(null);
		debug("ObjInfo parse() got " + objects.size() + " objects");
	}

	public long getOffset() {
		return buf.readerIndex();
	}

	static void debug(String s) {
		out.println(s);
	}

	public static void setLogFile(String filename) throws FileNotFoundException {
		out = new PrintStream(new FileOutputStream(filename));
	}

	public ObjInfo findObjectOfType(int type, int num) {
		for (ObjInfo o : objects) {
			if ((o.type) == type) {
				if (num-- == 0)
					return o;
			}
		}
		return null;
	}

	public ByteBuf getBuf() {
		return buf;
	}

}
