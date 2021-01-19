package tv.kiekko.eqoa.file;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * Dictionary containing Materials, Sprites, or other resources
 * that can be referred with their 32-bit ID.
 */

public class Dictionary {
	ObjFile file;
	Map<Integer, Entry> map;

	class Entry {
		long offset;
		ObjInfo objInfo;

		Entry(long offset, ObjInfo objInfo) {
			this.offset = offset;
			this.objInfo = objInfo;
		}

	}

	Dictionary(ObjFile file) {
		map = new HashMap<Integer, Entry>();
		this.file = file;
	}

	public String toString() {
		String ret = "(";
		for (int i : map.keySet()) {
			ret += String.format("%x:", i) + map.get(i).objInfo + " ";
		}
		ret += ")";
		return ret;
	}

	void add(ObjInfo objInfo) throws IOException {
		if (objInfo.dictID == 0)
			throw new IOException("can't add zero ID to dictionary");
		if (map.containsKey(objInfo.dictID)) {
			ObjFile.debug("double add dictionary ID " + String.format("%x", objInfo.dictID));
		}
		map.put(objInfo.dictID, new Entry(file.getOffset(), objInfo));
	}

	boolean contains(int id) throws IOException {
		if (id == 0)
			throw new IOException("don't query zero id");
		return map.containsKey(id);
	}

	public ObjInfo get(int id) throws IOException {
		if (id == 0)
			throw new IOException("don't get zero id");
		Entry e = map.get(id);
		if (e == null)
			return null;
		// Special case for certain object types, e.g. MaterialPalette, and HSPrite.
		// The dictionary ID is in a separate header object. We're interested in its
		// parent, not the ID container object.
		if (e.objInfo.getType().isIdContainer())
			return e.objInfo.getParent();
		return e.objInfo;
	}

}