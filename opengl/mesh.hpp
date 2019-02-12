#pragma once

#include <iostream>
#include <string>
#include <algorithm>

#define GLEW_STATIC
#include <GL/glew.h>
#include <GLFW/glfw3.h>
#include <glm/glm.hpp>

using namespace std;
using namespace glm;

#define BONES_AMOUNT 6 //also check in vertexObjectShader.glsl

struct Vertex
{
    vec3 position;
    vec3 normal;
    vec2 texCoords; //UV

    float boneIDs[BONES_AMOUNT] = {0.0f}; //bones that carry this vertex 
    float weights[BONES_AMOUNT] = {0.0f}; //strength/weigth of the above bones 
};

struct Texture
{
    unsigned int id; //opengl id
    string type; //type diffuse/specular
    string path; //path to the texture
};

class Mesh
{
    private:
        GLuint VAO;
        GLuint VBO, EBO; 

        vector < Vertex > vertices; //vector of vertices this mesh has
        vector < GLuint > indices; //vector of indices for EBO
        vector < Texture > textures; //textures this mesh has
        
        void setupMesh(); //here we generate VAO, VBO, EBO
        
    public:
        Mesh (vector < Vertex > &v, vector < unsigned int > &i, vector < Texture > &t);

        void draw(ShaderLoader *shader); //here we render the mesh to the given shader

        ~Mesh();
};
