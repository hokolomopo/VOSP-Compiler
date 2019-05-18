#include <stdlib.h>

int main() {
	char* test = malloc(1000*sizeof(char));
	char* test2 = malloc(1000*sizeof(char));
	int i;
	for (i = 0; i < 1000; i++) {
		if (*(test + i) == *(test + i));
	}
	return 0;
}