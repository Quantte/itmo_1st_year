#set page(
  margin: 2cm,
  paper: "a4",  
)

#set text(lang: "ru")
#set text(lang: "eng")
#set text(size: 14pt)

#align(center)[
 Федеральное государственное автономное образовательное учреждение высшего образования\
 «Национальный исследовательский университет ИТМО»
]



#align(center)[
  #set text(style: "italic")
  Факультет программной инженерии и компьютерной техники
]

#v(4cm)

#align(center)[
  #set text(size: 18pt)

*Лабораторная работа №1\
по дисциплине\ «Основы профессиональной деятельности»*\
Вариант: 1650
]

#v(4cm)

#align(right)[
Выполнил:\ 
Герасюто Фадей Александрович \
Группа: Р3119 \
Преподаватель:\
Остапенко Ольга Денисовна
]
#v(6cm)

#align(center)[Санкт-Петербург, 2025]

#pagebreak()

#outline(title: "Содержание")
#pagebreak()

#[
  #set text(size: 16pt)
  = Задание
]
#set text(size: 10pt)

1. Создать приведенное в варианте дерево каталогов и файлов с содержимым. В качестве корня дерева использовать каталог lab0 своего домашнего каталога. Для создания и навигации по дереву использовать команды: mkdir, echo, cat, touch, ls, pwd, cd, more, cp, rm, rmdir, mv.


#align(left)[
  #figure(
    image(".typ_pic/tree.png", height: 50%)
  )
]

2. Установить согласно заданию права на файлы и каталоги при помощи команды chmod, используя различные способы указания прав.

  -dragonair5: владелец должен читать и записывать файл; группа-владелец должна не иметь никаких прав; остальные пользователи должны читать файл\
  -mightyena0: -wxrwxr-x
  -arcanine: владелец должен читать, записывать директорию и переходить в нее; группа-владелец должна записывать директорию и переходить в нее; остальные пользователи должны читать, записывать директорию и переходить в нее\
  -camerupt: -wxrwx-wx\
  -cradily: владелец должен читать директорию и переходить в нее; группа-владелец должна только переходить в директорию; остальные пользователи должны записывать директорию и переходить в нее\
  -duskull: права 307\
  -shellos1: права 315\
  -butterfree: rwxrwxrwx\
  -pupitar: права 355\
  -bellossom: -wx-wxr-x\
  -bagon: права 700\
  -shuppet: владелец должен читать и записывать файл; группа-владелец должна записывать файл; остальные пользователи должны не иметь никаких прав\
  -magmortar: права 305\
  -slugma6: права 660\
  -teddiursa5: права 666\
  -trapinch0: права 755\
  -victreebel: права 550\
  -breloom: права 006\
  -vanillish: владелец должен читать директорию и переходить в нее; группа-владелец должна читать, записывать директорию и переходить в нее; остальные пользователи должны записывать директорию и переходить в нее\
  -cofagrigus: владелец должен читать файл; группа-владелец должна читать файл; остальные пользователи должны не иметь никаких прав\
  
3. Скопировать часть дерева и создать ссылки внутри дерева согласно заданию при помощи команд cp и ln, а также комманды cat и перенаправления ввода-вывода.

  -cоздать жесткую ссылку для файла slugma6 с именем lab0/shellos1/shuppetslugma\
  -скопировать файл teddiursa5 в директорию lab0/mightyena0/cradily\
  -cоздать символическую ссылку для файла teddiursa5 с именем lab0/shellos1/shuppetteddiursa\
  -создать символическую ссылку c именем "Copy_65" на директорию trapinch0 в каталоге lab0\
  -скопировать содержимое файла slugma6 в новый файл lab0/shellos1/shuppetslugma\
  -объеденить содержимое файлов lab0/shellos1/shuppet, lab0/shellos1/shuppet, в новый файл lab0/teddiursa5_58\
  -скопировать рекурсивно директорию mightyena0 в директорию lab0/shellos1/magmortar\
  
4. Используя команды cat, wc, ls, head, tail, echo, sort, grep выполнить в соответствии с вариантом задания поиск и фильтрацию файлов, каталогов и содержащихся в них данных.

  -Рекурсивно подсчитать количество символов содержимого файлов из директории lab0, имя которых начинается на 'b', отсортировать вывод по уменьшению количества, добавить вывод ошибок доступа в стандартный поток вывода\
  -Вывести рекурсивно список имен файлов в директории trapinch0, список отсортировать по имени z->a, ошибки доступа не подавлять и не перенаправлять\
  -Рекурсивно вывести содержимое файлов из директории lab0, имя которых заканчивается на 't', строки отсортировать по имени z->a, добавить вывод ошибок доступа в стандартный поток вывода\
  -Вывести содержимое файла dragonair5 с номерами строк, исключить строки, заканчивающиеся на 'y', регистр символов игнорировать, ошибки доступа перенаправить в файл в директории /tmp\
  -Вывести содержимое файлов в директории shellos1, оставить только строки, содержащие "ch", ошибки доступа перенаправить в файл в директории /tmp\
  -Вывести содержимое файлов в директории mightyena0, строки отсортировать по имени z->a, добавить вывод ошибок доступа в стандартный поток вывода\
  
5. Выполнить удаление файлов и каталогов при помощи команд rm и rmdir согласно варианту задания.

  -Удалить файл teddiursa5\
  -Удалить файл lab0/shellos1/shuppet\
  -удалить символические ссылки "Copy\_\*"\
  -удалить жесткие ссылки lab0/shellos1/shuppetslug\*\
  -Удалить директорию shellos1\
  -Удалить директорию lab0/trapinch0/victreebel\


#pagebreak()

= Ход решения

== Итоговые скрипты

#set text(size: 9pt)
=== 1.sh
```sh
cd ~
mkdir lab0
cd lab0

echo "Способности Thunder Wave Twister Dragon Rage Slam Agility
Dragon Tail Aqua Tail Dragon Rush Safeguard Dragon Dance Outrage Hyper
Beam" > dragonair5
mkdir mightyena0
mkdir -p ./mightyena0/arcanine
mkdir -p ./mightyena0/camerupt
mkdir -p ./mightyena0/cradily
mkdir -p ./mightyena0/duskull

mkdir shellos1
mkdir -p ./shellos1/butterfree
mkdir -p ./shellos1/pupitar
mkdir -p ./shellos1/bellossom
mkdir -p ./shellos1/bagon
mkdir -p ./shellos1/magmortar
echo "Ходы Body Slam Dark Pulse Double-Edge Foul Play Icy
Wind Knock Oft Magic Coat Magic Room Ominous Wind Pain Split Role Play
Trick" > ./shellos1/shuppet

echo "satk=7 sdef=4 spd=2" > slugma6
echo "Ходы Body Slam Covet#
Defense Curl Dynamicpunch Fake Tears‡ Fire Punch Focus Punch Fury
Cutter Gunk Shot Hyper Voice Ice Punch Last Resort Mega Kick Mega
Punch Metronome Mud-Slap Rollout Seed Bomb Sleep Talk Snore Superpower
Swift Thunderpunch" > teddiursa5

mkdir trapinch0
mkdir -p ./trapinch0/victreebel
echo "Возможности Overland=8 Surface=4 Jump=3
Power=3 Intelligence=4" > ./trapinch0/breloom
mkdir -p ./trapinch0/vanillish
echo "Развитые способности Sand Veil" > ./trapinch0/cofagrigus
```

=== 2.sh
```sh
chmod 604 lab0/dragonair5 

chmod u=wx,g=rwx,o=rx lab0/mightyena0
chmod 737 lab0/mightyena0/arcanine
chmod 373 lab0/mightyena0/camerupt
chmod u=rx,g=x,o=wx lab0/mightyena0/cradily
chmod 307 lab0/mightyena0/duskull

chmod 315 lab0/shellos1
chmod u=rwx,g=rwx,o=rwx lab0/shellos1/butterfree
chmod 355 lab0/shellos1/pupitar
chmod u=wx,g=wx,o=rx lab0/shellos1/bellossom
chmod 700 lab0/shellos1/bagon
chmod 620 lab0/shellos1/shuppet
chmod 305 lab0/shellos1/magmortar

chmod 660 lab0/slugma6
chmod 666 lab0/teddiursa5

chmod 755 lab0/trapinch0
chmod 550 lab0/trapinch0/victreebel
chmod 006 lab0/trapinch0/breloom
chmod 573 lab0/trapinch0/vanillish
chmod 440 lab0/trapinch0/cofagrigus

```

#pagebreak()

=== 3.sh (с исправлением прав через chmod)

```sh
ln lab0/slugma6 lab0/shellos1/shuppetslugma
cp lab0/teddiursa5 lab0/mightyena0/cradily
ln -s lab0/teddiursa5 lab0/shellos1/shuppetteddiursa
ln -s lab0/trapinch0 lab0/Copy_65
cat lab0/slugma6 > lab0/shellos1/shuppetslugma
cat lab0/shellos1/shuppet lab0/shellos1/shuppet > lab0/teddiursa5_58

chmod u+r lab0/mightyena0/camerupt
chmod u+r lab0/mightyena0/duskull

cp -R lab0/mightyena0 lab0/shellos1/magmortar

chmod u-r lab0/mightyena0/camerupt
chmod u-r lab0/mightyena0/duskull
```

=== 4.sh (c исправлением прав доступа)

```sh
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
```

=== 5.sh (с исправлением прав доступа)

```sh
rm lab0/teddiursa5

rm lab0/shellos1/shuppet

rm lab0/Copy_*

chmod u+r lab0/shellos1/
rm lab0/shellos1/shuppetslug*

chmod -R u+rw lab0/shellos1
rm -rf lab0/shellos1/

rmdir lab0/trapinch0/victreebel/
# Возвращать права обратно уже нечему (skip)
```

#pagebreak()

#set text(size: 12pt)
== Результат выполнения команд

=== Вывод ```sh ls -lR lab0/``` после выполнение 3.sh
#figure(
  image(".typ_pic/1.png", height: 94%),
)

=== Вывод 3.sh до исправления прав доступа
#figure(
  image(".typ_pic/2.png", width: 70%),
)

=== Вывод 4.sh до и после исправления

#figure(
  image(".typ_pic/3.png", width: 85%),
)
#figure(
  image(".typ_pic/Screenshot From 2025-09-16 18-15-14.png", width: 85%),
)

=== Вывод 5.sh до исправления
#figure(
  image(".typ_pic/4.png"),
)

#pagebreak()

= Вывод
В ходе этой лабораторной работы я познакомился с командами Unix os, научился их применять такие команды как `mkdir, echo, cat, touch, ls, pwd, cd, more, cp, rm, rmdir, mv` и др. Также я узнал об организации потоков ввода-вывода `stdin, stdout и stderr` и научился перенаправлять вывод из одного потока в другой и ещё научился управлять правами пользователей на доступ к файлам и директориям.
