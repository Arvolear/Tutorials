#pragma once

#include <iostream>
#include <vector>

#define GLEW_STATIC
#include <GL/glew.h>
#include <GLFW/glfw3.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>

using namespace std;
using namespace glm;

class Camera
{
    private:
        double width; //window width
        double height; //window height

        double prevx; //previous mouse position
        double prevy; 

        double sensitivityX; //sensitivity on X coordinates
        double sensitivityY; //sensitivity on Y coordinates

        vec3 Speed; //movement speed

        vec3 Pos; //camera position

        vec3 moveDirection; //camera movement direction
        vec3 Forward; //camera forward vector
        vec3 Up; //camera up vector
        vec3 Left; //camera left vector

    public:
        Camera(const vec3 &cameraPos, const vec3 &cameraForward, const vec3 &cameraUp, double windowWidth, double windowHeight, double speed = 5);

        void lookAction(double posx, double posy); //call this when mouse is moved
        void moveAction(char direction); //call this when keys are pressed

        vec3 getPosition();
        vec3 getForward();
        vec3 getLeft();
        vec3 getUp();

        mat4 getView(mat4 &view); //get view matrix

        void update(); //update camera`s position

        ~Camera();
};
