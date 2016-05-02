package LWJGL;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Created by Rene on 29.04.2016.
 */
public abstract class Model {
    protected float colorR;
    protected float colorG;
    protected float colorB;

    protected Vector3f rotation;
    protected Vector3f position;
    protected float scale;
    protected float[] vertices;
    protected int[] indices;

    protected Matrix4f modelMat = new Matrix4f().identity();

    public Model() {
        position = new Vector3f(0,0,0);
        rotation = new Vector3f(0,0,0);
        scale = 1;
    }

    protected void setVertices(float[] vertices){
        this.vertices = vertices;
    }
    protected void setIndices(int[] indices){
        this.indices = indices;
    }
    public void setPosition(float x, float y, float z){
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }
    public void setScale(float scale){
        this.scale = scale;
    }

    public float getScale(){
        return this.scale;
    }
    public Vector3f getPosition(){
        return this.position;
    }

    public void setRotation(float rotX, float rotY, float rotZ){
        rotation.x = rotX;
        rotation.y = rotY;
        rotation.z = rotZ;
    }
    public Matrix4f getModelMatrix(){
        return modelMat;
    }
    public int[] getIndices() {
        return indices;
    }
    public float[] getVertices() {
        return vertices;
    }
    public Vector3f getRotation(){
        return rotation;
    }
    public float getRotXAngle(){
        return (float)Math.toRadians(rotation.x);
    }
    public float getRotYAngle(){
        return (float)Math.toRadians(rotation.y);
    }
    public float getRotZAngle(){
        return (float)Math.toRadians(rotation.z);
    }
}
