%{
#include "ast.h"
#include "hcl.tab.h"
#include <string.h>
#include <stdlib.h>

int yywrap(void) {
	return 1;
}

%}


%%

"day" {return DAY;}
"lesson" {return KW_LESSON;}
"time" {return TIME;}
"subject" {return SUBJECT;}
"room" {return ROOM;}
"type" {return KW_TYPE;}
"teacher" {return TEACHER;}

"{" {return LBRACE;}
"}" {return RBRACE;}
"=" {return EQUAL;}

\"(?:[^\"\\]|\\.)*\"  {
    int len = yyleng - 2;
    char *s = (char*)malloc(len + 1);
    memcpy(s, yytext + 1, len);
    s[len] = '\0';
    yylval.str = s;
    return STRING;
}

\s+ {}
. {}

%%
