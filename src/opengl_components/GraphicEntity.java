package opengl_components;

import java.util.ArrayList;
import java.util.Currency;
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
		Quaternion pc = p.conjugate();
		
		return Quaternion.mult(Quaternion.mult(p, m_LocalRotation), pc);
	}
	
	public Vector3 getEulerAngles() {
		return getRotation().toEulerAngles();
	}
	
	public Vector3 getPosition() {
		Vector3 pPos = m_Parent != null? m_Parent.getPosition() : Vector3.zero();
		return Vector3.add(m_LocalPosition, pPos);
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
		Quaternion pc = p.conjugate();
		
		// invert
		p = p.invert();
		pc = pc.invert();
		
		m_LocalRotation = Quaternion.mult(Quaternion.mult(pc, rotation), p);
		m_bMatrixIsDirty = true;
	}
	
	public void setPosition(Vector3 position) {
		Vector3 pPos = m_Parent != null? m_Parent.getPosition() : Vector3.zero();
		m_LocalPosition = Vector3.subtract(position, pPos);
		m_bMatrixIsDirty = true;
	}
	
	public void setScale(Vector3 scale) {
		Vector3 pSca = m_Parent != null? m_Parent.getScale() : Vector3.one();
		m_LocalScale = new Vector3(scale.x/pSca.x, scale.y/pSca.y, scale.z/pSca.z);
		m_bMatrixIsDirty = true;
	}
	
	// scene graph
	public void setParent(GraphicEntity parent) {
		if(parent == m_Parent) {
			return;
		}
		
		GraphicEntity curParent = m_Parent;
		if(curParent != null) {
			curParent.removeChildren(this);
		}
		m_Parent = parent;
		if(m_Parent != null) {
			m_Parent.addChildren(this);
		}
		m_bMatrixIsDirty = true;
	}
	
	public void removeChildren(GraphicEntity children) {
		if(children == null || !m_Children.contains(children)) {
			return;
		}
		
		m_Children.remove(children);
		children.setParent(null);
	}
	
	public void addChildren(GraphicEntity children) {
		if(children == null || m_Children.contains(children))
	}
}
