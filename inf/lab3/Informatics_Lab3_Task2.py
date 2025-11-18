# Author = Gerasiuto Fadey Aleksandrovich
# Group = P3119
# Date = 21.10.2025

import re

tests = [
    "students.spam@yandex.ru dfasdlalskbn",
    "students_spam08@dev.yandex.ru",
    "example@example", 
    "students.spam@y_andex.ru",
    "@yandex.ru",
]

for test in tests:
    reg = r"^[\w\.]+@([a-zA-Z]+(?:\.[a-zA-Z]+)+)$"
    res = re.search(reg, test)
    print(res.group(1) if res else "Fail!")

