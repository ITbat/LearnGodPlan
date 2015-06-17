//A.11  用虚拟跟踪球实现立方体旋转程序  
/* Rotating cube demo with trackball */  
#include <math.h>  
//#include <GL/glut.h>  
#include<stdlib.h>
#include<iostream>
#include<vector>
#include "lighting.cpp"
#define bool int  
#define false 0  
#define true 1  
#define M_PI 3.14

using namespace std;

int  winWidth, winHeight;  
float angle = 0.0, axis[3];
vector<float> recovA;
vector<float> recov[3];
float moveX = 0, moveY = 0, moveZ = 0;
bool drawingSurfaces = true;
bool translate = false;
bool rotation = false;
/* Draw the dodecahedron */  
GLfloat vertices[][3] = {  
    {1.214124,0,1.589309}, {0.375185,1.154701,1.589309}, {-0.982247,0.713644,1.589309},  
	{-0.982247,-0.713644,1.589309}, {0.375185,-1.154701,1.589309}, {1.964494,0,0.375185},    
    {0.607062,1.868345,0.375185}, {-1.589309,1.154701,0.375185}, {-1.589309,-1.154701,0.375185},
	{0.607062,-1.868345,0.375185}, {1.589309,1.154701,-0.375185}, {-0.607062,1.868345,-0.375185},
	{-1.964494,0,-0.375185}, {-0.607062,-1.868345,-0.375185}, {1.589309,-1.154701,-0.375185},
	{0.982247,0.713644,-1.589309}, {-0.375185,1.154701,-1.589309}, {-1.214124,0,-1.589309},
	{-0.375185,-1.154701,-1.589309}, {0.982247,-0.713644,-1.589309}
};  
GLfloat colors[][3] = {  
    {1.0,1.0,1.0}, {1.0,1.0,1.0}, {1.0,1.0,1.0}, {1.0,1.0,1.0}, 
	{1.0,1.0,1.0}, {1.0,1.0,1.0}, {1.0,1.0,1.0}, {1.0,1.0,1.0}, 
	{1.0,1.0,1.0}, {1.0,1.0,1.0}, {1.0,1.0,1.0}, {1.0,1.0,1.0}
};  
void polygon(int a, int b, int c , int d, int e, int face)  
{  
    /* draw a polygon via list of vertices */
	if (drawingSurfaces)
		glBegin(GL_POLYGON); 
	else
		glBegin(GL_LINE_STRIP);
    glColor3fv(colors[face]);  
    glVertex3fv(vertices[a]);  
    glVertex3fv(vertices[b]);  
    glVertex3fv(vertices[c]);  
    glVertex3fv(vertices[d]); 
	glVertex3fv(vertices[e]);
    glEnd();  
}  
void displayBall(void)  
{  
    /* map vertices to faces */  
    polygon(0,1,2,3,4,0);
	polygon(0,5,10,6,1,1);
	polygon(1,6,11,7,2,2);
	polygon(2,7,12,8,3,3);
	polygon(3,8,13,9,4,4);
	polygon(4,9,14,5,0,5);
	polygon(15,10,5,14,19,6);
	polygon(16,11,6,10,15,7);
	polygon(17,12,7,11,16,8);
	polygon(18,13,8,12,17,9);
	polygon(19,14,9,13,18,10);
	polygon(19,18,17,16,15,11);
}  
/*
void display(void)  
{  
    glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
	
    /* view transform 
    if (translate)  
    {  
		if (recovA.empty())
			glTranslatef(moveX, moveY, 0);
		else {
			// Change the ordinate to the original one
			for (int i = recovA.size() - 1; i >= 0; i--) {
				glRotatef(-recovA[i], recov[0][i], recov[1][i], recov[2][i]);
			}
			glTranslatef(moveX, moveY, 0.0);
			for (int i = 0; i < recovA.size(); i++) {
				glRotatef(recovA[i], recov[0][i], recov[1][i], recov[2][i]);
			}
		}
	} else if (rotation) {
	    glRotatef(angle, axis[0], axis[1], axis[2]);
	}
	displayBall();
	glFlush();
    glutSwapBuffers();  
}*/

void display(void) {
	glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
	glColor3fv(colors[0]);
	//glBegin();
		glutSolidSphere(2, 50, 50);
	//glEnd();
	glFlush();
    glutSwapBuffers(); 
}

void processMenuEvents(int option) {
	switch (option) {
		case 0 : drawingSurfaces = true;
				 display();
			     break;
		case 1 : drawingSurfaces = false;
				 display();
			     break;
	}
}

// If the window's size is changed
void myReshape(int w, int h) {
	int dis = w < h ? w : h;
	glViewport(0, 0, dis, dis);
	winWidth = w;
	winHeight = h;
}

float lastPos[3] = {0.0, 0.0, 0.0};
float lastPosT[3] = {0.0, 0.0, 0.0};
int startX, startY, curX, curY;
void trackball_ptov(int x, int y, int w, int h, float last[3]) {
	float d, a;
	last[0] = (2.0f * x - w) / w;
	last[1] = (h - 2.0f * y) / h;
	d = (float) sqrt(last[0] * last[0] + last[1] * last[1]);
	last[2] = (float) cos((M_PI / 2.0f) * ((d < 1.0f) ? d : 1.0f));
	a = 1.0f / (float) sqrt(last[0] * last[0] + last[1] * last[1] + last[2] * last[2]);
	last[0] *= a;
	last[1] *= a;
	last[2] *= a;
}

void startMotion(int x, int y) {
	startX = x;
	startY = y;
	curX = x;
	curY = y;
	if (translate)
		trackball_ptov(x, y, winWidth, winHeight, lastPosT);
	else
		trackball_ptov(x, y, winWidth, winHeight, lastPos);
}

void mouseButton(int button, int state, int x, int y) {
	if (GLUT_RIGHT_BUTTON == button) switch(state) {
	    case GLUT_DOWN :
			y = winHeight - y;
			translate = true;
			startMotion(x, y);
			break;
		case GLUT_UP :
			translate = false;
			break;
	} else if (GLUT_LEFT_BUTTON == button) switch(state) {
		case GLUT_DOWN : 
			y = winHeight - y;
			rotation = true;
			startMotion(x, y);
			break;
		case GLUT_UP : 
			rotation = false;
			break;
	}
}

void mouseMotion(int x, int y) {
	float cur[3];
	trackball_ptov(x, y, winWidth, winHeight, cur);

	if (translate) {
		moveX = cur[0] * 3 - lastPos[0] * 3;
		moveY = cur[1] * 3 - lastPos[1] * 3;
		moveZ = cur[2] * 3 - lastPos[2] * 3;
		lastPos[0] = cur[0];
		lastPos[1] = cur[1];
		lastPos[2] = cur[2];
	} else if (rotation) {
	    moveX = lastPos[0]-cur[0];
		moveY = lastPos[1]-cur[1];
		moveZ = lastPos[2]-cur[2];
		if (moveX || moveY || moveZ) {
		    angle = 90.f * sqrt(moveX * moveX + moveY * moveY + moveZ * moveZ);
			axis[0] = lastPos[1]*cur[2] - lastPos[2]*cur[1];  
            axis[1] = lastPos[2]*cur[0] - lastPos[0]*cur[2];  
            axis[2] = lastPos[0]*cur[1] - lastPos[1]*cur[0];
			recovA.push_back(angle);
			recov[0].push_back(axis[0]);
			recov[1].push_back(axis[1]);
			recov[2].push_back(axis[2]);
		}
		lastPos[0] = cur[0];
		lastPos[1] = cur[1];
		lastPos[2] = cur[2];
	}
	
	display();
}

int main(int argc, char **argv)  
{  
    glutInit(&argc, argv);  
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);  
    glutInitWindowSize(500, 500);
  
    glutCreateWindow("Virtual Ball");  
    glutReshapeFunc(myReshape); 

	glScalef(0.3f, 0.3f, 0.3f);
    glutDisplayFunc(display);  
    //glutIdleFunc(spinCube);  
    //glutMouseFunc(mouseButton);  
    //glutMotionFunc(mouseMotion);  
    //glEnable(GL_DEPTH_TEST);  
    glMatrixMode(GL_PROJECTION);  
    glLoadIdentity();  
    glOrtho(-2.0, 2.0, -2.0, 2.0, -2.0, 2.0);  
    glMatrixMode(GL_MODELVIEW);

	//gluLookAt(0.1212, 0.0, 3.0, 0.1212, 0.0, 0.0, 0.0, 1.0, 0.0);

	int menu = glutCreateMenu(processMenuEvents);
	glutAddMenuEntry("Surfaces", 0);
	glutAddMenuEntry("Frames", 1);
	glutAttachMenu(GLUT_MIDDLE_BUTTON);

	lighting();

    glutMainLoop();  
}  
