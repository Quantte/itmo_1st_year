#ifndef AST_H
#define AST_H

#include <stdio.h>

typedef struct Lesson {
	char *time;
	char *subject;
	char *room;
	char *type;
	char *teacher;
	struct Lesson *next;
} Lesson;

typedef struct Day {
	char *name;
	Lesson *lessons;
	struct Day *next;
} Day;

extern Day *g_schedule;


Day *make_day(char *name, Lesson *lessons);
Day *append_day(Day *list, Day *d);

Lesson *make_lesson(void);
Lesson *append_lesson(Lesson *list, Lesson *l);


void emit_plain(const Day *root, FILE *out);

#endif
