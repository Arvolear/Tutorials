#include "loadshader.hpp"
#include "animation.hpp"
#include "mesh.hpp"
#include "bone.hpp"
#include "skeleton.hpp"
#include "modelloader.hpp"
#include "gameobject.hpp"

GameObject::GameObject()
{
    modelLoader = new ModelLoader();

    localTransform = mat4(1.0);
}

void GameObject::createGraphicsObject(string path)
{
    modelLoader->loadModel(path); //load the model from the file
        
    modelLoader->getModelData(skeleton, meshes); //get the loaded data and store it in this class
}

void GameObject::playAnimation(Animation* anim, bool reset)
{
    skeleton->playAnimation(anim, reset); //play animation
}

void GameObject::stopAnimation()
{
    skeleton->stopAnimation(); //stop animation
}

void GameObject::applyLocalRotation(float angle, vec3 axis)
{
    vec3 sc; //scaling
    quat rot; //rotation
    vec3 tran; //translation
    vec3 skew; //skew
    vec4 proj; //projection

    decompose(localTransform, sc, rot, tran, skew, proj); //decomposing localTransform into parts to tweak

    //constructing back the tweaked localTransform
    localTransform = mat4(1.0); 
    localTransform *= scale(sc);
    localTransform *= translate(tran);
    localTransform *= rotate(radians(angle), axis);
}

void GameObject::render(ShaderLoader* shader)
{
    glUniformMatrix4fv(glGetUniformLocation(shader->ID, "localTransform"), 1, GL_FALSE, value_ptr(localTransform));

    skeleton->update(shader); //rendering the skeleton part

    for (int i = 0; i < meshes.size(); i++) //loop through the meshes
    {
        meshes[i]->draw(shader); //rendering the mesh part
    }
}

GameObject::~GameObject()
{
    delete modelLoader;

    for (int i = 0; i < meshes.size(); i++)
    {
        delete meshes[i];
    }

    delete skeleton;
}
