#pragma once

#include <iostream>
#include <algorithm>
#include <cmath>
#include <thread>

#define GLEW_STATIC
#include <GL/glew.h>
#include <SOIL/SOIL.h>
#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>

using namespace std;
using namespace glm;

class Graphics //our main class
{
    private: 
        bool keys[1024]; //need this to track pressed keys

        int width, height; //size of the window

        ShaderLoader *objectShader; //object that compiles and links shaders

        Camera *camera; //camera class

        GLFWwindow *scene; //our window
   
        GameObject *object; //our suspect model
        GameObject *floor;

        mat4 view; //view/camera matrix 
        mat4 projection; //projection matrix

    public:
        Graphics(); //contructor

        string path(string p); //method to find the path to ... from exe file

        void mark_pressed(int key); //key press callback 
        void mark_released(int key); //key release callback
        void look_around(double posx, double posy); //mouse movement callback

        void key_pressed(); //here we track the pressed keys

        void init(); //initialize settings, load shaders, load models

        void play(); //main loop

        ~Graphics(); //destructor
};
