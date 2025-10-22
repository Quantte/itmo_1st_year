# Author = Gerasiuto Fadey Aleksandrovich
# Group = P3119
# Date = 21.10.2025

import re

tests = [
    "students.spam@yandex.ru",
    "students.spam@dev.yandex_beta.ru",
    "example@example", 
    "students.spam@@y_andex.ru.",
    "@yandex.ru",
]

for test in tests:
    reg = r"\w@\w+(\.\w+)+$"
    res = re.search(reg, test)
    print(res.group()[2:] if res else "Fail!")

