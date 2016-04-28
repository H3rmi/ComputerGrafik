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
	private DreieckData punkt0;
	private DreieckData punkt1;
	private DreieckData punkt2;
	private DreieckData[] punkte;
	private Matrix4f rotMat;
	private Matrix4f projMat = new Matrix4f().perspective((float)Math.toRadians(60.0f),aspectRatio,0.01f,1000.f);

	private final Map<String, Integer> uniforms;
	float[] vertices = new float[]{
			-0.5f,0.0f,-0.1f,
			0.0f,0.5f,0.5f,
			0.5f,0.0f,-0.1f,
			0.0f,-0.5f,0.5f
	};
	int[] indices = new int[]{
			0,1,3,
			3,1,2,
			3,0,2,
			0,1,2
	};
	public DreieckUebung() {
		punkt0 = new DreieckData(1.0f,0.0f,0.0f,-0.5f,-0.5f,0.0f);
		punkt1 = new DreieckData(0.0f,1.0f,0.0f,-0.7f,0.7f,0.0f);
		punkt2 = new DreieckData(0.0f,0.0f,1.0f,0.2f,-0.2f,0.0f);
		punkte = new DreieckData[]{punkt0,punkt1,punkt2};
		rotMat = new Matrix4f().rotateX((float)Math.toRadians(0)).rotateY((float)Math.toRadians(45)).rotateZ((float)Math.toRadians(0));

		uniforms = new HashMap<>();
	}
	public DreieckData getPunktData(int i){
		return punkte[i];
	}

	@Override
	public void run() {
		try{
			initWindow();
	        //GL.createCapabilities();
			initBuffers();
			initShaders();

			while (glfwWindowShouldClose(window) == GL_FALSE) {
				//initBuffers();
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
		rotMat = new Matrix4f().rotateAffineXYZ((float)Math.toRadians(punkte[0].green),(float)Math.toRadians(punkte[0].blue),(float)Math.toRadians(punkte[0].red));
		//projMat.mul(rotMat);
		if (resized){
			glViewport(0, 0, WIDTH, HEIGHT);
			resized = false;

		}
		glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);
		rotMat.get(fb);
		glUniformMatrix4fv(uniforms.get("projMat"),false,fb);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glBindVertexArray(VAO);
		//glDrawArrays(GL_TRIANGLES,0,3);
		glDrawElements(GL_TRIANGLES,indices.length, GL_UNSIGNED_INT,0);
	}
	
	private void initBuffers() {
		if(VAO > -1){
			glDeleteBuffers(VAO);
			glDeleteVertexArrays(VBOPosition);
			glDeleteVertexArrays(VBOColor);
		}

		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);
		FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(vertices.length);
		dataBuffer.put(vertices);
		dataBuffer.flip();

		VBOPosition = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, VBOPosition);
		glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_STATIC_DRAW);

		glEnableVertexAttribArray(1); // Position = 1;
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0L);

		VBOIndex = glGenBuffers();
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
		indexBuffer.put(indices);
		indexBuffer.flip();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,VBOIndex);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER,indexBuffer,GL_STATIC_DRAW);
		
		VBOColor = glGenBuffers();
		float[] colors = new float[]{
				punkt0.red, punkt0.green, punkt0.blue, 1.0f,
				punkt1.red, punkt1.green, punkt1.blue, 1.0f,
				punkt2.red, punkt2.green, punkt2.blue, 1.0f,
				1,1,1,1

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
				"void main() { \n"+
				"	gl_Position = projMat * in_pos; \n"+
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
		for (int i = 0; i < punkte.length;i++){
			if (i == cont.getID()){
				punkte[i].red = cont.GetSliderValue(Slider.RED);
				punkte[i].green = cont.GetSliderValue(Slider.XAXIS);
				punkte[i].blue = cont.GetSliderValue(Slider.BLUE);
				punkte[i].x = cont.GetSliderValue(Slider.XPOS);
				punkte[i].y = cont.GetSliderValue(Slider.YPOS);
			}
		}
	}
}
