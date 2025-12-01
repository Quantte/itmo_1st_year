#include <stdlib.h>
#include <string.h>
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


void free_schedule(Day *d) {
    while (d) {
        Day *nextd = d->next;
        free(d->name);
        Lesson *l = d->lessons;
        while (l) {
            Lesson *nextl = l->next;
            free(l->time);
            free(l->subject);
            free(l->room);
            free(l->type);
            free(l->teacher);
            free(l);
            l = nextl;
        }
        free(d);
        d = nextd;
    }
}
