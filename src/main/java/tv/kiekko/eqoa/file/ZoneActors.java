package tv.kiekko.eqoa.file;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ZoneActors extends Obj {
	List<ZoneActor> actors;

	public ZoneActors(ObjInfo info) {
		super(info);
	}

	@Override
	public void load() throws IOException {
		actors = new ArrayList<ZoneActor>();
		debug("loading actors");
		for (ObjInfo a : info.getChildren(ObjType.ZoneRoomActors)) {
			for (ObjInfo b : a.getChild(ObjType.ZoneRoomActors3).getChildren(ObjType.ZoneActor)) {
				actors.add((ZoneActor) b.getObj());
			}
		}
	}

	public List<ZoneActor> getActors() {
		return actors;
	}

}
