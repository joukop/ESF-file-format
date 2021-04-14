package tv.kiekko.eqoa.geom;

import java.io.Serializable;

import io.netty.buffer.ByteBuf;

public class Point implements Serializable {
	private static final long serialVersionUID = 1L;
	public float x;
	public float y;
	public float z;
	
	
	public static Point read(ByteBuf buf) {
		Point ret=new Point(0,0,0);
		ret.x=buf.readFloatLE();
		ret.y=buf.readFloatLE();
		ret.z=buf.readFloatLE();
		return ret;
	}
	
	public Point copy() {
		return new Point(this.x,this.y,this.z);
	}
	
	public Point(float x,float y,float z) {
		set(x,y,z);
	}
	
	public void set(float x,float y,float z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public void set(Point p) {
		x=p.x;
		y=p.y;
		z=p.z;
	}
	
	public String toString() {
		return "("+x+","+y+","+z+")";
	}
	
	public Point sub(Point p) {
		return new Point(x-p.x,y-p.y,z-p.z);
	}
	
	public Point add(Point p) {
		return new Point(x+p.x,y+p.y,z+p.z);
	}
	
	public Point add(float ax,float ay,float az) {
		return new Point(x+ax,y+ay,z+az);
	}
	
	
	public float distance2() {
		return x*x+y+z*z;
	}
	
	public float distance() {
		return (float)Math.sqrt(distance2());
	}

	public static Point middle(Point a,Point b) {
		return new Point((a.x+b.x)/2,(a.y+b.y)/2,(a.z+b.z)/2);
	}
	
	public void unitify() {	// ??? yksikkövektoriksi
		float d=distance();
		if (d==0) new Exception("invalid unit vector d=0").printStackTrace();
		x/=d;
		y/=d;
		z/=d;
	}
	
	// names ending with postpositions(?) don't return the result but do it in-place
	
	public void multiplyWith(float f) {
		x*=f;
		y*=f;
		z*=f;
	}

	public void addTo(Point p) {
		x+=p.x;
		y+=p.y;
		z+=p.z;
	}

	public void subFrom(Point p) {
		x-=p.x;
		y-=p.y;
		z-=p.z;
	}

	
	public void rotate(Point rot) {
		float rx=(float)(z*Math.sin(rot.x)+x*Math.cos(rot.x));
		float ry=y;
		float rz=(float)(z*Math.cos(rot.x)-x*Math.sin(rot.x));
		x=rx;
		y=ry;
		z=rz;
	}

	public void negate() {
		x=-x;
		y=-y;
		z=-z;
	}

	public void zero() {
		x=y=z=0;
	}
	
	
}
