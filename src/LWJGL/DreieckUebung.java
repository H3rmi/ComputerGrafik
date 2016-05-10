package LWJGL;


import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class DreieckUebung implements Runnable,Observer {

	private int VBOIndex;
	private boolean resized;

	public static void main(String[] args) {
		new DreieckUebung().run();
	}

	private long window;
	private GLFWErrorCallback errorCallback;
	private GLFWWindowSizeCallback windowSizeCallback;
	private int WIDTH = 600;
	private int HEIGHT = 600;
	float aspectRatio = (float)WIDTH / (float)HEIGHT;
	private int program;
	private int vertexShader;
	private int fragmentShader;
	private int VAO = 1;
	private int VBOPosition;
	private int VBOColor;

	private Matrix4f rotMat;
	private Matrix4f modelMat;
	private Matrix4f projMat = new Matrix4f().perspective((float)Math.toRadians(60.0f),aspectRatio,0.01f,1000.f);
	private Tetraeder tetraeder;
	private final Map<String, Integer> uniforms;

	public DreieckUebung() {
		tetraeder = new Tetraeder();
		tetraeder.setPosition(0,0,-3.0f);
		rotMat = new Matrix4f().identity();
		modelMat = new Matrix4f();
		uniforms = new HashMap<>();
	}


	@Override
	public void run() {
		try{
			initWindow();
			initBuffers();
			initShaders();
			while (glfwWindowShouldClose(window) == GL_FALSE) {
				frame();
				glfwSwapBuffers(window); 
                glfwPollEvents();
			}
		}
		finally {
			glfwTerminate();
			errorCallback.release();
		}
	}

	private void frame() {
		modelMat.identity().translate(tetraeder.getPosition()).
				rotateX(tetraeder.getRotXAngle()).
				rotateY(tetraeder.getRotYAngle()).
				rotateZ(tetraeder.getRotZAngle()).scale(tetraeder.getScale());

		if (resized){
			glViewport(0, 0, WIDTH, HEIGHT);
			resized = false;
		}
		glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		projMat.get(fb);
		glUniformMatrix4fv(uniforms.get("projMat"),false,fb);

		fb = BufferUtils.createFloatBuffer(16);
		modelMat.get(fb);
		glUniformMatrix4fv(uniforms.get("modelMat"),false,fb);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glBindVertexArray(VAO);
		glDrawElements(GL_TRIANGLES,tetraeder.getIndices().length, GL_UNSIGNED_INT,0);
	}
	
	private void initBuffers() {
		if(VAO > -1){
			glDeleteBuffers(VAO);
			glDeleteVertexArrays(VBOPosition);
			glDeleteVertexArrays(VBOColor);
		}

		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);
		FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(tetraeder.getVertices().length);
		dataBuffer.put(tetraeder.getVertices());
		dataBuffer.flip();

		VBOPosition = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, VBOPosition);
		glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_STATIC_DRAW);

		glEnableVertexAttribArray(1); // Position = 1;
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0L);

		VBOIndex = glGenBuffers();
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(tetraeder.getIndices().length);
		indexBuffer.put(tetraeder.getIndices());
		indexBuffer.flip();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,VBOIndex);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER,indexBuffer,GL_STATIC_DRAW);
		
		VBOColor = glGenBuffers();
		float[] colors = new float[]{
				1.0f, 0.0f, 0.0f, 1.0f,
				0.0f, 1.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f
		};
		dataBuffer = BufferUtils.createFloatBuffer(colors.length);
		dataBuffer.put(colors);
		dataBuffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, VBOColor);
		glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_STATIC_DRAW);
		glEnableVertexAttribArray(2); // Position = 2;
		glVertexAttribPointer(2, 4, GL_FLOAT, false, 0, 0L);
	}

	private void initShaders() {
		String vertexShader=
				"#version 330 \n"+
				"in vec4 in_pos; \n"+
				"in vec4 in_color; \n"+
				"out vec4 var_color; \n"+
				"uniform mat4 projMat;\n"+
				"uniform mat4 modelMat;\n"+
				"void main() { \n"+
				"	gl_Position = projMat * modelMat* in_pos; \n"+
				"	var_color = in_color; \n"+				
				"}";
		String fragmentShader =
				"#version 330 \n"+
				"in vec4 var_color; \n"+
				"out vec4 out_color; \n"+
				"void main() { \n"+
				"	out_color = var_color; \n"+
				"} \n";
		this.vertexShader = CreateShader(GL_VERTEX_SHADER,vertexShader);
		this.fragmentShader = CreateShader(GL_FRAGMENT_SHADER,fragmentShader);
		program = glCreateProgram();
		glAttachShader(program,this.vertexShader);
		glAttachShader(program,this.fragmentShader);
		glBindAttribLocation(program,1,"in_pos");
		glBindAttribLocation(program,2,"in_color");
		glBindFragDataLocation(program,0,"out_color");
		
		
		glLinkProgram(program);
		int uniformLoc = glGetUniformLocation(program,"projMat");
		uniforms.put("projMat",uniformLoc);
		uniformLoc = glGetUniformLocation(program,"modelMat");
		uniforms.put("modelMat",uniformLoc);

		glUseProgram(program);
	}
	private int CreateShader(int shaderType, String shaderSource){
		int shader = glCreateShader(shaderType);
		glShaderSource(shader, shaderSource);
		glCompileShader(shader);
		int status = glGetShaderi(shader,GL_COMPILE_STATUS);
		if(status== GL_FALSE){
			String error = glGetShaderInfoLog(shader);
			String shaderString = null;
			switch(shaderType){
			case GL_VERTEX_SHADER: shaderString = "vertex";break;
			case GL_FRAGMENT_SHADER: shaderString ="fragment";break;
			}
			System.err.println("Compile error in " +shaderString+":\n"+error);
		}
		return shader;
	}

	private void initWindow() {
		System.out.println("LWJGL version: " + Version.getVersion());
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback
				.createPrint(System.err));

		if (glfwInit() != GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

		window = glfwCreateWindow(WIDTH, HEIGHT, "Demo", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetWindowSizeCallback(window, windowSizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				WIDTH = width;
				HEIGHT = height;
				setResized(true);
			}
		});
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);

		glfwShowWindow(window);
		GL.createCapabilities();
		glEnable(GL_DEPTH_TEST);
	}
	public void setResized(boolean resized) {
		this.resized = resized;
	}

	@Override
	public void update(Observable o, Object arg) {
		SubController cont = (SubController)o;
		tetraeder.setRotation(cont.GetSliderValue(Slider.X_AXIS),cont.GetSliderValue(Slider.Y_AXIS),cont.GetSliderValue(Slider.Z_AXIS));
	}

}
