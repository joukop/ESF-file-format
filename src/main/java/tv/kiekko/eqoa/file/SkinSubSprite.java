package tv.kiekko.eqoa.file;

import java.io.IOException;

public class SkinSubSprite extends SimpleSubSprite {

	public SkinSubSprite(ObjInfo info) {
		super(info);
	}

	@Override
	public void load() throws IOException {
		Obj content=info.getChild(ObjType.SkinSubSprite2).getObj();
		content.seek();
		int id=content.readInt();
		matpalId=content.readInt();
		box=content.readBox();
		debug(String.format("id=%08x matpal=%08x box=%s",id,matpalId,box));
		debug("children="+info.getChildren());
		primInfo=info.getChild(ObjType.SkinPrimBuffer);
	}
	
	
}
