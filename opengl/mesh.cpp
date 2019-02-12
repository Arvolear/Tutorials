#include "loadshader.hpp"
#include "mesh.hpp"

using namespace std;
using namespace glm;

Mesh::Mesh (vector < Vertex > &v, vector < unsigned int > &i, vector < Texture > &t)
{
    vertices = v; //copy vertex data
    indices = i; //copy index data
    textures = t; //copy textures data

    setupMesh(); //call setup...bad stuff...
}

void Mesh::setupMesh()
{
    glGenVertexArrays(1, &VAO); //generate VAO
    glGenBuffers(1, &VBO); //generate VBO
    glGenBuffers(1, &EBO); //generate EBO

    glBindVertexArray(VAO); //bind VAO

    glBindBuffer(GL_ARRAY_BUFFER, VBO); //bind VBO
    glBufferData(GL_ARRAY_BUFFER, sizeof(Vertex) * vertices.size(), &vertices[0], GL_STATIC_DRAW); //insert vertex data into VBO

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO); //bind EBO
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(unsigned int) * indices.size(), &indices[0], GL_STATIC_DRAW); //insert index data into EBO

    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)0);
    glEnableVertexAttribArray(0); //0 layout for position

    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)offsetof(Vertex, normal));
    glEnableVertexAttribArray(1); //1 layout for normal

    glVertexAttribPointer(2, 2, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)offsetof(Vertex, texCoords));
    glEnableVertexAttribArray(2); //2 layout for UV
   
    for (int i = 0; i < BONES_AMOUNT; i++)
    {
        glVertexAttribPointer(3 + i, 1, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)(offsetof(Vertex, boneIDs) + sizeof(float) * i));
        glEnableVertexAttribArray(3 + i); //from 3 to 9 (BONES_AMOUNT) layouts for bones ids. Array in shader uses N layouts, equal to the size, instead of single layout location the vec uses
    }
    
    for (int i = 0; i < BONES_AMOUNT; i++)
    {
        glVertexAttribPointer(9 + i, 1, GL_FLOAT, GL_FALSE, sizeof(Vertex), (void*)(offsetof(Vertex, weights) + sizeof(float) * i));
        glEnableVertexAttribArray(9 + i); //from 9 to 15 for the weights
    }

    glBindVertexArray(0); //unbind 
}

void Mesh::draw(ShaderLoader *shader)
{
    unsigned int diffuseNR = 1; //amount of diffuse textures
    unsigned int specularNR = 1; //amount of specular textures

    for (int i = 0; i < textures.size(); i++) //loop through textures
    {
        glActiveTexture(GL_TEXTURE0 + i); //set the texture active

        string number; //this one is needed if we use more than one texture of the same type
        
        if (textures[i].type == "texture_diffuse") //if it is a diffuse texture
        {
            number = to_string(diffuseNR); //this is the diffuseNR`s texture
            diffuseNR++; //amount of diffuse + 1
        }
        else if (textures[i].type == "texture_specular")
        {
            number = to_string(specularNR); //this is the specularNR`s texture
            specularNR++; //amount of specular + 1
        }

        glUniform1i(glGetUniformLocation(shader->ID, ("material." + textures[i].type + number).c_str()), i); //send the texture to the shader (example: material.texture_diffuse1)
        glBindTexture(GL_TEXTURE_2D, textures[i].id); //bind this texture
    }

    glBindVertexArray(VAO); //bind VAO
    glDrawElements(GL_TRIANGLES, indices.size(), GL_UNSIGNED_INT, 0); //draw mesh from indices
    
    glBindVertexArray(0); //unbind VAO
    glBindTexture(GL_TEXTURE_2D, 0); //unbind textures
}

Mesh::~Mesh(){}
