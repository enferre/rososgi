OS := $(shell uname -s)
ARCH := $(shell uname -m)

CXX := g++
JAVAH := ../generated/jni-headers
JAVA_INCLUDE := $(JAVA_HOME)/include
JDKPLATFORMINCLUDESUBDIR := linux
JAVA_PLATFORMINCLUDE := $(JAVA_INCLUDE)/$(JDKPLATFORMINCLUDESUBDIR)

HEADERS := $(wildcard *.h)
SRC := $(wildcard *.cpp)
INCLUDES := -Ilibfranka/common/include -Ilibfranka/include -I$(JAVA_INCLUDE) -I$(JAVA_PLATFORMINCLUDE) -I$(JAVAH) 
OBJECTS := $(SRC:.cpp=.o)
FLAGS := -std=c++11
CCFLAGS := -O3 -w -fPIC
LINKS := -L. -lfranka -lPocoNet -lPocoFoundation
LIB := libpanda.so
DEPLOY_DIR := ../native/$(OS)/$(ARCH)

all: install

libfranka.a: 
	cd libfranka; \
	mkdir build; \
	cd build; \
	cmake ..; \
	make franka; \
	cp libfranka.a ../..

compile: $(SRC) libfranka.a
	$(CXX) $(FLAGS) $(CCFLAGS) $(INCLUDES) -c $(SRC)

link: compile
	$(CXX) -shared $(FLAGS) $(CCFLAGS) $(OBJECTS) $(LINKS) -o $(LIB)

install: link
	mkdir -p $(DEPLOY_DIR); \
	cp $(LIB) $(DEPLOY_DIR)/$(LIB)

clean:
	rm -rf *.o $(LIB) be_*.h
	
cleanall: clean
	rm -rf libfranka/build libfranka.a

