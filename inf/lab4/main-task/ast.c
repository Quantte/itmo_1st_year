#include <stdio.h>
#include <stdlib.h>
#include "ast.h"

Day *g_schedule = NULL;


Day *make_day(char *name, Lesson *lessons) {
	Day *d = (Day*)calloc(1, sizeof(Day));
	d->name = name;
	d->lessons = lessons;
	return d;
}

Day *append_day(Day *list, Day *d) {
	if (!list)
		return d;
	Day *current = list;
	while (current->next)
		current = current->next;
	current->next = d;
	return list;
}


Lesson *make_lesson(void) {
	Lesson *l = (Lesson*)calloc(1, sizeof(Lesson));
	return l;
}

Lesson *append_lesson(Lesson *list, Lesson *l) {
	if (!list)
		return l;
	Lesson *current = list;
	while (current->next)
		current = current->next;
	current->next = l;
	return list;
}

void emit_plain(const Day *root, FILE *out) {
	for (const Day *d = root; d; d = d->next) {
		fprintf(out, "DAY %s\n", d->name);
		for (const Lesson *l = d->lessons; l; l = l->next) {
			fprintf(
				out, 
				"LESSON %s|%s|%s|%s|%s ", 
				l->time ? l->time : "None",
		   		l->subject ? l->subject : "None",
		   		l->room ? l->room : "None",
		   		l->type ? l->type : "None",
		   		l->teacher ? l->teacher : "None"
		   );
		}
		fprintf(out, "\n");
	}
}
