#include <stdio.h>
#include<stdlib.h>

// A Child must be able to masquerade as a Parent
typedef struct {
struct ChildVTable *_vtable; // Virtual function table first
int inheritedField; // Parent fields, in the same order as parent!
int newField; // And, finally, Child's new fields
} Child;

// A ChildVTable must be able to masquerade as a ParentVTable
struct ChildVTable {
 // First, parent methods in the same order
 char * (*inheritedMethod)(Child *); // Why not Parent * here?
 void (*overriddenMethod)(int);
 // Then child's new methods
 char * (*newMethod)(Child *, int);
 };

 // Child VTable can mix inherited, overridden and new methods
 //struct ChildVTable ChildVTable_inst {
 // Necessary (but legit) cast for inherited method
 //.inheritedMethod = (void (*)(Child *)) Parent_inheritedMethod;
 //.overriddenMethod = Child_overriddenMethod;
 //.newMethod = Child_newMethod;
 //}

char * newMethod(Child *self, int x) { return "Someone"; }
char * inheritedMethod(Child *self) { return "Someone"; }
void overriddenMethod(int x){printf("Hey\n");};

int main()
{
     Child* x = malloc(sizeof(Child));
     x->_vtable = malloc(sizeof(struct ChildVTable));
     x->_vtable->newMethod = &newMethod;
     char* s = x->_vtable->newMethod(x, 3);

     printf("%s\n", s);


    x->_vtable->overriddenMethod = &overriddenMethod;
    x->_vtable->overriddenMethod(2);


    return 0;
}
