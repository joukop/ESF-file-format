package tv.kiekko.eqoa.geom;

public class Vec2 {
	public float x;
	public float y;
	
	public Vec2() {
		
	}
	
	public void set(float x,float y) {
		this.x=x;
		this.y=y;
	}

	public Vec2(float x,float y) {
		set(x,y);
	}

	public String toString() {
		return "("+x+","+y+")";
	}
	
	public Vec2 copy() {
		return new Vec2(x,y);
	}
	
}

