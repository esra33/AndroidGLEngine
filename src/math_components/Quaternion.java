package math_components;

public class Quaternion {

	public static Quaternion identity(){return new Quaternion(0,0,0,1);}
	
	public float x;
	public float y;
	public float z;
	public float w;
	
	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	public Quaternion(Vector3 axis, float angle) {
		
		// normalize jic
		axis = axis.normalize();
		
		float s = (float) Math.sin(angle * 0.5f);
		x = axis.x * s;
		y = axis.y * s;
		z = axis.z * s;
		w = (float) Math.cos(angle * 0.5f);
	}
	public Quaternion(Vector3 eulerAngles) {
		
		float a = (float)(Math.toRadians(eulerAngles.z))*0.5f;
		float b = (float)(Math.toRadians(eulerAngles.x))*0.5f;
		float c = (float)(Math.toRadians(eulerAngles.y))*0.5f;
		
		float cosa = (float)Math.cos(a), sena = (float)Math.sin(a);
		float cosb = (float)Math.cos(b), senb = (float)Math.sin(b);
		float cosc = (float)Math.cos(c), senc = (float)Math.sin(c);
		
		x = sena*cosb*cosc - cosa*senb*senc;
		y = cosa*senb*cosc + sena*cosb*senc;
		z = cosa*cosb*senc - sena*senb*cosc;
		w = cosa*cosb*cosc + sena*senb*senc;
	}
	
	public static Quaternion mult(Quaternion q1, Quaternion q2) {
		/* Math formula
		a1 + b1i + c1j + d1k and a2 + b2i + c2j + d2k,
			 a_1a_2 - b_1b_2 - c_1c_2 - d_1d_2
		{}+ (a_1b_2 + b_1a_2 + c_1d_2 - d_1c_2)i
		{}+ (a_1c_2 - b_1d_2 + c_1a_2 + d_1b_2)j
		{}+ (a_1d_2 + b_1c_2 - c_1b_2 + d_1a_2)k.
		*/
		return new Quaternion(
				q1.w*q2.x + q1.x*q2.w + q1.y*q2.z - q1.z*q2.y,		// x
				q1.w*q2.y - q1.x*q2.z + q1.y*q2.w + q1.z*q2.x,		// y
				q1.w*q2.z + q1.x*q2.y - q1.y*q2.x + q1.z*q2.w,		// z
				q1.w*q2.w - q1.x*q2.x - q1.y*q2.y - q1.z*q2.z);		// r
	}
	public static Quaternion add(Quaternion q1, Quaternion q2) {return new Quaternion(q1.x+q2.x, q1.y+q2.y, q1.z+q2.z, q1.w+q2.w);}
	public static Quaternion normalize(Quaternion q) {
		float size = q.lenght();
		return new Quaternion(q.x/size,q.y/size,q.z/size,q.w/size);
	}
	public static Quaternion conjugate(Quaternion q) {
		return new Quaternion(-q.x, -q.y, -q.z, q.w);
	}
	public static float dot(Quaternion q1, Quaternion q2) {return q1.x*q2.x + q1.y*q2.y + q1.z*q2.z;}
	public static Quaternion scale(float scale, Quaternion q) {return new Quaternion(q.x*scale, q.y*scale, q.z*scale, q.w*scale);}
	public static Quaternion invert(Quaternion q) {
		Quaternion conjugated = q.conjugate();
		conjugated.scale(1/(q.lenght()*q.lenght()));
		return conjugated;
	}
	
	// Euler angles manipulation
	public static Vector3 toEulerAngles(Quaternion q) {
		float q0 = q.w, 
			q1 = q.x,
			q2 = q.y,
			q3 = q.z;

		return new Vector3((float)Math.toDegrees(Math.asin (2*(q0*q2 + q3*q1))),
							(float)Math.toDegrees(Math.atan2(2*(q0*q3 + q1*q2), 1 - 2*(q2*q2 + q3*q3))),
							(float)Math.toDegrees(Math.atan2(2*(q0*q1 + q2*q3), 1 - 2*(q1*q1 + q2*q2))));
	}
	public static Vector3 rotateVectorByQuaternion(Vector3 base, Quaternion q) {
		Quaternion qOut = new Quaternion(base.x, base.y, base.z, 0);
		return Quaternion.mult(Quaternion.mult(q.conjugate(), qOut), q).getImaginary();
	}
	public static Quaternion angleBetweenVectors(Vector3 v, Vector3 u) {
		// Check for parallels
		float dotP, sangle;
		Vector3 normV1, normV2, vNormal;

		normV1 = v.normalize();
		normV2 = u.normalize();
				
		dotP = normV1.dot(normV2);
		sangle = (float)Math.acos(dotP);
		
		vNormal = normV2.cross(normV1).normalize();
		return new Quaternion(vNormal, sangle);
	}
	
	public float lenght() {
		return (float)Math.sqrt(w*w+x*x+y*y+z*z);
	}
	
	public void mult(Quaternion q2){
		Quaternion q1 = this;
		float _x, _y, _z, _r;
		_x = q1.w*q2.x + q1.x*q2.w + q1.y*q2.z - q1.z*q2.y;
		_y = q1.w*q2.y - q1.x*q2.z + q1.y*q2.w + q1.z*q2.x;
		_z = q1.w*q2.z + q1.x*q2.y - q1.y*q2.x + q1.z*q2.w;
		_r = q1.w*q2.w - q1.x*q2.x - q1.y*q2.y - q1.z*q2.z;
		
		this.x = _x;
		this.y = _y;
		this.z = _z;
		this.w = _r;	
	}
	
	public void add(Quaternion q2) {
		x+=q2.x;
		y+=q2.y;
		z+=q2.z;
		w+=q2.w;
	}
	
	public Quaternion normalize() {
		return Quaternion.normalize(this);
	}
	
	public Quaternion conjugate() {
		return Quaternion.conjugate(this);
	}
	
	public float dot(Quaternion q2) {
		return Quaternion.dot(this, q2);
	}
	
	public void scale(float scale) {
		x*=scale;
		y*=scale;
		z*=scale;
		w*=scale;
	}
	
	public Quaternion invert() {
		return Quaternion.invert(this);
	}
	
	public float getReal() { // no seriously get real dude
		return w;
	}
	
	public Vector3 getImaginary() { // how do you even do that?
		return new Vector3(x,y,z);
	}
	
	public Vector3 toEulerAngles() {
		return Quaternion.toEulerAngles(this);
	}
	
	public Vector3 rotateVector(Vector3 base) {
		return Quaternion.rotateVectorByQuaternion(base, this);
	}
}
