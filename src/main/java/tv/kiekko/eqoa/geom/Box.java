package tv.kiekko.eqoa.geom;

import io.netty.buffer.ByteBuf;


// bounding box

public class Box {
	float minx,miny,minz,maxx,maxy,maxz;

	public Box() {
		minx=Float.MAX_VALUE;
		miny=minx;
		minz=minx;
		maxx=-minx;
		maxy=maxx;
		maxz=maxx;
	}
	
	public Point getCenter() {
		return new Point(0.5f*(minx+maxx),0.5f*(miny+maxy),0.5f*(minz+maxz));
	}
	
	public Box(float minx,float maxx,float miny,float maxy,float minz,float maxz) {
		this.minx=minx;
		this.maxx=maxx;
		this.miny=miny;
		this.maxy=maxy;
		this.minz=minz;
		this.maxz=maxz;
	}

	public void add(float x,float y,float z) {
		if (x<minx) minx=x;
		if (y<miny) miny=y;
		if (z<minz) minz=z;
		if (x>maxx) maxx=x;
		if (y>maxy) maxy=y;
		if (z>maxz) maxz=z;
	}
	
	public void add(Point p) {
		add(p.x,p.y,p.z);
	}

	public void add(Box b) {
		add(b.getMin());
		add(b.getMax());
	}
	
	public Point getDimensions() {
		return new Point(maxx-minx,maxy-miny,maxz-minz);
	}
	
	
	public static Box read(ByteBuf buf) {
		Box b=new Box();
		b.minx=buf.readFloatLE();
		b.miny=buf.readFloatLE();
		b.minz=buf.readFloatLE();
		b.maxx=buf.readFloatLE();
		b.maxy=buf.readFloatLE();
		b.maxz=buf.readFloatLE();
		return b;
	}
	
	boolean contains(Point p) {
		return minx<p.x && maxx>p.x && miny<p.y && maxy<p.y && minz<p.z && maxz>p.z;
	}
	
	public boolean containsXZ(Point p) {
		return minx<p.x && maxx>p.x && minz<p.z && maxz>p.z;
	}
	
	public String toString() {
		return "[Box x("+minx+" .. "+maxx+"), y("+miny+" .. "+maxy+"), z("+minz+" .. "+maxz+")]";
	}

	public Point getMin() {
		return new Point(minx,miny,minz);
	}
	
	public Point getMax() {
		return new Point(maxx,maxy,maxz);
	}

	public float getSize() {
		Point d=getDimensions();
		float s=d.x;
		if (d.y > s) s=d.y;
		if (d.z > s) s=d.z;
		return s;
	}



}