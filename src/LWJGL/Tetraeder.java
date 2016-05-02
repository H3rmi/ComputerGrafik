package LWJGL;

import org.joml.Matrix4f;

/**
 * Created by Rene on 29.04.2016.
 */
public class Tetraeder extends Model {

    public Tetraeder() {
        super();
        float a = 1.0f;
        float h = (float) ((a/3.0f) * Math.sqrt(6.0f));
        float[] vertices = new float[]{
                -a/2.0f,-h/2.0f, 0.0f,
                 a/2.0f,-h/2.0f, 0.0f,
                   0.0f, h/2.0f,-0.5f,
                   0.0f, h/2.0f, 0.5f
        };
        int[] indices  = new int[]{
                0,1,3,
                3,1,2,
                3,0,2,
                0,1,2
        };
        setIndices(indices);
        setVertices(vertices);
    }
}
