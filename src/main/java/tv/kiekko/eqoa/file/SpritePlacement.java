package tv.kiekko.eqoa.file;

import java.io.IOException;

import tv.kiekko.eqoa.geom.Point;

public class SpritePlacement {
	SimpleSprite sprite;
	int sprite_id;
	public Point pos;
	public Point rot;
	float f; // scaling in some contexts
	byte[] color; // unsure

	public SpritePlacement(int id, Point pos2, Point rot2, float f2) {
		sprite_id = id;
		pos = pos2;
		rot = rot2;
		f = f2;
	}

	public SpritePlacement(int actor_id, Point pos2, Point rot2, float a, byte[] color2) {
		sprite_id = actor_id;
		pos = pos2;
		rot = rot2;
		f = a;
		color = color2;
	}

	public SpritePlacement(SimpleSprite s) {
		sprite = s;
		pos = new Point(0, 0, 0);
		rot = new Point(0, 0, 0);
	}

	public SpritePlacement(SimpleSprite s, SpritePlacement p) {
		this(s);
		pos = p.pos;
		rot = p.rot;
		f = p.f;
	}

	public SimpleSprite getSprite(ObjFile file) throws IOException {
		if (sprite != null)
			return sprite;
		Obj obj = file.findObject(sprite_id);
		if (obj instanceof SimpleSprite)
			return (SimpleSprite) obj;
		ObjFile.debug("can't place this type of object: " + obj);
		return null;
	}

	public void transform(Point p) {
		if (f > 0) {
			p.multiplyWith(f);
		}
		p.rotate(rot);
		p.addTo(pos);
	}

	public float getScale() {
		if (f > 0)
			return f;
		else
			return 1;
	}

	public String toString() {
		return String.format("[SpritePlacement id=%08x pos=%s rot=%s f=%f s=%s]", sprite_id, pos, rot, f,
				sprite == null ? "-" : sprite.getClass().getSimpleName());
	}

	public SpritePlacement add(SpritePlacement p) {
		SpritePlacement added = new SpritePlacement(sprite_id, pos.add(p.pos), rot.add(p.rot), f, color);
		added.sprite = this.sprite;
		added.sprite_id = this.sprite_id;
		return added;
	}

}
