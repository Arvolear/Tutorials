#include "camera.hpp"

using namespace std;
using namespace glm;

Camera::Camera(const vec3 &cameraPos, const vec3 &cameraForward, const vec3 &cameraUp, double windowWidth, double windowHeight, double speed)
{
    sensitivityX = 2 / windowWidth; //calculate sensitivity
    sensitivityY = (2 / (windowWidth / windowHeight)) / windowHeight;

    Pos = cameraPos; //set position
    moveDirection = vec3(0, 0, 0); //set move direction
    Forward = cameraForward; //set forward
    Up = cameraUp; //set up

    Left = normalize(cross(Up, Forward)); //set left

    Speed = vec3(speed); //set speed

    //set previous coords at the screen center
    prevx = windowWidth / 2.0;
    prevy = windowHeight / 2.0;

    //set window size
    width = windowWidth;
    height = windowHeight;
}

void Camera::lookAction(double posx, double posy)
{
    double xoffset = posx - prevx; //amount of X pixels mouse moved
    double yoffset = posy - prevy; //amount of Y pixels mouse moved

    //multiply it by sensitivity
    xoffset *= sensitivityX; 
    yoffset *= sensitivityY;


    mat4 camView; //temporate matrix

    Forward = mat3(rotate(camView, GLfloat(-xoffset), normalize(Up))) * Forward; //here we calculate the rotation matrix and multiply it by a Forward vector
        
    Left = normalize(cross(Up, Forward)); //calculate left vector
        
    Forward = mat3(rotate(camView, GLfloat(yoffset), normalize(Left))) * Forward; //here we calculate the rotation matrix and multiply it by a Forward vector
        
    Forward = normalize(Forward); //normalize forward

    prevx = posx; //reset previous mouse coords
    prevy = posy;
}
        
void Camera::moveAction(char direction)
{
    switch(direction)
    {
        case 'w': //if w is pressed
            {
                moveDirection += Forward; 
                break;
            }

        case 's':
            {
                moveDirection -= Forward;
                break;
            }

        case 'd':
            {
                moveDirection -= Left;
                break;
            }
        
        case 'a':
            {
                moveDirection += Left;
                break;
            }

        case 'e':
            {
                moveDirection += Up;
                break;
            }

        case 'q':
            {
                moveDirection -= Up;
                break;
            }
    }
}

vec3 Camera::getPosition()
{
    return Pos;
}

vec3 Camera::getForward()
{
    return Forward;
}

vec3 Camera::getLeft()
{
    return Left;
}

vec3 Camera::getUp()
{
    return Up;
}
        
mat4 Camera::getView(mat4 &view)
{
    view = lookAt(Pos, Pos + Forward, Up); //using glm function to calculate view matrix

    return view;
}

void Camera::update()
{
    if (moveDirection != vec3(0, 0, 0)) //if move direction is not 0 0 0
    {
        Pos += normalize(moveDirection) * Speed; //calculate new position
    }

    moveDirection = vec3(0, 0, 0); //move direction set to 0 
}

Camera::~Camera(){}
