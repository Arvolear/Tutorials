#pragma once

#include <iostream>
#include <cstdio>
#include <string>
#include <vector>
#include <fstream>
#include <algorithm>
#include <sstream>

#include <cstdlib>
#include <cstring>

#include <GL/glew.h>

#define GLEW_STATIC
#include <GL/glew.h>
#include <GLFW/glfw3.h>

using namespace std;

class ShaderLoader
{
    public:
        unsigned int ID;
        
        ShaderLoader();

        GLuint loadShaders(string vertex_file_path, string fragment_file_path);

        void use();

        ~ShaderLoader();
};
