package math_components;

public class Vector3 {

	public static int X = 0;
	public static int Y = 1;
	public static int Z = 2;
	
	public float x;
	public float y;
	public float z;
	
	public Vector3(float x, float y, float z) {
		set(x,y,z);
	}
	
	// Basic startups
	public static Vector3 right(){return new Vector3(1,0,0);}
	public static Vector3 up(){return new Vector3(0,1,0);}
	public static Vector3 forward(){return new Vector3(0,0,1);}
	public static Vector3 zero(){return new Vector3(0,0,0);}
	public static Vector3 one(){return new Vector3(1,1,1);}
	
	// Basic arithmetic functions external
	public static Vector3 add(Vector3 a, Vector3 b){return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);}
	public static Vector3 subtract(Vector3 a, Vector3 b){return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);}
	public static float dot(Vector3 a, Vector3 b){return a.x*b.x+a.y*b.y+a.z*b.z;}
	public static Vector3 cross(Vector3 a, Vector3 b){return new Vector3(a.y*b.z, a.z*b.x, a.x*b.y);}
	public static Vector3 normalize(Vector3 a) {
		float lenght = a.lenght();
		return new Vector3(a.x/lenght, a.y/lenght, a.z/lenght);
	}
	public static Vector3 scale(float scale, Vector3 a) {return new Vector3(a.x*scale, a.y*scale, a.z*scale);}
	public static float distance(Vector3 a, Vector3 b) {
		float x = b.x - a.x;
		float y = b.y - a.y;
		float z = b.z - a.z;
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
	
	public static Vector3 projectAonB(Vector3 a, Vector3 b) {
		float dot = Vector3.dot(a,b);
		float magU = b.lenght();

		dot /= magU;

		return Vector3.scale(dot, b);
	}
	
	// Matrix Multiplication functions
	
	// Basic arithmetic functions internal
	public float lenght() {
		return (float) Math.sqrt(x*x + y*y + z*z);
	}
	
	public void add(Vector3 b) {
		x += b.x;
		y += b.y;
		z += b.z;
	}
	
	public void subtract(Vector3 b) {
		x -= b.x;
		y -= b.y;
		z -= b.z;
	}
	
	public void scale(float scale) {
		x*=scale;
		y*=scale;
		z*=scale;
	}
	
	public float dot(Vector3 b) {
		return Vector3.distance(this, b);
	}
	
	public Vector3 cross(Vector3 b) {
		return Vector3.cross(this, b);
	}
	
	public Vector3 normalize() {
		return Vector3.normalize(this);
	}
	
	public float distance(Vector3 b) {
		return Vector3.distance(this, b);
	}
	
	public Vector3 projectOnB(Vector3 b) {
		return Vector3.projectAonB(this, b);
	}
	
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(Vector3 v) {
		float [] va = v.xyz();
		set(va[X], va[Y], va[Z]);
	}
	
	public float[] xyz() {
		float [] values = {x,y,z}; 
		return values;
	}
	
	public void mult(Matrix4x4 m) {
		set(Matrix4x4.multiplyVector3(this, m));
	}
}
