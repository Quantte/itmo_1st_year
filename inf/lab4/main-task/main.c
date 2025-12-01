#include <stdio.h>
#include "ast.h"

int yyparse(void);
extern FILE *yyin;
extern Day *g_schedule;

int main(int argc, char **argv) {
	if (argc > 1) {
		yyin = fopen(argv[1], "r");
		if (!yyin) {
			perror("fopen");
			return 1;
		}
	} else 
		yyin = stdin;

	if (yyparse() != 0) {
		fprintf(stderr, "parsing error\n");
		return 1;
	}

	emit_plain(g_schedule, stdout);
	return 0;
}
