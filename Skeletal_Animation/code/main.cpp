#include "./demo/loadshader.hpp"
#include "./demo/animation.hpp"
#include "./demo/camera.hpp"
#include "./demo/mesh.hpp"
#include "./demo/bone.hpp"
#include "./demo/skeleton.hpp"
#include "./demo/modelloader.hpp"
#include "./demo/gameobject.hpp"
#include "./demo/graphics.hpp"

using namespace std;

int main()
{
    Graphics *G = new Graphics(); //creating main class

    G->init(); //initialize setting, load shaders, load models, etc

    G->play(); //main loop

    delete G;

    return 0;
}
