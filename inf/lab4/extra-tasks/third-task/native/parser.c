#include <stdio.h>
#include "ast.h"
#include "hcl.tab.h"


extern FILE *yyin;
extern int yyparse(void);

Day * parse_hcl_file(const char *filename) {
	yyin = fopen(filename, "r");
	if (!yyin) {
		perror("fopen");
		return NULL;
	}

	g_schedule = NULL;
	if (yyparse() != 0) {
		fclose(yyin);
		return NULL;
	}
	fclose(yyin);
	return g_schedule;
}
