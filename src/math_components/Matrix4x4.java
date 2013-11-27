package math_components;

public class Matrix4x4 {
	float [] m_Matrix;
	
	public Matrix4x4() {
		m_Matrix = new float[16];
	}
	public Matrix4x4(float a00, float a01, float a02, float a03,
					float a10, float a11, float a12, float a13,
					float a20, float a21, float a22, float a23,
					float a30, float a31, float a32, float a33) {
		
		this();
		
		m_Matrix[0] = a00;
		m_Matrix[1] = a01;
		m_Matrix[2] = a02;
		m_Matrix[3] = a03;
		
		m_Matrix[4] = a10;
		m_Matrix[5] = a11;
		m_Matrix[6] = a12;
		m_Matrix[7] = a13;
		
		m_Matrix[8] = a20;
		m_Matrix[9] = a21;
		m_Matrix[10] = a22;
		m_Matrix[11] = a23;
		
		m_Matrix[12] = a30;
		m_Matrix[13] = a31;
		m_Matrix[14] = a32;
		m_Matrix[15] = a33;
		
	}
	
	public float [] matrix() {
		return m_Matrix;
	}
	
	/**
	 * returns the matrix index at given position
	 * @param i row number
	 * @param j column number
	 * @return
	 */
	public int getPosition(int i, int j) {
		return i*4 + j;
	}
	
	/**
	 * returns the matrix component at given position
	 * @param i row number
	 * @param j column number
	 * @return
	 */
	public float get(int i, int j) {
		return m_Matrix[getPosition(i, j)];
	}
	
	public void set(int i, int j, float val) {
		m_Matrix[getPosition(i, j)] = val;
	}
	
	public static Matrix4x4 mult(Matrix4x4 a, Matrix4x4 b) {
		Matrix4x4 res = new Matrix4x4();
		float sum;
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				sum = 0;
				for(int k = 0; k < 4; k++) {
					sum += a.get(i, k)*b.get(k, j);
				}
				res.set(i,j, sum);
			}
		}
		return res;
	}
	
	public static Matrix4x4 transpose(Matrix4x4 m) {
		
		Matrix4x4 res = new Matrix4x4();
		int oi, oj;
		for(int i = 0; i < 4; i+=2) {
			for(int j = 0; j < 4; j+=2) {
				oi = i+1;
				oj = j+1;
				res.set(j,j, m.get(j, i));
				res.set(oj,oj, m.get(oj, oi));
			}
		}
		
		return res;
	}
	
	/**
	 * Creates a multiplied result of R*T
	 * Based on http://www.youtube.com/watch?v=7CxKAtWqHC8#t=210
	 * @param r Quaternion rotation
	 * @param t Translation
	 * @return
	 */
	public static Matrix4x4 createRigidbodyMatrix(Quaternion r, Vector3 t) {
		
		float x = r.x, y = r.y, z = r.z, w = r.w;
		float X = x*x, Y = y*y, Z = z*z;
		
		return new Matrix4x4(1 - 2*Y - 2*Z, 2*x*y - 2*z*w, 2*x*z + 2*y*w, t.x,
							 2*x*y + 2*z*w, 1 - 2*X - 2*Z, 2*y*z - 2*x*w, t.y,
							 2*x*z - 2*y*w, 2*y*z - 2*x*w, 1 - 2*X - 2*Y, t.z,
							 0,0,0,1);
	}
	
	/**
	 * Create a rotation matrix based on the given quaternion
	 * @param r
	 * @return
	 */
	public static Matrix4x4 createRotationMatrix(Quaternion r) {
		return Matrix4x4.createRigidbodyMatrix(r, Vector3.zero());
	}
	
	/**
	 * Create a rotation matrix based on the given euler angles
	 * @param t
	 * @return
	 */
	public static Matrix4x4 createRotationMatrix(Vector3 ea) {
		return Matrix4x4.createRotationMatrix(new Quaternion(ea));
	}
	
	/**
	 * Create a translation matrix based on the given translation vector
	 * @param t
	 * @return
	 */
	public static Matrix4x4 createTranslationMatrix(Vector3 t) {
		return new Matrix4x4(0, 0, 0, t.x,
				 			 0, 0, 0, t.y,
			 				 0, 0, 0, t.z,
			 				 0, 0, 0, 1);
	}
	
	/**
	 * Create a scale magrix based on the given scale vector
	 * @param s
	 * @return
	 */
	public static Matrix4x4 createScaleMatrix(Vector3 s) {
		return new Matrix4x4(s.x, 0,   0,   0,
				 			 0,   s.y, 0,   0,
			 				 0,   0,   s.z, 0,
			 				 0,   0,   0,   1);
	}
	
	public static Matrix4x4 identity() {
		return new Matrix4x4(1,   0,   0,   0,
				 			 0,   1,   0,   0,
			 				 0,   0,   1,   0,
			 				 0,   0,   0,   1);
	}
	
	public static Vector3 multiplyVector3(Matrix4x4 m, Vector3 v) {
		return new Vector3(m.get(0, 0)*v.x + m.get(0, 1)*v.y + m.get(0, 2)*v.z + m.get(0, 3),
							m.get(1, 0)*v.x + m.get(1, 1)*v.y + m.get(1, 2)*v.z + m.get(1, 3),
							m.get(2, 0)*v.x + m.get(2, 1)*v.y + m.get(2, 2)*v.z + m.get(2, 3));		
	}
	
	public static Vector3 multiplyVector3(Vector3 v, Matrix4x4 m) {
		return new Vector3(m.get(0, 0)*v.x + m.get(1, 0)*v.y + m.get(2, 0)*v.z + m.get(3, 0),
							m.get(0, 1)*v.x + m.get(1, 1)*v.y + m.get(2, 1)*v.z + m.get(3, 1),
							m.get(0, 2)*v.x + m.get(1, 2)*v.y + m.get(2, 2)*v.z + m.get(3, 2));		
	}
	
	public void mult(Matrix4x4 m) {
		m_Matrix = Matrix4x4.mult(this, m).matrix();
	}
	
	public Vector3 mult(Vector3 v) {
		return Matrix4x4.multiplyVector3(this, v);
	}
	
	public void set(float []matrix) {
		if(matrix.length > 16) {
			return;
		}
		
		m_Matrix = matrix;
	}
	
	public Matrix4x4 transpose() {
		return Matrix4x4.transpose(this);
	}
	
	public void setRow(int i, Vector3 xyz, float w) {
		set(i,0, xyz.x);
		set(i,1, xyz.y);
		set(i,2, xyz.z);
		set(i,3, w);
	}
	
	public void setRow(int i, float x, Vector3 yzw) {
		set(i,0, x);
		set(i,1, yzw.x);
		set(i,2, yzw.y);
		set(i,3, yzw.z);
	}
	
	public void setColumn(int j, Vector3 xyz, float w) {
		set(0,j, xyz.x);
		set(1,j, xyz.y);
		set(2,j, xyz.z);
		set(3,j, w);
	}
	
	public void setColumn(int j, float x, Vector3 yzw) {
		set(0,j, x);
		set(1,j, yzw.x);
		set(2,j, yzw.y);
		set(3,j, yzw.z);
	}
}
