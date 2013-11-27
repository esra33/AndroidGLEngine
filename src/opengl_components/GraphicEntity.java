package opengl_components;

import java.util.ArrayList;
import java.util.List;

import math_components.Matrix4x4;
import math_components.Quaternion;
import math_components.Vector3;

public class GraphicEntity {

	public interface Scriptable {
		public void Update();
		public void Start();
	}
	
	// Transformations
	Vector3 m_LocalPosition;
	Vector3 m_LocalScale;
	Quaternion m_LocalRotation;
	
	// Scene graph Transformation matrix
	Matrix4x4 m_AccumulatedMatrix;
	boolean m_bMatrixIsDirty;
	
	// Scene graph hierarchy
	boolean m_bLocked = false;
	GraphicEntity m_Parent;
	List<GraphicEntity> m_Children;
	
	// "Game" Logic/Scripts
	List<Scriptable> m_Scripts;
	
	public GraphicEntity() {
		m_LocalPosition = Vector3.zero();
		m_LocalScale = Vector3.one();
		m_LocalRotation = Quaternion.identity();
		m_bMatrixIsDirty = true;
		
		m_Parent = null;
		m_Children = new ArrayList<GraphicEntity>();
		m_Scripts = new ArrayList<GraphicEntity.Scriptable>();
	}
	
	/**
	 * recalculates the value of the acumulated matrix based on changes to the parent
	 * or to the properties
	 */
	protected void recalculateMatrix() {
		final Matrix4x4 rigidbodyMatrix = Matrix4x4.createRigidbodyMatrix(m_LocalRotation, m_LocalPosition);
		final Matrix4x4 scaleMatrix = Matrix4x4.createScaleMatrix(m_LocalScale);
		final Matrix4x4 parentMatrix = m_Parent != null? m_Parent.getWorldTransformationMatrix() : Matrix4x4.identity();
		
		m_AccumulatedMatrix = Matrix4x4.mult(Matrix4x4.mult(scaleMatrix, rigidbodyMatrix), parentMatrix);
		m_bMatrixIsDirty = false;
	}
	
	/**
	 * returns a deep copy of the world transformation matrix
	 * @return
	 */
	public Matrix4x4 getWorldTransformationMatrix() {
		if(m_bMatrixIsDirty) {
			recalculateMatrix();
		}
		
		Matrix4x4 copy = new Matrix4x4();
		copy.set(m_AccumulatedMatrix.matrix());
		return copy;
	}
	
	// Local gets
	public Vector3 getLocalEulerAngles() {
		return m_LocalRotation.toEulerAngles();
	}
	
	public Quaternion getLoacalRotation() {
		return m_LocalRotation;
	}
	
	public Vector3 getLocalPosition() {
		return m_LocalPosition;
	}
	
	public Vector3 getLocalScale() {
		return m_LocalScale;
	}
	
	// Local sets
	public void setLocalEulerAngles(Vector3 eulerAngles) {
		m_LocalRotation = new Quaternion(eulerAngles);
		m_bMatrixIsDirty = true;
	}
	
	public void setLocalRotation(Quaternion rotation) {
		m_LocalRotation = rotation;
		m_bMatrixIsDirty = true;
	}
	
	public void setLocalPosition(Vector3 position) {
		m_LocalPosition = position;
		m_bMatrixIsDirty = true;
	}
	
	public void setLocalScale(Vector3 scale) {
		m_LocalScale = scale;
		m_bMatrixIsDirty = true;
	}
	
	// get as global components
	public Quaternion getRotation() {
		Quaternion p = m_Parent != null? m_Parent.getRotation() : Quaternion.identity();
		return Quaternion.mult(p, m_LocalRotation);
	}
	
	public Vector3 getEulerAngles() {
		return getRotation().toEulerAngles();
	}
	
	public Vector3 getPosition() {
		Matrix4x4 pTransf = m_Parent != null? m_Parent.getWorldTransformationMatrix() : Matrix4x4.identity();
		return Matrix4x4.multiplyVector3(pTransf, m_LocalPosition);
	}
	
	public Vector3 getScale() {
		Vector3 pSca = m_Parent != null? m_Parent.getScale() : Vector3.one();
		return new Vector3(m_LocalScale.x*pSca.x, m_LocalScale.y*pSca.y, m_LocalScale.z*pSca.z);
	}
	
	// set as global components 
	// TODO Andres WARNING!! these are buggy as hell I still need to do the math for these
	public void setEulerAngles(Vector3 eulerAngles) {
		setRotation(new Quaternion(eulerAngles));
	}
	
	// I really don't know if this is the right math, I need to double check this but it kinda makes sense
	// dunno though
	public void setRotation(Quaternion rotation) {
		Quaternion p = m_Parent != null? m_Parent.getRotation() : Quaternion.identity();
		
		// invert
		p = p.invert();
		
		m_LocalRotation = Quaternion.mult(p,rotation);
		m_bMatrixIsDirty = true;
	}
	
	public void setPosition(Vector3 position) {
		
		final Matrix4x4 invTransl;
		final Matrix4x4 invRotation;
		final Matrix4x4 invScale;
		
		if(m_Parent != null) {
			invTransl = new Matrix4x4();
			invTransl.setColumn(3, Vector3.scale(-1, m_Parent.getPosition()), 1);
			
			invRotation = Matrix4x4.transpose(Matrix4x4.createRotationMatrix(m_Parent.getRotation()));
			
			Vector3 scal = m_Parent.getScale();
			invScale = Matrix4x4.createScaleMatrix(new Vector3(1/scal.x, 1/scal.y, 1/scal.z));
		} else {
			invTransl = Matrix4x4.identity();
			invRotation = Matrix4x4.identity();
			invScale = Matrix4x4.identity();
		}
		
		m_LocalPosition = Matrix4x4.multiplyVector3(Matrix4x4.mult(invScale, Matrix4x4.mult(invRotation, invTransl)), position);		
		m_bMatrixIsDirty = true;
	}
	
	public void setScale(Vector3 scale) {
		Vector3 pSca = m_Parent != null? m_Parent.getScale() : Vector3.one();
		m_LocalScale = new Vector3(scale.x/pSca.x, scale.y/pSca.y, scale.z/pSca.z);
		m_bMatrixIsDirty = true;
	}
	
	// scene graph
	public synchronized void setParent(GraphicEntity parent) {
		if(parent == m_Parent || m_bLocked) {
			return;
		}
		m_bLocked = true;
		
		GraphicEntity curParent = m_Parent;
		if(curParent != null) {
			curParent.removeChildren(this);
		}
		m_Parent = parent;
		if(m_Parent != null) {
			m_Parent.addChildren(this);
		}
		m_bMatrixIsDirty = true;
		m_bLocked = false;
	}
	
	public void removeChildren(GraphicEntity children) {
		if(children == null || !m_Children.contains(children)) {
			return;
		}
		
		m_Children.remove(children);
		children.setParent(null);
		m_bLocked = false;
	}
	
	public void addChildren(GraphicEntity children) {
		if(children == null || m_Children.contains(children)) {
			return;
		}
		
		m_Children.remove(children);
		children.setParent(this);
	}
	
	// Scripts management
	public void addScript(Scriptable newScript) {
		if(newScript == null || m_Scripts.contains(newScript)) {
			return;
		}
		
		m_Scripts.add(newScript);
	}
	
	public Scriptable getScriptOfType(Class<?> cls) {
		
		for(Scriptable s : m_Scripts) {
			if(cls.equals(s)) {
				return s;
			}
		}
		
		return null;
	}
	
	public void removeScript(Scriptable s) {
		if(s == null || !m_Scripts.contains(s)) {
			return;
		}
		
		m_Scripts.remove(s);
	}
	
	public void start() {
		for(Scriptable s : m_Scripts) {
			s.Start();
		}
	}
	
	public void update() {
		for(Scriptable s : m_Scripts) {
			s.Update();
		}
	}
}
