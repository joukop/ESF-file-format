package tv.kiekko.eqoa.file;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ObjInfo {

	public int type;
	int version;
	int numSubObjects;
	int size;
	public int offset;
	final int headerSize = 12;
	public ObjFile file;
	public int dictID;
	List<ObjInfo> children = new ArrayList<>();
	ObjInfo parent;

	public List<ObjInfo> getChildren() {
		return getChildren(0);
	}

	public ObjInfo getParent() {
		return getParent(0);
	}

	public List<ObjInfo> getChildren(int type) {
		List<ObjInfo> o = new ArrayList<>();
		for (ObjInfo c : children)
			if (type == 0 || c.type == type)
				o.add(c);
		return o;
	}

	public ObjInfo getChild(int type) {
		List<ObjInfo> c = getChildren(type);
		if (c.size() == 1)
			return c.get(0);
		if (c.size() > 1)
			throw new IllegalStateException("Object '" + toString() + "' has " + c.size() + " children of type "
					+ String.format("%04x", type) + ". Asked for just one.");
		return null;
	}

	public ObjInfo getParent(int type) {
		ObjInfo p = parent;
		while (type != 0 && p.type != type) {
			p = p.parent;
			if (p == null)
				return null;
		}
		return p;
	}

	public ObjInfo getNextSibling() {
		List<ObjInfo> c = this.getParent().children;
		return c.get(c.indexOf(this) + 1);
	}

	public ObjInfo(ObjFile file, ObjInfo parent) throws IOException {
		this.file = file;
		type = file.buf.readIntLE();
		version = type >> 16;
		type = type & 0xffff;
		size = file.buf.readIntLE();
		numSubObjects = file.buf.readIntLE();
		offset = file.buf.readerIndex();
		this.parent = parent;
		if (parent != null)
			parent.addChild(this);
	}

	public Obj getObj() throws IOException {
		int saveoffset = file.buf.readerIndex();
		Obj ret = file.getObject(this);
		file.buf.readerIndex(saveoffset);
		return ret;
	}

	public ObjType getType() {
		return ObjType.objTypeMap.get(this.type);
	}

	public int getObjectVersion() {
		return version;
	}

	void addChild(ObjInfo obj) {
		children.add(obj);
	}

	int getAvailable() {
		return size - (file.buf.readerIndex() - offset);
	}

	public String toString() {
		if (dictID != 0)
			return ObjFile.getObjectName(type)
					+ String.format(" (0x%08x 0x%x) [%d] 0x%08x", offset, size, numSubObjects, dictID);
		else
			return ObjFile.getObjectName(type) + String.format(" (0x%08x 0x%x) [%d]", offset, size, numSubObjects);
	}

	public void print(PrintStream out) {
		print(out, "");
	}

	public void printParents(PrintStream out) {
		ObjInfo o = this;
		String indent = "";
		while (o != null) {
			out.println(indent + o.toString());
			if (indent.length() == 0) {
				indent = "^- ";
			}
			indent = "\t" + indent;
			o = o.parent;
		}
	}

	public void print(PrintStream out, String indent) {
		if (numSubObjects == 0) {
			out.println(indent + toString());
		} else {
			out.println(indent + toString() + " {");

			for (ObjInfo child : children) {
				child.print(out, indent + "\t");
			}
			out.println(indent + "}");
		}
	}

}