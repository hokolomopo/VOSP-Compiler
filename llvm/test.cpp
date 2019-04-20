#include <iostream>

using namespace std;

class Box {
   public:
      double length;         // Length of a box
      double breadth;        // Breadth of a box
      double height;         // Height of a box

      // Member functions declaration
      double getVolume(void);
      Box setLength( double len , Box a);
      void setBreadth( double bre );
      void setHeight( double hei );
};

// Member functions definitions
double Box::getVolume(void) {
   return length * breadth * height;
}

Box Box::setLength( double len, Box a) {
   length = len;
   return *this;
}

void Box::setBreadth( double bre ) {
   bre += 5 * 2 - 1;
   breadth = bre;
}
void Box::setHeight( double hei ) {
	double myHeight = height;
   height = hei;
}

// Main function for the program
int main() {
   Box Box1;                // Declare Box1 of type Box
   Box Box2;                // Declare Box2 of type Box
   double volume = 0.0;     // Store the volume of a box here
   double height1 = 21.3;
	char str1[]="Sample string";
    Box b4 = Box();

   // box 1 specification
   Box b3 = Box1.setLength(6.0, Box1); 
   Box1.setBreadth(7.0); 
   Box1.setHeight(height1);

   if(b3.getVolume() > height1)
   		volume = 22.2;
   	else
   		volume = 87.56;

   // box 2 specification
   Box2.setLength(12.0, b3); 
   Box2.setBreadth(13.0); 
   Box2.setHeight(10.0);

   // volume of box 1
   volume = Box1.getVolume();
   cout << "lol" << volume <<endl;

   // volume of box 2
   volume = Box2.getVolume();
   cout << "Volume of Box2 : " << volume <<endl;
   return 0;
}
