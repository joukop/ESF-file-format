package tv.kiekko.eqoa.file;

import java.io.IOException;

import tv.kiekko.eqoa.geom.Box;

public class SimpleSprite extends Obj {
	MaterialPalette matpal;
	Box box;
	PrimBuffer primBuffer;
	CollBuffer collBuffer;
	ObjInfo primInfo;
	ObjInfo collInfo;
	int matpalId;
	float unknown_float;
	boolean usePretrans;

	public SimpleSprite(ObjInfo info) {
		super(info);
		usePretrans = true;
	}

	public Box getBox() {
		return box;
	}

	public String toString() {
		return getClass().getSimpleName() + " " + box;
	}

	@Override
	public void load() throws IOException {
		ObjInfo headerInfo = info.getChild(ObjType.SimpleSpriteHeader);
		Obj header = headerInfo.getObj();
		header.seek();
		header.skipBytes(4);
		box = header.readBox();
		if (headerInfo.getObjectVersion() != 0)
			unknown_float = header.readFloat();
		else
			unknown_float = 500;
		debug(String.format("box=%s unknown=%f", box.toString(), unknown_float));
		// if (info.getObjectVersion()<2) contains SurfaceArray
		primInfo = info.getChild(ObjType.PrimBuffer);
		collInfo = info.getChild(ObjType.CollBuffer);
	}

	public MaterialPalette getMatPal() throws IOException {
		if (matpal == null) {
			matpal = (MaterialPalette) info.getChild(ObjType.MaterialPalette).getObj();
		}
		return matpal;
	}

	public PrimBuffer getPrimBuffer() throws IOException {
		if (primBuffer == null) {
			if (primInfo == null)
				debug("where is primbuffer for " + this);
			else
				primBuffer = (PrimBuffer) primInfo.getObj();
		}
		return primBuffer;
	}

	public CollBuffer getCollBuffer() throws IOException {
		if (collBuffer == null) {
			if (collInfo == null)
				debug("where is collbuffer for " + this);
			collBuffer = (CollBuffer) collInfo.getObj();
		}
		return collBuffer;
	}

}