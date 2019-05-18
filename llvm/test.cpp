#include <iostream>

using namespace std;


class Box {
   public:
      double height;

      void setHeight( double hei );
};

void Box::setHeight( double hei ) {
	double myHeight = height;
   height = hei;
}

class B2 :public Box{
   public:
      void setHeight( double hei );

};

void B2::setHeight( double hei ) {
   height = 3;
}

// Main function for the program
int main() {
   Box Box1;                // Declare Box1 of type Box
   Box Box2;                // Declare Box2 of type Box
   Box1.setHeight(1);

   return 0;
}
