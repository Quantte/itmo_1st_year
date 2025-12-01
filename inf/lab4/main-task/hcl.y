%{
#include <stdio.h>
#include <stdlib.h>
#include "ast.h"

int yylex(void);
void yyerror(const char *s);

extern Day *g_schedule;


%}

%union {
	char *str;
	Day *day;
	Lesson *lesson;
}


%token DAY KW_LESSON
%token TIME SUBJECT ROOM KW_TYPE TEACHER
%token LBRACE RBRACE EQUAL
%token <str> STRING

%type <day> schedule days day_block
%type <lesson> lessons lesson_block lesson_fields

%%
schedule:
	days {g_schedule = $1;}
	;

days:
		{$$ = NULL;}
	| days day_block {$$ = append_day($1, $2);}
	;

day_block:
	DAY STRING LBRACE lessons RBRACE
		{$$ = make_day($2, $4);}
	;

lessons:
		{$$ = NULL;}
	| lessons lesson_block {$$ = append_lesson($1, $2);}
	;

lesson_block:
	KW_LESSON LBRACE lesson_fields RBRACE
		{$$ = $3;}
	;

lesson_fields:
      /* empty */                   
        { $$ = make_lesson(); }

    | TIME EQUAL STRING lesson_fields
        {
            $$ = $4;
            $$->time = $3;
        }

    | SUBJECT EQUAL STRING lesson_fields
        {
            $$ = $4;
            $$->subject = $3;
        }

    | ROOM EQUAL STRING lesson_fields
        {
            $$ = $4;
            $$->room = $3;
        }

    | KW_TYPE EQUAL STRING lesson_fields
        {
            $$ = $4;
            $$->type = $3;
        }

    | TEACHER EQUAL STRING lesson_fields
        {
            $$ = $4;
            $$->teacher = $3;
        }
    ;

%%



void yyerror(const char *s) {
	fprintf(stderr, "Parse failed: %s\n", s);
}


