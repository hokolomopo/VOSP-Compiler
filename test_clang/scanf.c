#include <stdio.h>
#include <stdlib.h>

char* inputLine() {
    char* input = malloc(100000 * sizeof (char));
    if (input == NULL) {
        exit(-1);
    }
    scanf("%s", input);
    return input;
}

int main() {
    char* input = inputLine();
    free(input);
    return 0;
}