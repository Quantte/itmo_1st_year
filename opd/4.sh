chmod u+r lab0/trapinch0/breloom
chmod u+r lab0/mightyena0/

echo "Рекурсивно подсчитать количество символов содержимого файлов из директории lab0, имя которых начинается на 'b', отсортировать вывод по уменьшению количества, добавить вывод ошибок доступа в стандартный поток вывода"
wc -m $(grep -rl "" lab0 2>&1 | grep "/b") | grep -v "total" | sort -nr

echo "Вывести рекурсивно список имен файлов в директории trapinch0, список отсортировать по имени z->a, ошибки доступа не подавлять и не перенаправлять"
ls -prR lab0/trapinch0/ | grep -v -e ":$" -e "/$" -e "^$"

chmod u+r lab0/shellos1
echo "Рекурсивно вывести содержимое файлов из директории lab0, имя которых заканчивается на 't', строки отсортировать по имени z->a, добавить вывод ошибок доступа в стандартный поток вывода"
cat $(grep -rl "" lab0 2>&1 | grep "t$") 2>&1 | sort -r

echo "Вывести содержимое файла dragonair5 с номерами строк, исключить строки, заканчивающиеся на 'y', регистр символов игнорировать, ошибки доступа перенаправить в файл в директории /tmp"
cat -b lab0/dragonair5 2>/tmp/errors | grep -i -v "y$"

chmod u+r lab0/shellos1/magmortar

echo "Вывести содержимое файлов в директории shellos1, оставить только строки, содержащие "ch", ошибки доступа перенаправить в файл в директории /tmp"
cat $(grep -rl "" lab0/shellos1/ 2>/tmp/errors) 2>/tmp/errors | grep "ch"

chmod u+r lab0/mightyena0/camerupt
chmod u+r lab0/mightyena0/duskull
echo "Вывести содержимое файлов в директории mightyena0, строки отсортировать по имени z->a, добавить вывод ошибок доступа в стандартный поток вывода"
cat $(grep -rl "" lab0/mightyena0/ 2>&1) 2>&1 | sort -r

chmod u-r lab0/trapinch0/breloom
chmod u-r lab0/mightyena0/
chmod u-r lab0/shellos1
chmod u-r lab0/shellos1/magmortar
chmod u-r lab0/mightyena0/camerupt
chmod u-r lab0/mightyena0/duskull
