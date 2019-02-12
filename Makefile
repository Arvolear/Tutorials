trueGL: main.o graphics.o gameobject.o modelloader.o skeleton.o bone.o mesh.o animation.o camera.o loadshader.o
	g++ -o trueGL main.o graphics.o gameobject.o modelloader.o skeleton.o bone.o mesh.o animation.o camera.o loadshader.o -lGLEW -lGL -lGLU -lSOIL -lassimp -lglfw3 -lX11 -lXxf86vm -lXrandr -lpthread -lXi -ldl -lXinerama -lXcursor -std=c++14 -O3



main.o: opengl/main.cpp
	g++ -c opengl/main.cpp -std=c++14

graphics.o: opengl/graphics.cpp opengl/graphics.hpp
	g++ -c opengl/graphics.cpp -std=c++14

gameobject.o: opengl/gameobject.cpp opengl/gameobject.hpp
	g++ -c opengl/gameobject.cpp -std=c++14

modelloader.o: opengl/modelloader.cpp opengl/modelloader.hpp
	g++ -c opengl/modelloader.cpp -std=c++14

skeleton.o: opengl/skeleton.cpp opengl/skeleton.hpp opengl/convert.hpp
	g++ -c opengl/skeleton.cpp -std=c++14

bone.o: opengl/bone.cpp opengl/bone.hpp
	g++ -c opengl/bone.cpp -std=c++14

mesh.o: opengl/mesh.cpp opengl/mesh.hpp
	g++ -c opengl/mesh.cpp -std=c++14

animation.o: opengl/animation.cpp opengl/animation.hpp
	g++ -c opengl/animation.cpp -std=c++14

camera.o: opengl/camera.cpp opengl/camera.hpp
	g++ -c opengl/camera.cpp -std=c++14

loadshader.o: opengl/loadshader.cpp opengl/loadshader.hpp
	g++ -c opengl/loadshader.cpp -std=c++14



clean:
	rm -rf trueGL
	rm -rf *.o
