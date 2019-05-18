#include <stdio.h>
#include <stdlib.h>
 
int main(void)
{ 
    char* hello = malloc(1000*sizeof(char));
    int i = 0;
    int ch;
    while ((ch=getchar()) != '\n') {
        *(hello + i++) = ch;
    }
    printf("%s", hello);
 
    return EXIT_SUCCESS;
}