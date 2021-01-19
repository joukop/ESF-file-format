package tv.kiekko.eqoa.file;

import java.io.IOException;

import tv.kiekko.eqoa.geom.Box;

public class SimpleSubSprite extends SimpleSprite {

	public SimpleSubSprite(ObjInfo info) {
		super(info);
	}

	@Override
	public void load() throws IOException {
		info.file.seek(info.getChild(ObjType.SimpleSubSpriteHeader));
		skipBytes(4); // id
		matpalId = readInt();
		debug(String.format("matpal=%x ver=%d", matpalId, info.getObjectVersion()));
		box = Box.read(info.file.getBuf());
		debug("box dimensions=" + box.getDimensions());
		primInfo = info.getChild(ObjType.PrimBuffer);
		collInfo = info.getChild(ObjType.CollBuffer);
	}

	@Override
	public MaterialPalette getMatPal() throws IOException {
		if (matpal == null) {
			matpal = (MaterialPalette) info.file.findObject(matpalId);
		}
		return matpal;
	}

}