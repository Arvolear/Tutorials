#include "loadshader.hpp"
#include "camera.hpp"
#include "animation.hpp"
#include "mesh.hpp"
#include "bone.hpp"
#include "skeleton.hpp"
#include "modelloader.hpp"
#include "gameobject.hpp"
#include "graphics.hpp"

using namespace std;
using namespace glm;

Graphics::Graphics()
{
    objectShader = new ShaderLoader();

    camera = new Camera(vec3(5.0f, 0.0f, -15.0f), vec3(0.0f, 0.0f, 1.0f), vec3(0.0f, 1.0f, 0.0f), 1920, 1080, 0.5); //make the camera (position, look direction, up vector, window width, window height, speed)
}

string Graphics::path(string p)
{
    char realPath[PATH_MAX];

    realpath(p.c_str(), realPath); //using limits.h function

    return {realPath};
}

void key_callback(GLFWwindow* window, int key, int scancode, int action, int mods)
{
    if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) //if escape is pressed
    {
        glfwSetWindowShouldClose(window, GL_TRUE); //close app
    }

    if (action == GLFW_PRESS) //if something else was pressed
    {
        static_cast < Graphics* > (glfwGetWindowUserPointer(window))->mark_pressed(key); //here we cast the userwindow pointer we have set in the init function to our Graphics class to call callback function
    }

    if (action == GLFW_RELEASE)
    {
        static_cast < Graphics* > (glfwGetWindowUserPointer(window))->mark_released(key); //the same
    }
}

void mouse_callback(GLFWwindow* window, double posx, double posy)
{
    static_cast < Graphics* > (glfwGetWindowUserPointer(window))->look_around(posx, posy); //the same
}

void Graphics::mark_pressed(int key)
{
    keys[key] = true;
}

void Graphics::mark_released(int key)
{
    keys[key] = false;
}

void Graphics::look_around(double posx, double posy)
{ 
    camera->lookAction(posx, posy); //change camera view
}

void Graphics::key_pressed()
{
    if (keys[GLFW_KEY_W]) //if w is pressed
    {
        camera->moveAction('w'); //move forward
    }

    if (keys[GLFW_KEY_S]) //s
    {
        camera->moveAction('s'); //backward
    }

    if (keys[GLFW_KEY_D]) //d
    {
        camera->moveAction('d'); //right
    }

    if (keys[GLFW_KEY_A]) //a
    {
        camera->moveAction('a'); //left
    }

    if (keys[GLFW_KEY_E]) //e
    {
        camera->moveAction('e'); //up
    }

    if (keys[GLFW_KEY_Q]) //q
    {
        camera->moveAction('q'); //down
    }
}

void Graphics::init()
{
    if (!glfwInit()) //trying to init glfw
    {
        cout << "Failed to initialize glfw" << endl;
        return;
    }

    //glfwWindowHint(GLFW_SAMPLES, 4); //antialiasing
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); //new openGL version 3.*
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3); //new OpenGL version *.3
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); //enable core version


    scene = glfwCreateWindow(1920, 1080, "trueGL", NULL, NULL); //creating window

    if (!scene) //if fail
    {
        glfwTerminate();
        cout << "Failed to initialize window" << endl;
        return;
    }

    glfwSetWindowUserPointer(scene, this); //here we set the userpointer

    glfwMakeContextCurrent(scene); //make current widow active
    glfwSetInputMode(scene, GLFW_CURSOR, GLFW_CURSOR_DISABLED); //turn off the cursor
    glfwSetKeyCallback(scene, key_callback); //enable key callback
    glfwSetCursorPosCallback(scene, mouse_callback); //enable mouse movement callback


    glewExperimental = GL_TRUE; //new version of glew
    if (glewInit() != GLEW_OK) //trying to init glew
    {
        cout << "Failed to initialize GLEW" << endl;
        return;
    }

    glEnable(GL_DEPTH_TEST); //enable depth test
    glEnable(GL_CULL_FACE); //enable back plane cull
    glCullFace(GL_BACK); //here
    //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);



    objectShader->loadShaders(path("./opengl/vertexShader.glsl"), path("./opengl/fragmentShader.glsl")); //loading object shader. There we render our model

    projection = perspective(45.0f, 1.77778f, 0.1f, 100.0f); //create our projection
    
    
    object = new GameObject(); //create model
    
    object->createGraphicsObject(path("animated_models/Caterpillar/caterpillar.fbx")); //get data from file
    //object->createGraphicsObject(path("static_models/Oilbarrel/barrel.obj")); //get data from file
    
    object->applyLocalRotation(180, vec3(1, 0, 0)); //there are some problems with loading fbx files. Models could be rotated or scaled. So we rotate it to the normal state

    object->playAnimation(new Animation("MAIN", vec2(0, 245), 0.34, 10, true)); //forcing our model to play the animation (name, frames, speed, priority, loop)
}

void Graphics::play()
{
    while (!glfwWindowShouldClose(scene))
    {
        glfwGetFramebufferSize(scene, &width, &height); //get window size
        glViewport(0, 0, width, height); //set visible

        glClearColor(0.1f, 0.1f, 0.1f, 1.0f); //clear screen
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); //clear buffers


        glfwPollEvents(); //check if any events were executed

        key_pressed(); //check any pressed keys 

        camera->getView(view); //update view/camera matrix
        camera->update(); //update camera`s location


        objectShader->use(); //use the shader

        glUniformMatrix4fv(glGetUniformLocation(objectShader->ID, "view"), 1, GL_FALSE, value_ptr(view)); //send the view matrix to the shader
        glUniformMatrix4fv(glGetUniformLocation(objectShader->ID, "projection"), 1, GL_FALSE, value_ptr(projection)); //send the projection matrix to the shader
        

        mat4 objectModel; //model matrix
        glUniformMatrix4fv(glGetUniformLocation(objectShader->ID, "model"), 1, GL_FALSE, value_ptr(objectModel)); //send the empty model matrix to the shader

        
        object->render(objectShader); //render our model
        
        glfwSwapBuffers(scene); //swap back and front buffers
    }

    glfwDestroyWindow(scene);

    glfwTerminate();

    return;
}

Graphics::~Graphics()
{
    delete objectShader;
    delete camera;

    delete object;
}
