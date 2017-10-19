package org.n070k3n.ogl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.system.MemoryUtil;
import org.n070k3n.ogl.util.TextureLoader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Main
{
    private static Logger logger = LogManager.getLogger(Main.class);
    
    private static long winId;
    private static int programId;
    private static int vboId;
    private static int vaoId;
    private static int eboId;
    private static int texId;
    
    private static int testUniId;
    
    public static void main(String[] args)
    {
        initGLFW();
        initGL();
        initShader();
        initMesh();
        initTexture();
        while(!glfwWindowShouldClose(winId))
            loop();
        cleanUp();
    }
    
    private static void initGLFW()
    {
        if(!glfwInit())
        {
            logger.error("Couldn't init GLFW");
            System.exit(-1);
        }
        glfwWindowHint(GLFW_SAMPLES, 8);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        winId = glfwCreateWindow(1600, 900, "OGL", MemoryUtil.NULL, MemoryUtil.NULL);
        if(winId == 0)
        {
            logger.error("Couldn't create GLFWWindow");
            glfwTerminate();
            System.exit(-1);
        }
        glfwSwapInterval(1);
        glfwMakeContextCurrent(winId);
        glfwSetWindowSizeCallback(winId, (win, w, h) ->glViewport(0,0, w, h));
    }
    
    private static void initGL()
    {
        createCapabilities();
        glClearColor(0,0,0,1);
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }
    
    private static void initShader()
    {
        int vsId = glCreateShader(GL_VERTEX_SHADER);
        try
        {
            glShaderSource(vsId, new String(Files.readAllBytes(Paths.get(Main.class.getClassLoader().getResource("vertex.glsl").toURI()))));
        }
        catch(IOException|URISyntaxException e)
        {
            logger.error(e);
            glDeleteShader(vsId);
            cleanUpGL();
            cleanUpGLFW();
            System.exit(-1);
        }
        glCompileShader(vsId);
        if(glGetShaderi(vsId, GL_COMPILE_STATUS) == GL_FALSE)
        {
            logger.error("Vertex shader compilation error");
            logger.error(glGetShaderInfoLog(vsId));
            glDeleteShader(vsId);
            cleanUpGL();
            cleanUpGLFW();
            System.exit(-1);
        }
    
        int fsId = glCreateShader(GL_FRAGMENT_SHADER);
        try
        {
            glShaderSource(fsId, new String(Files.readAllBytes(Paths.get(Main.class.getClassLoader().getResource("fragment.glsl").toURI()))));
        }
        catch(IOException|URISyntaxException e)
        {
            logger.error(e);
            glDeleteShader(fsId);
            cleanUpGL();
            cleanUpGLFW();
            System.exit(-1);
        }
        glCompileShader(fsId);
        if(glGetShaderi(fsId, GL_COMPILE_STATUS) == GL_FALSE)
        {
            logger.error("Fragment shader compilation error");
            logger.error(glGetShaderInfoLog(fsId));
            glDeleteShader(vsId);
            glDeleteShader(fsId);
            cleanUpGL();
            cleanUpGLFW();
            System.exit(-1);
        }
    
        programId = glCreateProgram();
        glAttachShader(programId, vsId);
        glAttachShader(programId, fsId);
        glDeleteShader(vsId);
        glDeleteShader(fsId);
        glLinkProgram(programId);
        if(glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE)
        {
            logger.error("Shader program linking error");
            logger.error(glGetProgramInfoLog(programId));
            glDeleteProgram(programId);
            cleanUpGL();
            cleanUpGLFW();
            System.exit(-1);
        }
        glValidateProgram(programId);
        if(glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE)
        {
            logger.error("Shader program validation error");
            logger.error(glGetProgramInfoLog(programId));
            glDeleteProgram(programId);
            cleanUpGL();
            cleanUpGLFW();
            System.exit(-1);
        }
        
        glUseProgram(programId);
        testUniId = glGetUniformLocation(programId,"test");
        glUseProgram(0);
    }
    
    private static void initMesh()
    {
        float[] vertices =
        {
             0.5f,  0.5f, 0.0f, 1, 0, 0, 1, 1,
             0.5f, -0.5f, 0.0f, 0, 1, 0, 1, 0,
            -0.5f, -0.5f, 0.0f, 0, 0, 1, 0, 0,
            -0.5f,  0.5f, 0.0f, 1, 0, 1, 0, 1
        };
        int[] indices =
        {
            0, 1, 3,
            1, 2, 3
        };
        
        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();
        eboId = glGenBuffers();
    
        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);
        glBindVertexArray(0);
    }
    
    public static void initTexture()
    {
        texId = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        try
        {
            TextureLoader.Image img = TextureLoader.load("texture.png");
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img.width, img.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, img.data);
        }
        catch(IOException e)
        {
            logger.error(e);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, 0);
            glDeleteTextures(texId);
            cleanUpMesh();
            cleanUpShader();
            cleanUpGL();
            cleanUpGLFW();
            System.exit(-1);
        }
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    private static void loop()
    {
        glfwPollEvents();
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glUseProgram(programId);
        glBindVertexArray(vaoId);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texId);
        glUniform1f(testUniId, (float)(Math.sin(glfwGetTime())/2.0+0.5));
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT,0);
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindVertexArray(0);
        glUseProgram(0);
        
        glfwSwapBuffers(winId);
    }
    
    private static void cleanUp()
    {
        cleanUpTexture();
        cleanUpMesh();
        cleanUpShader();
        cleanUpGL();
        cleanUpGLFW();
    }
    
    private static void cleanUpTexture()
    {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);
        glDeleteTextures(texId);
    }
    
    private static void cleanUpMesh()
    {
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
        glDeleteBuffers(eboId);
        glDeleteBuffers(vboId);
    }
    
    private static void cleanUpShader()
    {
        glUseProgram(0);
        glDeleteProgram(programId);
    }
    
    private static void cleanUpGL()
    {
    }
    
    private static void cleanUpGLFW()
    {
        glfwDestroyWindow(winId);
        Callbacks.glfwFreeCallbacks(winId);
        glfwTerminate();
    }
}