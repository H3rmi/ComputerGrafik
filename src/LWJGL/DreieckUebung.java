package LWJGL;

import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;
import java.util.Observable;
import java.util.Observer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class DreieckUebung implements Runnable,Observer {

	public static void main(String[] args) {
		new DreieckUebung().run();
	}

	private long window;
	private GLFWErrorCallback errorCallback;
	private int WIDTH = 600;
	private int HEIGHT = 600;
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

	public DreieckUebung() {
		punkt0 = new DreieckData(1.0f,0.0f,0.0f,-0.5f,-0.5f,0.0f);
		punkt1 = new DreieckData(0.0f,1.0f,0.0f,-0.7f,0.7f,0.0f);
		punkt2 = new DreieckData(0.0f,0.0f,1.0f,0.2f,-0.2f,0.0f);
		punkte = new DreieckData[]{punkt0,punkt1,punkt2};
	}
	public DreieckData getPunktData(int i){
		return punkte[i];
	}

	@Override
	public void run() {
		try{
			initWindow();
	        GL.createCapabilities();
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
		glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glBindVertexArray(VAO);
		glDrawArrays(GL_TRIANGLES,0,3);
	}
	
	private void initBuffers() {
		if(VAO > -1){
			glDeleteBuffers(VAO);
			glDeleteVertexArrays(VBOPosition);
			glDeleteVertexArrays(VBOColor);
		}
		VAO = glGenVertexArrays();
		glBindVertexArray(VAO);
		VBOPosition = glGenBuffers();
		float[] positions = new float[]{
				punkt0.x, punkt0.y, 0.0f, 1.0f,
				punkt1.x, punkt1.y, 0.0f, 1.0f,
				punkt2.x, punkt2.y, 0.0f, 1.0f
		};
		FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(positions.length);
		dataBuffer.put(positions);
		dataBuffer.flip();
		glBindBuffer(GL_ARRAY_BUFFER, VBOPosition);
		glBufferData(GL_ARRAY_BUFFER, dataBuffer, GL_STATIC_DRAW);
		glEnableVertexAttribArray(1); // Position = 1;
		glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0L);
		
		VBOColor = glGenBuffers();
		float[] colors = new float[]{
				punkt0.red, punkt0.green, punkt0.blue, 1.0f,
				punkt1.red, punkt1.green, punkt1.blue, 1.0f,
				punkt2.red, punkt2.green, punkt2.blue, 1.0f

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
				"void main() { \n"+
				"	gl_Position = in_pos; \n"+
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

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);

		glfwShowWindow(window);
	}

	@Override
	public void update(Observable o, Object arg) {
		SubController cont = (SubController)o;
		for (int i = 0; i < punkte.length;i++){
			if (i == cont.getID()){
				punkte[i].red = cont.GetSliderValue(Slider.RED);
				punkte[i].green = cont.GetSliderValue(Slider.GREEN)/255f;
				punkte[i].blue = cont.GetSliderValue(Slider.BLUE)/255f;
				punkte[i].x = cont.GetSliderValue(Slider.XPOS);
				punkte[i].y = cont.GetSliderValue(Slider.YPOS);
			}
		}
	}
}
