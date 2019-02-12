#include "loadshader.hpp"
#include "animation.hpp"
#include "camera.hpp"
#include "mesh.hpp"
#include "bone.hpp"
#include "skeleton.hpp"
#include "modelloader.hpp"
#include "gameobject.hpp"
#include "graphics.hpp"

using namespace std;

int main()
{
    Graphics *G = new Graphics(); //creating main class

    G->init(); //initialize setting, load shaders, load models, etc

    G->play(); //main loop

    delete G;

    return 0;
}
