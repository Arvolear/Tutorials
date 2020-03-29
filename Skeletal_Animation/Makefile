.PHONY: all clean cleanall

FLAGS = -std=c++11 -Wall -O3

LIBS = -lGLEW -lGL -lSOIL -lassimp -lglfw3 -lX11 -lpthread -ldl 

INPUTDIR = ./code
OUTPUTDIR = ./build

MAIN = main.o 
DEMO = graphics.o gameobject.o modelloader.o skeleton.o bone.o mesh.o animation.o camera.o loadshader.o

OBJECTFILES = $(addprefix $(OUTPUTDIR)/, $(MAIN) $(DEMO)) 

### ALL ###

all: Demo

Demo: $(OBJECTFILES)
	g++ -o Demo $(OBJECTFILES) $(LIBS) $(FLAGS) 

### MAIN ###

$(OUTPUTDIR)/main.o: $(INPUTDIR)/main.cpp
	g++ -c $(INPUTDIR)/main.cpp -o $@ $(FLAGS)

### DEMO ###

$(OUTPUTDIR)/graphics.o: $(INPUTDIR)/demo/graphics.cpp $(INPUTDIR)/demo/graphics.hpp
	g++ -c $(INPUTDIR)/demo/graphics.cpp -o $@ $(FLAGS)

$(OUTPUTDIR)/gameobject.o: $(INPUTDIR)/demo/gameobject.cpp $(INPUTDIR)/demo/gameobject.hpp
	g++ -c $(INPUTDIR)/demo/gameobject.cpp -o $@ $(FLAGS)

$(OUTPUTDIR)/modelloader.o: $(INPUTDIR)/demo/modelloader.cpp $(INPUTDIR)/demo/modelloader.hpp
	g++ -c $(INPUTDIR)/demo/modelloader.cpp -o $@ $(FLAGS)

$(OUTPUTDIR)/skeleton.o: $(INPUTDIR)/demo/skeleton.cpp $(INPUTDIR)/demo/skeleton.hpp
	g++ -c $(INPUTDIR)/demo/skeleton.cpp -o $@ $(FLAGS)

$(OUTPUTDIR)/bone.o: $(INPUTDIR)/demo/bone.cpp $(INPUTDIR)/demo/bone.hpp
	g++ -c $(INPUTDIR)/demo/bone.cpp -o $@ $(FLAGS)

$(OUTPUTDIR)/mesh.o: $(INPUTDIR)/demo/mesh.cpp $(INPUTDIR)/demo/mesh.hpp
	g++ -c $(INPUTDIR)/demo/mesh.cpp -o $@ $(FLAGS)

$(OUTPUTDIR)/animation.o: $(INPUTDIR)/demo/animation.cpp $(INPUTDIR)/demo/animation.hpp
	g++ -c $(INPUTDIR)/demo/animation.cpp -o $@ $(FLAGS)

$(OUTPUTDIR)/camera.o: $(INPUTDIR)/demo/camera.cpp $(INPUTDIR)/demo/camera.hpp
	g++ -c $(INPUTDIR)/demo/camera.cpp -o $@ $(FLAGS)

$(OUTPUTDIR)/loadshader.o: $(INPUTDIR)/demo/loadshader.cpp $(INPUTDIR)/demo/loadshader.hpp
	g++ -c $(INPUTDIR)/demo/loadshader.cpp -o $@ $(FLAGS)

### CLEAN ###

clean:
	rm -rf $(OUTPUTDIR)/*.o

### CLEANALL ###

cleanall:
	rm -rf Demo
	rm -rf $(OUTPUTDIR)/*.o
